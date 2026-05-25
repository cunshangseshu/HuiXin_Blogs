package com.huixin.search.controller;

import com.huixin.search.service.SearchService;
import com.huixin.common.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 搜索服务控制器
 *
 * @author 爱吃罗氏虾
 */
@Tag(name = "搜索服务", description = "全文搜索、热门搜索词等接口")
@RestController
@RequestMapping("/search")
public class SearchController {

    @Resource
    private SearchService searchService;

    @Operation(summary = "搜索文章", description = "按关键词搜索文章标题和摘要")
    @GetMapping
    public ResultVO<Object> search(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (keyword == null || keyword.isBlank()) {
            return ResultVO.error(400, "请输入搜索关键词");
        }
        return ResultVO.success(searchService.search(keyword, page, size));
    }

    @Operation(summary = "热门搜索词", description = "获取搜索频次最高的关键词（Redis ZSet排行）")
    @GetMapping("/hot-keywords")
    public ResultVO<Object> getHotKeywords(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ResultVO.success(searchService.getHotKeywords(limit));
    }

}
