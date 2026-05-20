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
 * <p>
 * 支持一级评论和二级回复（嵌套一层）。
 * 评论树结构：一级评论 → 其下挂载所有二级回复。
 * </p>
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
        // 1. 验证文章存在
        try {
            articleFeignClient.getArticleDetail(dto.getArticleId());
        } catch (Exception e) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }

        // 2. 如果是回复，验证父评论存在
        if (dto.getParentId() != null) {
            Comment parent = commentMapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException(ResultCode.COMMENT_NOT_FOUND);
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

        log.info("[评论发表] commentId={}, articleId={}, userId={}", comment.getId(), dto.getArticleId(), userId);

        // 4. 返回构建好的VO
        return buildCommentVO(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(ResultCode.COMMENT_NOT_FOUND);
        }

        // 权限判断：评论者本人可以删除
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        commentMapper.deleteById(commentId);
        log.info("[评论删除] commentId={}, userId={}", commentId, userId);
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

        // 收集所有评论者ID，批量获取用户信息（简化：逐条查询）
        Set<Long> userIds = allComments.stream()
                .map(Comment::getUserId)
                .collect(Collectors.toSet());
        Map<Long, Map<String, Object>> userCache = new HashMap<>();
        for (Long uid : userIds) {
            try {
                ResultVO<Map<String, Object>> result = userFeignClient.getUserPublicInfo(uid);
                if (result.getCode() == 200 && result.getData() != null) {
                    userCache.put(uid, result.getData());
                }
            } catch (Exception ignored) {}
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
            replyToUsername = replyUser != null ? String.valueOf(replyUser.getOrDefault("nickname",
                    replyUser.getOrDefault("username", ""))) : null;
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
                        rtn = rtu != null ? String.valueOf(rtu.getOrDefault("nickname",
                                rtu.getOrDefault("username", ""))) : null;
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
