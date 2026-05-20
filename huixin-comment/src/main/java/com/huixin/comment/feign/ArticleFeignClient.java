package com.huixin.comment.feign;

import com.huixin.common.vo.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 文章服务Feign客户端
 * <p>
 * 用于验证文章是否存在。
 * </p>
 *
 * @author Huixin Blog
 */
@FeignClient(name = "huixin-article", path = "/api/article")
public interface ArticleFeignClient {

    /**
     * 获取文章详情（仅用于验证存在性）
     */
    @GetMapping("/{id}")
    ResultVO<Map<String, Object>> getArticleDetail(@PathVariable("id") Long id);

}
