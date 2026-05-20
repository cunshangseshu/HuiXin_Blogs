package com.huixin.search.feign;

import com.huixin.common.vo.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 文章服务Feign客户端
 *
 * @author Huixin Blog
 */
@FeignClient(name = "huixin-article", path = "/api/article")
public interface ArticleFeignClient {

    @GetMapping("/list")
    ResultVO<Map<String, Object>> listArticles(
            @RequestParam("keyword") String keyword,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    );

}
