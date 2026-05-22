package com.huixin.common.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存工具类
 * <p>
 * 封装常用的Redis操作方法，简化业务代码。
 * </p>
 *
 * @author 爱吃罗氏虾
 */
@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取底层RedisTemplate（供需要原生操作的场景使用）
     */
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    /* ==================== String操作 ==================== */

    /**
     * 设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置缓存（带过期时间）
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @return 缓存值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取缓存并转为指定类型
     *
     * @param key   缓存键
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 缓存值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return (T) value;
    }

    /**
     * 自增（用于计数场景）
     *
     * @param key 缓存键
     * @return 自增后的值
     */
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 自增指定数量
     *
     * @param key   缓存键
     * @param delta 增量（可为负数）
     * @return 自增后的值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /* ==================== 通用操作 ==================== */

    /**
     * 判断Key是否存在
     *
     * @param key 缓存键
     * @return true-存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置过期时间
     *
     * @param key      缓存键
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public void expire(String key, long timeout, TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }

    /* ==================== Set操作 ==================== */

    /**
     * 向Set中添加元素
     *
     * @param key    Set键
     * @param values 元素值
     * @return 添加成功的数量
     */
    public Long setAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 从Set中移除元素
     *
     * @param key    Set键
     * @param values 元素值
     * @return 移除成功的数量
     */
    public Long setRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 判断元素是否在Set中
     *
     * @param key   Set键
     * @param value 元素值
     * @return true-存在
     */
    public boolean setIsMember(String key, Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }

    /**
     * 获取Set大小
     *
     * @param key Set键
     * @return 元素数量
     */
    public Long setSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /* ==================== ZSet操作 ==================== */

    /**
     * 向ZSet中添加元素
     *
     * @param key   ZSet键
     * @param value 元素值
     * @param score 分数
     */
    public void zSetAdd(String key, Object value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * ZSet分数增加
     *
     * @param key   ZSet键
     * @param value 元素值
     * @param delta 增量
     */
    public void zSetIncrementScore(String key, Object value, double delta) {
        redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

}
