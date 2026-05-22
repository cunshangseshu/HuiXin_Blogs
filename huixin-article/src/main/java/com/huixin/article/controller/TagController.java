package com.huixin.article.controller;

import com.huixin.article.service.TagService;
import com.huixin.common.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 标签管理控制器
 *
 * @author 爱吃罗氏虾
 */
@Tag(name = "标签管理", description = "文章标签查询接口")
@RestController
@RequestMapping("/api/tag")
public class TagController {

    @Resource
    private TagService tagService;

    @Operation(summary = "获取所有标签")
    @GetMapping("/list")
    public ResultVO<Object> listAll() {
        return ResultVO.success(tagService.listAll());
    }

    @Operation(summary = "获取标签详情")
    @GetMapping("/{id}")
    public ResultVO<Object> getById(@PathVariable Long id) {
        return ResultVO.success(tagService.getById(id));
    }

}
