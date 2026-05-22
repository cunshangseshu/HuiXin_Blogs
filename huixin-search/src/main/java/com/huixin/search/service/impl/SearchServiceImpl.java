package com.huixin.search.service.impl;

import com.huixin.common.utils.RedisUtil;
import com.huixin.common.vo.ResultVO;
import com.huixin.search.feign.ArticleFeignClient;
import com.huixin.search.service.SearchService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 搜索服务实现类
 *
 * @author 爱吃罗氏虾
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    private ArticleFeignClient articleFeignClient;

    @Resource
    private RedisUtil redisUtil;

    private static final String HOT_KEYWORDS_KEY = "search:hot_keywords";

    @Override
    public Map<String, Object> search(String keyword, Integer page, Integer size) {
        // 关键词预处理
        String trimmed = keyword != null ? keyword.trim() : "";

        if (!trimmed.isEmpty()) {
            // 记录搜索热词到Redis ZSet
            redisUtil.zSetIncrementScore(HOT_KEYWORDS_KEY, trimmed, 1.0);
            log.debug("[搜索] keyword={}", trimmed);
        }

        // 委托文章服务执行搜索
        try {
            ResultVO<Map<String, Object>> result = articleFeignClient.listArticles(trimmed, page, size);
            if (result.getCode() == 200 && result.getData() != null) {
                return result.getData();
            }
        } catch (Exception e) {
            log.error("[搜索失败] keyword={}", trimmed, e);
        }

        // 降级返回空结果
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("records", Collections.emptyList());
        fallback.put("total", 0L);
        fallback.put("size", size);
        fallback.put("current", page);
        fallback.put("pages", 0L);
        return fallback;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getHotKeywords(int limit) {
        Set<Object> topSet = redisUtil.getRedisTemplate()
                .opsForZSet()
                .reverseRange(HOT_KEYWORDS_KEY, 0, limit - 1);

        if (topSet == null || topSet.isEmpty()) {
            return Collections.emptyList();
        }

        return topSet.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

}
