package com.huixin.stats.service.impl;

import com.huixin.common.utils.RedisUtil;
import com.huixin.stats.service.StatsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
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
 * @author 爱吃罗氏虾
 */
@Slf4j
@Service
public class StatsServiceImpl implements StatsService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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
     * 点赞/取消点赞（toggle模式，Lua 脚本原子化）
     * <p>
     * 使用 Redis Lua 脚本保证 SISMEMBER + SADD/SREM + INCRBY 的原子性，
     * 避免并发竞态导致重复计数。
     * </p>
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> toggleLike(Long articleId, Long userId) {
        String usersKey = LIKE_USERS_KEY + articleId;
        String countKey = LIKE_COUNT_KEY + articleId;
        String member = userId.toString();
        String articleIdStr = articleId.toString();

        // Lua 脚本：原子化 toggle 点赞
        String luaScript =
            "local is_member = redis.call('SISMEMBER', KEYS[1], ARGV[1]) " +
            "if is_member == 1 then " +
            "  redis.call('SREM', KEYS[1], ARGV[1]) " +
            "  local count = redis.call('INCRBY', KEYS[2], -1) " +
            "  if count < 0 then redis.call('SET', KEYS[2], 0); count = 0 end " +
            "  redis.call('ZINCRBY', KEYS[3], -ARGV[2], ARGV[3]) " +
            "  return {0, count} " +  // 0 = 取消点赞
            "else " +
            "  redis.call('SADD', KEYS[1], ARGV[1]) " +
            "  local count = redis.call('INCRBY', KEYS[2], 1) " +
            "  redis.call('ZINCRBY', KEYS[3], ARGV[2], ARGV[3]) " +
            "  return {1, count} " +  // 1 = 点赞
            "end";

        DefaultRedisScript<List> script = new DefaultRedisScript<>(luaScript, List.class);
        List<String> keys = Arrays.asList(usersKey, countKey, HOT_RANK_KEY);
        List<Long> result = redisTemplate.execute(script, keys,
                member, (long) LIKE_WEIGHT, articleIdStr);

        boolean liked = result != null && !result.isEmpty() && result.get(0) == 1L;
        long newCount = result != null && result.size() > 1 ? result.get(1) : 0L;

        Map<String, Object> map = new HashMap<>();
        map.put("liked", liked);
        map.put("likeCount", newCount);
        log.debug("[{}] articleId={}, userId={}, likeCount={}",
                liked ? "点赞成功" : "取消点赞", articleId, userId, newCount);
        return map;
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
