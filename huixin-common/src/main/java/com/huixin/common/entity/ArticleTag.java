package com.huixin.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("article_tag")
@Schema(description = "文章-标签关联")
public class ArticleTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "关联ID")
    private Long id;

    @TableField("article_id")
    @Schema(description = "文章ID", example = "1")
    private Long articleId;

    @TableField("tag_id")
    @Schema(description = "标签ID", example = "3")
    private Long tagId;

}
