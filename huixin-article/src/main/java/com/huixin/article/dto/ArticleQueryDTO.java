package com.huixin.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 文章列表查询DTO
 *
 * @author Huixin Blog
 */
@Data
@Schema(description = "文章列表查询参数")
public class ArticleQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页数量", example = "10")
    private Integer size = 10;

    @Schema(description = "分类ID筛选")
    private Long categoryId;

    @Schema(description = "标签ID筛选")
    private Long tagId;

    @Schema(description = "作者ID筛选")
    private Long authorId;

    @Schema(description = "排序方式：latest(最新)/hot(最热)", example = "latest")
    private String sort = "latest";

    @Schema(description = "搜索关键词")
    private String keyword;

}
