package com.huixin.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章分类实体类
 * <p>
 * 对应分类表（category）。
 * </p>
 *
 * @author Huixin Blog
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
@Schema(description = "文章分类")
public class Category extends BaseEntity {

    /**
     * 分类名称
     */
    @TableField("category_name")
    @Schema(description = "分类名称", example = "技术分享")
    private String categoryName;

    /**
     * 分类描述
     */
    @TableField("category_desc")
    @Schema(description = "分类描述", example = "技术文章、编程经验")
    private String categoryDesc;

    /**
     * 排序序号（越小越靠前）
     */
    @TableField("sort_order")
    @Schema(description = "排序序号", example = "1")
    private Integer sortOrder;

    /**
     * 该分类下文章数量（冗余字段，方便查询）
     */
    @TableField("article_count")
    @Schema(description = "文章数量", example = "25")
    private Integer articleCount;

}
