package com.huixin.article.feign;

import com.huixin.common.vo.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * 统计服务Feign客户端
 * <p>
 * 用于上报/获取文章统计数据。
 * </p>
 *
 * @author Huixin Blog
 */
@FeignClient(name = "huixin-stats", path = "/api/stats")
public interface StatsFeignClient {

    /**
     * 记录文章阅读
     *
     * @param articleId 文章ID
     * @return 最新统计数据
     */
    @PostMapping("/article/{id}/view")
    ResultVO<Map<String, Object>> recordView(@PathVariable("id") Long articleId);

}
