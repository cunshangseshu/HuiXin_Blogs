package com.huixin.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

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
