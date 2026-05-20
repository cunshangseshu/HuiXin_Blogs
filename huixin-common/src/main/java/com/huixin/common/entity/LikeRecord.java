package com.huixin.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 点赞记录实体类
 * <p>
 * 对应点赞记录表（like_record），用于唯一约束防止重复点赞。
 * 不继承BaseEntity（无逻辑删除等需求）。
 * </p>
 *
 * @author Huixin Blog
 */
@Data
@TableName("like_record")
@Schema(description = "点赞记录")
public class LikeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID（自增）
     */
    @TableField("id")
    @Schema(description = "记录ID")
    private Long id;

    /**
     * 点赞用户ID
     */
    @TableField("user_id")
    @Schema(description = "点赞用户ID", example = "2")
    private Long userId;

    /**
     * 被点赞文章ID
     */
    @TableField("article_id")
    @Schema(description = "被点赞文章ID", example = "1")
    private Long articleId;

}
