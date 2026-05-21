package com.huixin.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 点赞记录实体
 * <p>
 * 注意：该实体不继承BaseEntity。点赞是一种轻量级的 toggle 操作（点赞时 INSERT，取消时物理 DELETE），
 * 无需逻辑删除和时间戳自动填充。点赞记录通过 Redis Set 实时管理，MySQL 仅用作异步同步备份。
 * </p>
 */
@Data
@TableName("like_record")
@Schema(description = "点赞记录")
public class LikeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "记录ID")
    private Long id;

    @TableField("user_id")
    @Schema(description = "点赞用户ID", example = "2")
    private Long userId;

    @TableField("article_id")
    @Schema(description = "被点赞文章ID", example = "1")
    private Long articleId;

}
