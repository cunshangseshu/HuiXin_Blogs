package com.huixin.comment.service;

import com.huixin.comment.dto.CommentCreateDTO;
import com.huixin.comment.vo.CommentVO;

import java.util.List;

/**
 * 评论服务接口
 *
 * @author Huixin Blog
 */
public interface CommentService {

    /**
     * 发表评论（或回复）
     *
     * @param userId 评论者用户ID
     * @param dto    评论内容
     * @return 评论VO
     */
    CommentVO createComment(Long userId, CommentCreateDTO dto);

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param userId    操作用户ID（评论者本人 或 文章作者）
     */
    void deleteComment(Long commentId, Long userId);

    /**
     * 获取文章评论列表（含嵌套回复）
     *
     * @param articleId 文章ID
     * @return 评论树
     */
    List<CommentVO> getArticleComments(Long articleId);

}
