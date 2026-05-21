package com.huixin.comment.feign;

import com.huixin.common.vo.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * 文章服务Feign客户端
 *
 * @author Huixin Blog
 */
@FeignClient(name = "huixin-article", path = "/api/article")
public interface ArticleFeignClient {

    /**
     * 验证文章是否存在（轻量级检查）
     */
    @GetMapping("/{id}/exists")
    ResultVO<Boolean> checkArticleExists(@PathVariable("id") Long id);

    /**
     * 获取文章基本信息（含 authorId，用于权限校验）
     */
    @GetMapping("/{id}/basic")
    ResultVO<Map<String, Object>> getArticleBasic(@PathVariable("id") Long id);

    /**
     * 文章评论数+1/-1
     * 注意：需要在 huixin-article 模块实现对应端点
     */
    @PostMapping("/{id}/comment-count")
    ResultVO<Void> incrementCommentCount(@PathVariable("id") Long id,
                                          @org.springframework.web.bind.annotation.RequestParam("delta") int delta);

}
