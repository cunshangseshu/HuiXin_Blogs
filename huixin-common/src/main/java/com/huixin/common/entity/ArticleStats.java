package com.huixin.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 文章统计实体类
 * <p>
 * 对应文章统计表（article_stats），与Redis配合使用。
 * 实时数据（今日阅读量）存Redis，累计数据定时同步到本表。
 * </p>
 *
 * @author Huixin Blog
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article_stats")
@Schema(description = "文章统计数据")
public class ArticleStats extends BaseEntity {

    /**
     * 文章ID
     */
    @TableField("article_id")
    @Schema(description = "文章ID", example = "1")
    private Long articleId;

    /**
     * 累计阅读量
     */
    @TableField("total_view_count")
    @Schema(description = "累计阅读量", example = "1024")
    private Integer totalViewCount;

    /**
     * 累计点赞数
     */
    @TableField("total_like_count")
    @Schema(description = "累计点赞数", example = "128")
    private Integer totalLikeCount;

    /**
     * 累计评论数
     */
    @TableField("total_comment_count")
    @Schema(description = "累计评论数", example = "32")
    private Integer totalCommentCount;

    /**
     * 今日阅读量
     */
    @TableField("daily_view_count")
    @Schema(description = "今日阅读量", example = "56")
    private Integer dailyViewCount;

    /**
     * 今日阅读量统计日期（用于跨天重置）
     */
    @TableField("daily_view_date")
    @Schema(description = "统计日期")
    private LocalDate dailyViewDate;

}
