package com.huixin.article.controller;

import com.huixin.article.dto.ArticleCreateDTO;
import com.huixin.article.dto.ArticleQueryDTO;
import com.huixin.article.service.ArticleService;
import com.huixin.common.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 文章管理控制器
 *
 * @author 爱吃罗氏虾
 */
@Slf4j
@Tag(name = "文章管理", description = "文章发布、编辑、删除、查询等接口")
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @Operation(summary = "发布文章", description = "博主发布新文章，支持选择分类和多个标签")
    @PostMapping
    public ResultVO<Map<String, Object>> createArticle(
            @Parameter(description = "用户ID", required = true, hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ArticleCreateDTO dto) {
        Long articleId = articleService.createArticle(userId, dto);
        return ResultVO.success(Map.of("articleId", articleId));
    }

    @Operation(summary = "编辑文章", description = "作者编辑自己的文章，分类变更时自动更新计数")
    @PutMapping("/{id}")
    public ResultVO<Void> updateArticle(
            @Parameter(description = "用户ID", required = true, hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("id") Long id,
            @Valid @RequestBody ArticleCreateDTO dto) {
        articleService.updateArticle(id, userId, dto);
        return ResultVO.success("文章更新成功");
    }

    @Operation(summary = "删除文章", description = "作者删除自己的文章（逻辑删除）")
    @DeleteMapping("/{id}")
    public ResultVO<Void> deleteArticle(
            @Parameter(description = "用户ID", required = true, hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("id") Long id) {
        articleService.deleteArticle(id, userId);
        return ResultVO.success("文章已删除");
    }

    @Operation(summary = "获取文章详情", description = "获取文章正文、作者信息、标签等完整信息")
    @GetMapping("/{id}")
    public ResultVO<Object> getArticleDetail(@PathVariable("id") Long id) {
        log.info("进来了喔");
        return ResultVO.success(articleService.getArticleDetail(id));
    }

    @Operation(summary = "文章列表", description = "分页查询文章列表，支持分类/作者/关键词筛选和排序")
    @GetMapping("/list")
    public ResultVO<Object> listArticles(@Valid ArticleQueryDTO queryDTO) {
        return ResultVO.success(articleService.listArticles(queryDTO));
    }

    @Operation(summary = "用户文章列表", description = "查询指定用户发布的文章")
    @GetMapping("/user/{authorId}")
    public ResultVO<Object> listUserArticles(
            @PathVariable("authorId") Long authorId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return ResultVO.success(articleService.listUserArticles(authorId, page, size));
    }

    /* ==================== 内部Feign调用接口 ==================== */

    @Operation(summary = "验证文章是否存在", description = "轻量级检查，供评论服务等内部调用")
    @GetMapping("/{id}/exists")
    public ResultVO<Boolean> checkArticleExists(@PathVariable("id") Long id) {
        return ResultVO.success(articleService.existsById(id));
    }

    @Operation(summary = "获取文章基本信息", description = "获取文章authorId等核心字段，供评论服务权限校验")
    @GetMapping("/{id}/basic")
    public ResultVO<Map<String, Object>> getArticleBasic(@PathVariable("id") Long id) {
        return ResultVO.success(articleService.getArticleBasic(id));
    }

    @Operation(summary = "评论数增减", description = "文章评论数+1或-1，供评论服务内部调用")
    @PostMapping("/{id}/comment-count")
    public ResultVO<Void> incrementCommentCount(
            @PathVariable("id") Long id,
            @RequestParam(value = "delta") int delta) {
        articleService.incrementCommentCount(id, delta);
        return ResultVO.success();
    }

}
