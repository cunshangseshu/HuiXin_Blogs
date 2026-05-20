package com.huixin.common.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * <p>
 * 配置RedisTemplate的序列化方式：
 * - Key使用String序列化，便于阅读和调试
 * - Value使用JSON序列化，支持对象存取
 * </p>
 * <p>
 * 启用Spring Cache注解支持（@Cacheable、@CacheEvict、@CachePut）
 * </p>
 *
 * @author Huixin Blog
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 自定义RedisTemplate，设置序列化方式
     *
     * @param connectionFactory Redis连接工厂
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key使用String序列化
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value使用JSON序列化
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

}
