package com.huixin.common.utils;

import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存工具类（含柔性降级版）
 * <p>
 * 封装常用的Redis操作方法，增加 try-catch 拦截机制。
 * 当 Redis 产生如超载、掉线等异常时，将会温和截断并返回 null。
 * 从而保障上层调用业务逻辑能够顺畅过渡至 MySQL 灾备查询，保证系统高可用性。
 * </p>
 *
 * @author 爱吃罗氏虾
 */
@Slf4j
@Getter
@Component
public class RedisUtil {

    /**
     * -- GETTER --
     *  获取底层RedisTemplate（供需要原生操作的场景使用）
     */
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /* ==================== String操作 ==================== */

    /**
     * 设置缓存（带有异常穿透保护）
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("[Redis降级警告] 设置缓存失败, key: {}", key, e);
        }
    }

    /**
     * 设置缓存（带过期时间 / 附带异常保护）
     */
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        } catch (Exception e) {
            log.error("[Redis降级警告] 设置有过期时间的缓存失败, key: {}", key, e);
        }
    }

    /**
     * 获取缓存
     *
     * 【核心护盾】：抛错时截断错误，返回 null 去欺骗业务层直接请求 MySQL！
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("[Redis降级警告] 获取缓存异常阻断，将返回 null 击穿至 DB 进行灾备查库, key: {}", key);
            return null;
        }
    }

    /**
     * 获取缓存并强转为指定泛型类型
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            return (T) value;
        } catch (Exception e) {
            log.error("[Redis降级警告] 强类型泛型获取缓存失败, key: {}", key);
            return null;
        }
    }

    /**
     * 自增（用于计数场景 / 防爆）
     */
    public Long increment(String key) {
        try {
            return redisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            log.error("[Redis降级警告] 缓存自增操作失败, key: {}", key);
            return null;
        }
    }

    /**
     * 自增指定数量
     */
    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("[Redis降级警告] 缓存自增步长操作失败, key: {}", key);
            return null;
        }
    }

    /**
     * 删除指定缓存键
     */
    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("[Redis降级警告] 缓存删除失败, key: {}", key);
            return false;
        }
    }

    /* ==================== 额外扩展 (ZSet, Set等) ==================== */

    /**
     * ZSet 增量更新（用于排行榜和热词），带有防熔断 try-catch
     */
    public void zSetIncrementScore(String key, String value, double delta) {
        try {
            redisTemplate.opsForZSet().incrementScore(key, value, delta);
        } catch (Exception e) {
            log.error("[Redis降级警告] ZSet 增量分数失败, key: {}, value: {}", key, value, e);
        }
    }

    /**
     * Set 判断是否存在（由于防穿透机制，出错统一返回 false 让业务妥协）
     */
    public boolean setIsMember(String key, String value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            log.error("[Redis降级警告] Set 检查存在性失败, key: {}, value: {}", key, value, e);
            return false;
        }
    }
}
