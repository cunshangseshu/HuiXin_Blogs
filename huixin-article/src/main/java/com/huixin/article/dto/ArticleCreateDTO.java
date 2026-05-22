package com.huixin.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建文章请求DTO
 *
 * @author 爱吃罗氏虾
 */
@Data
@Schema(description = "创建文章请求")
public class ArticleCreateDTO {

    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "标题不能超过200字符")
    @Schema(description = "文章标题", example = "Spring Boot 3 快速入门")
    private String title;

    @Size(max = 500, message = "摘要不能超过500字符")
    @Schema(description = "文章摘要", example = "本文介绍Spring Boot 3的核心特性...")
    private String summary;

    @NotBlank(message = "文章内容不能为空")
    @Schema(description = "文章正文（Markdown格式）")
    private String content;

    @Schema(description = "文章正文（HTML，可选，不传则后端转换）")
    private String contentHtml;

    @Schema(description = "封面图片URL")
    private String coverImageUrl;

    @NotNull(message = "请选择分类")
    @Schema(description = "所属分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "标签ID列表")
    private List<Long> tagIds;

    @Schema(description = "是否原创：0-转载，1-原创", example = "1")
    private Integer isOriginal = 1;

}
