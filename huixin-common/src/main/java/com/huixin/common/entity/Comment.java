package com.huixin.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论实体类
 * <p>
 * 对应评论表（comment），支持二级回复。
 * parentId = NULL 表示一级评论，非NULL 表示回复某条评论。
 * </p>
 *
 * @author Huixin Blog
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("comment")
@Schema(description = "文章评论")
public class Comment extends BaseEntity {

    /**
     * 所属文章ID
     */
    @TableField("article_id")
    @Schema(description = "所属文章ID", example = "1")
    private Long articleId;

    /**
     * 评论者用户ID
     */
    @TableField("user_id")
    @Schema(description = "评论者用户ID", example = "2")
    private Long userId;

    /**
     * 父评论ID（NULL表示一级评论，非NULL表示二级回复）
     */
    @TableField("parent_id")
    @Schema(description = "父评论ID", example = "null")
    private Long parentId;

    /**
     * 被回复的用户ID（仅二级回复时使用）
     */
    @TableField("reply_to_user_id")
    @Schema(description = "被回复的用户ID", example = "1")
    private Long replyToUserId;

    /**
     * 评论内容
     */
    @TableField("content")
    @Schema(description = "评论内容", example = "写得非常好，受益匪浅！")
    private String content;

}
