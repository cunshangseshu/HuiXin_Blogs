package com.huixin.comment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论VO（含回复列表）
 *
 * @author 爱吃罗氏虾
 */
@Data
@Builder
@Schema(description = "评论信息")
public class CommentVO {

    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "文章ID")
    private Long articleId;

    @Schema(description = "评论者用户ID")
    private Long userId;

    @Schema(description = "评论者用户名")
    private String username;

    @Schema(description = "评论者头像")
    private String avatarUrl;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "父评论ID")
    private Long parentId;

    @Schema(description = "被回复的用户ID")
    private Long replyToUserId;

    @Schema(description = "被回复的用户名")
    private String replyToUsername;

    @Schema(description = "子回复列表")
    private List<CommentVO> replies;

    @Schema(description = "评论时间")
    private LocalDateTime createTime;

}
