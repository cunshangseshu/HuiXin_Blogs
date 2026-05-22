package com.huixin.article.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章详情VO（含作者信息）
 *
 * @author 爱吃罗氏虾
 */
@Data
@Builder
@Schema(description = "文章详情")
public class ArticleVO {

    @Schema(description = "文章ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "正文（Markdown）")
    private String content;

    @Schema(description = "正文（HTML）")
    private String contentHtml;

    @Schema(description = "封面图URL")
    private String coverImageUrl;

    @Schema(description = "作者用户ID")
    private Long authorId;

    @Schema(description = "作者用户名")
    private String authorName;

    @Schema(description = "作者头像")
    private String authorAvatar;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "标签列表")
    private List<TagVO> tags;

    @Schema(description = "阅读量")
    private Integer viewCount;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "是否置顶")
    private Integer isTop;

    @Schema(description = "是否原创")
    private Integer isOriginal;

}
