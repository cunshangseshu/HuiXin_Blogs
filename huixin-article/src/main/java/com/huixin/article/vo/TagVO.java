package com.huixin.article.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 标签VO
 *
 * @author Huixin Blog
 */
@Data
@Builder
@Schema(description = "标签信息")
public class TagVO {

    @Schema(description = "标签ID")
    private Long id;

    @Schema(description = "标签名称", example = "Java")
    private String tagName;

    @Schema(description = "标签颜色", example = "#B07219")
    private String tagColor;

}
