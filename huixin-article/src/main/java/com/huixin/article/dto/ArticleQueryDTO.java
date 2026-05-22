package com.huixin.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

/**
 * 文章列表查询DTO
 *
 * @author 爱吃罗氏虾
 */
@Data
@Schema(description = "文章列表查询参数")
public class ArticleQueryDTO {

    @Min(value = 1, message = "页码最小为1")
    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Min(value = 1, message = "每页数量最小为1")
    @Max(value = 100, message = "每页数量最大为100")
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
