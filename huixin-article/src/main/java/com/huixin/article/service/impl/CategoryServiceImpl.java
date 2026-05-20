package com.huixin.article.service.impl;

import com.huixin.article.mapper.CategoryMapper;
import com.huixin.article.service.CategoryService;
import com.huixin.common.entity.Category;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> listAll() {
        return categoryMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category>()
                        .orderByAsc(Category::getSortOrder)
        );
    }

    @Override
    public Category getById(Long id) {
        return categoryMapper.selectById(id);
    }

}
