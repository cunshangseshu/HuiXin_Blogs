package com.huixin.stats.service.impl;

import com.huixin.common.utils.RedisUtil;
import com.huixin.stats.service.StatsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 * <p>
 * 核心使用Redis的数据结构：
 * - String INCR：文章阅读量原子自增
 * - Set SADD/SREM/SISMEMBER：用户点赞记录，O(1)判重
 * - ZSet ZINCRBY：热门文章排行，按热度分排序
 * </p>
 * <p>
 * 参考 Redis Skill 最佳实践：
 * - 使用 INCR 而非 GET+SET，保证原子性
 * - 使用 Set 结构存储点赞用户，SISMEMBER O(1)判重点
 * - ZSet 做排行榜，天然有序
 * </p>
 *
 * @author Huixin Blog
 */
@Slf4j
@Service
public class StatsServiceImpl implements StatsService {

    @Resource
    private RedisUtil redisUtil;

    /** Redis Key 前缀 */
    private static final String VIEW_COUNT_KEY = "article:views:";
    private static final String LIKE_COUNT_KEY = "article:likes:";
    private static final String LIKE_USERS_KEY = "article:like_users:";
    private static final String HOT_RANK_KEY  = "article:hot:rank";

    private static final double VIEW_WEIGHT  = 1.0;  // 阅读权重
    private static final double LIKE_WEIGHT  = 3.0;  // 点赞权重

    /* ==================== 阅读量 ==================== */

    /**
     * 记录文章阅读
     * <p>
     * 使用 Redis INCR 原子自增，参考 Redis Skill：
     * "GET then SET is not atomic—use INCR, SETNX, or Lua"
     * 单次 INCR 是原子操作，无需担心并发问题。
     * </p>
     */
    @Override
    public Long recordView(Long articleId) {
        String key = VIEW_COUNT_KEY + articleId;
        Long count = redisUtil.increment(key);

        // 更新热门排行ZSet
        redisUtil.zSetIncrementScore(HOT_RANK_KEY, articleId.toString(), VIEW_WEIGHT);

        log.debug("[阅读量+1] articleId={}, viewCount={}", articleId, count);
        return count;
    }

    /* ==================== 点赞 ==================== */

    /**
     * 点赞/取消点赞（toggle模式）
     * <p>
     * 使用 Redis Set 存储每个文章的点赞用户集合：
     * - SADD 添加用户 → 返回1表示首次点赞 → INCR点赞数
     * - SREM 移除用户 → 返回1表示取消点赞 → DECR点赞数
     * <p>
     * 参考 Redis Skill：
     * "SETNX for locks, SADD returns count of added elements"
     * SADD 返回值天然支持幂等——重复点赞返回0。
     * </p>
     */
    @Override
    public Map<String, Object> toggleLike(Long articleId, Long userId) {
        String usersKey = LIKE_USERS_KEY + articleId;
        String countKey = LIKE_COUNT_KEY + articleId;

        // 判断是否已点赞（O(1)复杂度）
        boolean alreadyLiked = redisUtil.setIsMember(usersKey, userId.toString());

        Map<String, Object> result = new HashMap<>();

        if (alreadyLiked) {
            // 取消点赞：SREM + DECR
            redisUtil.setRemove(usersKey, userId.toString());
            Long newCount = redisUtil.increment(countKey, -1);
            // 确保不低于0
            if (newCount < 0) {
                redisUtil.set(countKey, "0");
                newCount = 0L;
            }
            redisUtil.zSetIncrementScore(HOT_RANK_KEY, articleId.toString(), -LIKE_WEIGHT);

            result.put("liked", false);
            result.put("likeCount", newCount);
            log.debug("[取消点赞] articleId={}, userId={}, likeCount={}", articleId, userId, newCount);
        } else {
            // 点赞：SADD + INCR
            redisUtil.setAdd(usersKey, userId.toString());
            Long newCount = redisUtil.increment(countKey);
            redisUtil.zSetIncrementScore(HOT_RANK_KEY, articleId.toString(), LIKE_WEIGHT);

            result.put("liked", true);
            result.put("likeCount", newCount);
            log.debug("[点赞成功] articleId={}, userId={}, likeCount={}", articleId, userId, newCount);
        }

        return result;
    }

    /* ==================== 统计查询 ==================== */

    @Override
    public Map<String, Object> getArticleStats(Long articleId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("viewCount", getViewCount(articleId));
        stats.put("likeCount", getLikeCount(articleId));
        return stats;
    }

    @Override
    public boolean hasLiked(Long articleId, Long userId) {
        return redisUtil.setIsMember(LIKE_USERS_KEY + articleId, userId.toString());
    }

    /* ==================== 热门排行 ==================== */

    /**
     * 获取热门文章排行
     * <p>
     * 使用 Redis ZSet 的 ZREVRANGE 按分数倒序。
     * 热度分 = 阅读量×1 + 点赞数×3。
     * </p>
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getHotArticles(int limit) {
        // 使用 RedisTemplate 的原生 ZSet 操作获取倒序排行
        Set<Object> topSet = redisUtil.getRedisTemplate()
                .opsForZSet()
                .reverseRange(HOT_RANK_KEY, 0, limit - 1);

        if (topSet == null || topSet.isEmpty()) {
            return Collections.emptyList();
        }

        return topSet.stream().map(articleId -> {
            Map<String, Object> item = new HashMap<>();
            item.put("articleId", Long.valueOf(articleId.toString()));
            Double score = redisUtil.getRedisTemplate().opsForZSet().score(HOT_RANK_KEY, articleId);
            item.put("hotScore", score != null ? score.longValue() : 0L);
            return item;
        }).collect(Collectors.toList());
    }

    /* ==================== 内部方法 ==================== */

    private Long getViewCount(Long articleId) {
        Object val = redisUtil.get(VIEW_COUNT_KEY + articleId);
        if (val == null) return 0L;
        return Long.valueOf(val.toString());
    }

    private Long getLikeCount(Long articleId) {
        Object val = redisUtil.get(LIKE_COUNT_KEY + articleId);
        if (val == null) return 0L;
        return Long.valueOf(val.toString());
    }

}
