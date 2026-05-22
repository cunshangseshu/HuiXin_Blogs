package com.huixin.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签实体类
 * <p>
 * 对应标签表（tag），与文章为多对多关系。
 * </p>
 *
 * @author 爱吃罗氏虾
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tag")
@Schema(description = "文章标签")
public class Tag extends BaseEntity {

    /**
     * 标签名称
     */
    @TableField("tag_name")
    @Schema(description = "标签名称", example = "Java")
    private String tagName;

    /**
     * 标签展示颜色（十六进制色值）
     */
    @TableField("tag_color")
    @Schema(description = "标签颜色", example = "#B07219")
    private String tagColor;

    /**
     * 该标签下文章数量（冗余字段，方便查询）
     */
    @TableField("article_count")
    @Schema(description = "文章数量", example = "15")
    private Integer articleCount;

}
