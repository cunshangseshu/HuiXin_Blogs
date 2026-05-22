package com.huixin.stats.controller;

import com.huixin.stats.service.StatsService;
import com.huixin.common.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 统计服务控制器
 *
 * @author 爱吃罗氏虾
 */
@Tag(name = "数据统计", description = "文章阅读量、点赞、热门排行等统计接口")
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Resource
    private StatsService statsService;

    @Operation(summary = "记录阅读", description = "文章阅读量+1（Redis INCR原子操作）")
    @PostMapping("/article/{id}/view")
    public ResultVO<Object> recordView(@PathVariable("id") Long articleId) {
        Long viewCount = statsService.recordView(articleId);
        return ResultVO.success(java.util.Map.of("viewCount", viewCount));
    }

    @Operation(summary = "点赞/取消点赞", description = "Toggle模式，已点则取消，未点则点赞（Redis Set+INCR原子操作）")
    @PostMapping("/article/{id}/like")
    public ResultVO<Object> toggleLike(
            @PathVariable("id") Long articleId,
            @Parameter(description = "用户ID", required = true, hidden = true)
            @RequestHeader("X-User-Id") Long userId) {
        return ResultVO.success(statsService.toggleLike(articleId, userId));
    }

    @Operation(summary = "检查点赞状态", description = "判断当前用户是否已点赞某文章")
    @GetMapping("/article/{id}/liked")
    public ResultVO<Object> hasLiked(
            @PathVariable("id") Long articleId,
            @Parameter(description = "用户ID", required = true, hidden = true)
            @RequestHeader("X-User-Id") Long userId) {
        return ResultVO.success(java.util.Map.of("liked", statsService.hasLiked(articleId, userId)));
    }

    @Operation(summary = "文章统计", description = "获取指定文章的阅读量和点赞数")
    @GetMapping("/article/{id}")
    public ResultVO<Object> getArticleStats(@PathVariable("id") Long articleId) {
        return ResultVO.success(statsService.getArticleStats(articleId));
    }

    @Operation(summary = "热门排行", description = "获取热门文章排行（Redis ZSet倒序）")
    @GetMapping("/hot")
    public ResultVO<Object> getHotArticles(
            @RequestParam(defaultValue = "10") int limit) {
        return ResultVO.success(statsService.getHotArticles(limit));
    }

}
