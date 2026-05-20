package com.huixin.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文章实体类
 * <p>
 * 对应文章表（article），博客的核心数据。
 * 阅读量和点赞数从Redis定期同步更新。
 * </p>
 *
 * @author Huixin Blog
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article")
@Schema(description = "文章信息")
public class Article extends BaseEntity {

    /**
     * 文章标题
     */
    @TableField("title")
    @Schema(description = "文章标题", example = "Spring Boot 3 快速入门")
    private String title;

    /**
     * 文章摘要
     */
    @TableField("summary")
    @Schema(description = "文章摘要", example = "本文介绍Spring Boot 3的核心特性...")
    private String summary;

    /**
     * 文章正文（Markdown格式）
     */
    @TableField("content")
    @Schema(description = "文章正文（Markdown）")
    private String content;

    /**
     * 文章正文（HTML渲染结果，冗余存储以减少前端渲染压力）
     */
    @TableField("content_html")
    @Schema(description = "文章正文（HTML）")
    private String contentHtml;

    /**
     * 封面图片URL
     */
    @TableField("cover_image_url")
    @Schema(description = "封面图片URL")
    private String coverImageUrl;

    /**
     * 作者用户ID
     */
    @TableField("author_id")
    @Schema(description = "作者用户ID", example = "1")
    private Long authorId;

    /**
     * 所属分类ID
     */
    @TableField("category_id")
    @Schema(description = "所属分类ID", example = "1")
    private Long categoryId;

    /**
     * 文章状态：0-草稿，1-已发布
     */
    @TableField("article_status")
    @Schema(description = "文章状态：0-草稿，1-已发布", example = "1")
    private Integer articleStatus;

    /**
     * 阅读量（从Redis定时同步）
     */
    @TableField("view_count")
    @Schema(description = "阅读量", example = "128")
    private Integer viewCount;

    /**
     * 点赞数（从Redis定时同步）
     */
    @TableField("like_count")
    @Schema(description = "点赞数", example = "32")
    private Integer likeCount;

    /**
     * 评论数（发布/删除评论时更新）
     */
    @TableField("comment_count")
    @Schema(description = "评论数", example = "8")
    private Integer commentCount;

    /**
     * 是否置顶：0-否，1-是
     */
    @TableField("is_top")
    @Schema(description = "是否置顶：0-否，1-是", example = "0")
    private Integer isTop;

    /**
     * 是否原创：0-转载，1-原创
     */
    @TableField("is_original")
    @Schema(description = "是否原创：0-转载，1-原创", example = "1")
    private Integer isOriginal;

    /**
     * 发布时间（首次发布时设置）
     */
    @TableField("publish_time")
    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

}
