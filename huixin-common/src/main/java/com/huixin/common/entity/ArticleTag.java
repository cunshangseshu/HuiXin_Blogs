package com.huixin.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 文章-标签关联实体类
 * <p>
 * 对应文章标签关联表（article_tag），维护文章与标签的多对多关系。
 * </p>
 *
 * @author Huixin Blog
 */
@Data
@TableName("article_tag")
@Schema(description = "文章-标签关联")
public class ArticleTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID（自增）
     */
    @TableField("id")
    @Schema(description = "关联ID")
    private Long id;

    /**
     * 文章ID
     */
    @TableField("article_id")
    @Schema(description = "文章ID", example = "1")
    private Long articleId;

    /**
     * 标签ID
     */
    @TableField("tag_id")
    @Schema(description = "标签ID", example = "3")
    private Long tagId;

}
