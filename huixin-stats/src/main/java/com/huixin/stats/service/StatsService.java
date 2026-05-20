package com.huixin.stats.service;

import java.util.List;
import java.util.Map;

/**
 * 统计服务接口
 *
 * @author Huixin Blog
 */
public interface StatsService {

    /**
     * 记录文章阅读（Redis INCR原子自增）
     *
     * @param articleId 文章ID
     * @return 最新阅读量
     */
    Long recordView(Long articleId);

    /**
     * 点赞/取消点赞（Redis Set + INCR原子操作）
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return Map: {liked: true/false, likeCount: N}
     */
    Map<String, Object> toggleLike(Long articleId, Long userId);

    /**
     * 获取文章统计数据
     *
     * @param articleId 文章ID
     * @return {viewCount, likeCount, commentCount}
     */
    Map<String, Object> getArticleStats(Long articleId);

    /**
     * 获取热门文章排行（Redis ZSet）
     *
     * @param limit 返回数量
     * @return [{articleId, score}, ...]
     */
    List<Map<String, Object>> getHotArticles(int limit);

    /**
     * 检查用户是否已点赞某文章
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return true-已点赞
     */
    boolean hasLiked(Long articleId, Long userId);

}
