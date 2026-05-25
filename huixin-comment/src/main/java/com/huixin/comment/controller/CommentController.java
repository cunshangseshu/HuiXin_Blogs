package com.huixin.comment.controller;

import com.huixin.comment.dto.CommentCreateDTO;
import com.huixin.comment.service.CommentService;
import com.huixin.common.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 评论管理控制器
 *
 * @author 爱吃罗氏虾
 */
@Tag(name = "评论管理", description = "文章评论发布、删除、查询等接口")
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @Operation(summary = "发表评论", description = "对文章发表评论或回复某条评论")
    @PostMapping
    public ResultVO<Object> createComment(
            @Parameter(description = "用户ID", required = true, hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CommentCreateDTO dto) {
        return ResultVO.success(commentService.createComment(userId, dto));
    }

    @Operation(summary = "删除评论", description = "删除自己的评论")
    @DeleteMapping("/{id}")
    public ResultVO<Void> deleteComment(
            @Parameter(description = "用户ID", required = true, hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("id") Long id) {
        commentService.deleteComment(id, userId);
        return ResultVO.success("评论已删除");
    }

    @Operation(summary = "文章评论列表", description = "获取指定文章的所有评论（含嵌套回复）")
    @GetMapping("/article/{articleId}")
    public ResultVO<Object> getArticleComments(@PathVariable("articleId") Long articleId) {
        return ResultVO.success(commentService.getArticleComments(articleId));
    }

}
