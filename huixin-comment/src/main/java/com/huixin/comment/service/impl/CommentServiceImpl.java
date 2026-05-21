package com.huixin.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huixin.comment.dto.CommentCreateDTO;
import com.huixin.comment.feign.ArticleFeignClient;
import com.huixin.comment.feign.UserFeignClient;
import com.huixin.comment.mapper.CommentMapper;
import com.huixin.comment.service.CommentService;
import com.huixin.comment.vo.CommentVO;
import com.huixin.common.entity.Comment;
import com.huixin.common.enums.ResultCode;
import com.huixin.common.exception.BusinessException;
import com.huixin.common.vo.ResultVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 *
 * @author Huixin Blog
 */
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;
    @Resource
    private ArticleFeignClient articleFeignClient;
    @Resource
    private UserFeignClient userFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentVO createComment(Long userId, CommentCreateDTO dto) {
        // 1. 验证文章存在（轻量级检查）
        try {
            ResultVO<Boolean> exists = articleFeignClient.checkArticleExists(dto.getArticleId());
            if (exists == null || exists.getData() == null || !exists.getData()) {
                throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("[验证文章存在性失败] articleId={}, error={}", dto.getArticleId(), e.getMessage());
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }

        // 2. 如果是回复，验证父评论存在且属于同一文章
        if (dto.getParentId() != null) {
            Comment parent = commentMapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException(ResultCode.COMMENT_NOT_FOUND);
            }
            // 父评论必须属于同一文章，防止跨文章回复
            if (!parent.getArticleId().equals(dto.getArticleId())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "回复的评论不属于该文章");
            }
        }

        // 3. 保存评论
        Comment comment = new Comment();
        comment.setArticleId(dto.getArticleId());
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        comment.setParentId(dto.getParentId());
        comment.setReplyToUserId(dto.getReplyToUserId());

        commentMapper.insert(comment);

        // 4. 更新文章评论计数
        try {
            articleFeignClient.incrementCommentCount(dto.getArticleId(), 1);
        } catch (Exception e) {
            log.warn("[更新文章评论计数失败] articleId={}", dto.getArticleId());
        }

        log.info("[评论发表] commentId={}, articleId={}, userId={}", comment.getId(), dto.getArticleId(), userId);

        // 5. 返回构建好的VO
        return buildCommentVO(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(ResultCode.COMMENT_NOT_FOUND);
        }

        // 权限判断：评论者本人 或 文章作者 可以删除
        boolean isCommentAuthor = comment.getUserId().equals(userId);
        boolean isArticleAuthor = false;
        if (!isCommentAuthor) {
            try {
                ResultVO<Map<String, Object>> articleBasic = articleFeignClient.getArticleBasic(comment.getArticleId());
                if (articleBasic.getCode() == 200 && articleBasic.getData() != null) {
                    Object authorIdObj = articleBasic.getData().get("authorId");
                    if (authorIdObj != null) {
                        isArticleAuthor = ((Number) authorIdObj).longValue() == userId.longValue();
                    }
                }
            } catch (Exception e) {
                log.warn("[获取文章作者信息失败] articleId={}", comment.getArticleId());
            }
        }
        if (!isCommentAuthor && !isArticleAuthor) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        // 级联删除子回复（避免孤立数据）
        List<Comment> childReplies = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>().eq(Comment::getParentId, commentId)
        );
        for (Comment child : childReplies) {
            commentMapper.deleteById(child.getId());
        }

        // 删除评论本身
        commentMapper.deleteById(commentId);

        // 更新文章评论计数
        try {
            int delta = -1 - childReplies.size(); // 父评论 + 子回复
            articleFeignClient.incrementCommentCount(comment.getArticleId(), delta);
        } catch (Exception e) {
            log.warn("[更新文章评论计数失败] articleId={}", comment.getArticleId());
        }

        log.info("[评论删除] commentId={}, userId={}, childReplies={}", commentId, userId, childReplies.size());
    }

    @Override
    public List<CommentVO> getArticleComments(Long articleId) {
        // 查询文章下所有未删除评论，按时间升序
        List<Comment> allComments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getArticleId, articleId)
                        .orderByAsc(Comment::getCreateTime)
        );

        if (allComments.isEmpty()) {
            return Collections.emptyList();
        }

        // 收集所有涉及的用户ID，批量获取用户信息（避免 N+1）
        Set<Long> userIds = new HashSet<>();
        for (Comment c : allComments) {
            userIds.add(c.getUserId());
            if (c.getReplyToUserId() != null) {
                userIds.add(c.getReplyToUserId());
            }
        }
        Map<Long, Map<String, Object>> userCache = new HashMap<>();
        if (!userIds.isEmpty()) {
            try {
                ResultVO<List<Map<String, Object>>> result = userFeignClient.getUsersByIds(new ArrayList<>(userIds));
                if (result.getCode() == 200 && result.getData() != null) {
                    for (Map<String, Object> userData : result.getData()) {
                        Object idObj = userData.get("id");
                        if (idObj != null) {
                            userCache.put(((Number) idObj).longValue(), userData);
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("[批量获取用户信息失败] count={}", userIds.size());
            }
        }

        // 分离一级评论和二级回复
        List<Comment> parents = allComments.stream()
                .filter(c -> c.getParentId() == null)
                .collect(Collectors.toList());
        List<Comment> replies = allComments.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.toList());

        // 按父评论ID分组回复
        Map<Long, List<Comment>> replyMap = replies.stream()
                .collect(Collectors.groupingBy(Comment::getParentId));

        // 构建评论树
        return parents.stream()
                .map(parent -> {
                    List<Comment> children = replyMap.getOrDefault(parent.getId(), Collections.emptyList());
                    return buildCommentTree(parent, children, userCache);
                })
                .collect(Collectors.toList());
    }

    /* ==================== 内部方法 ==================== */

    private CommentVO buildCommentVO(Comment comment) {
        Map<String, Object> userData = getUserData(comment.getUserId());
        String replyToUsername = null;
        if (comment.getReplyToUserId() != null) {
            Map<String, Object> replyUser = getUserData(comment.getReplyToUserId());
            if (replyUser != null) {
                Object nick = replyUser.get("nickname");
                Object uname = replyUser.get("username");
                replyToUsername = nick != null ? nick.toString()
                                : uname != null ? uname.toString() : null;
            }
        }
        return CommentVO.builder()
                .id(comment.getId()).articleId(comment.getArticleId())
                .userId(comment.getUserId())
                .username(getUserField(userData, "nickname", "username"))
                .avatarUrl(getUserField(userData, "avatarUrl"))
                .content(comment.getContent())
                .parentId(comment.getParentId())
                .replyToUserId(comment.getReplyToUserId())
                .replyToUsername(replyToUsername)
                .replies(Collections.emptyList())
                .createTime(comment.getCreateTime())
                .build();
    }

    private CommentVO buildCommentTree(Comment parent, List<Comment> replies, Map<Long, Map<String, Object>> userCache) {
        List<CommentVO> replyVOs = replies.stream()
                .map(r -> {
                    Map<String, Object> ru = userCache.get(r.getUserId());
                    String rtn = null;
                    if (r.getReplyToUserId() != null) {
                        Map<String, Object> rtu = userCache.get(r.getReplyToUserId());
                        if (rtu != null) {
                            Object nick = rtu.get("nickname");
                            Object uname = rtu.get("username");
                            rtn = nick != null ? nick.toString()
                                : uname != null ? uname.toString() : null;
                        }
                    }
                    return CommentVO.builder()
                            .id(r.getId()).articleId(r.getArticleId()).userId(r.getUserId())
                            .username(getUserField(ru, "nickname", "username"))
                            .avatarUrl(getUserField(ru, "avatarUrl"))
                            .content(r.getContent()).parentId(r.getParentId())
                            .replyToUserId(r.getReplyToUserId()).replyToUsername(rtn)
                            .replies(Collections.emptyList()).createTime(r.getCreateTime())
                            .build();
                }).collect(Collectors.toList());

        Map<String, Object> pu = userCache.get(parent.getUserId());
        return CommentVO.builder()
                .id(parent.getId()).articleId(parent.getArticleId()).userId(parent.getUserId())
                .username(getUserField(pu, "nickname", "username"))
                .avatarUrl(getUserField(pu, "avatarUrl"))
                .content(parent.getContent()).parentId(parent.getParentId())
                .replyToUserId(parent.getReplyToUserId()).replyToUsername(null)
                .replies(replyVOs).createTime(parent.getCreateTime())
                .build();
    }

    private Map<String, Object> getUserData(Long userId) {
        try {
            ResultVO<Map<String, Object>> r = userFeignClient.getUserPublicInfo(userId);
            return (r.getCode() == 200 && r.getData() != null) ? r.getData() : null;
        } catch (Exception e) { return null; }
    }

    private String getUserField(Map<String, Object> data, String... keys) {
        if (data == null) return "";
        for (String key : keys) {
            Object val = data.get(key);
            if (val != null && !val.toString().isEmpty()) return val.toString();
        }
        return "";
    }

}
