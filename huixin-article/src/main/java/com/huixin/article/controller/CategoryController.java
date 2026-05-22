package com.huixin.article.controller;

import com.huixin.article.service.CategoryService;
import com.huixin.common.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 分类管理控制器
 *
 * @author 爱吃罗氏虾
 */
@Tag(name = "分类管理", description = "文章分类查询接口")
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @Operation(summary = "获取所有分类")
    @GetMapping("/list")
    public ResultVO<Object> listAll() {
        return ResultVO.success(categoryService.listAll());
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public ResultVO<Object> getById(@PathVariable Long id) {
        return ResultVO.success(categoryService.getById(id));
    }

}
