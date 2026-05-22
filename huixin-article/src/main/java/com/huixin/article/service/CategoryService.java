package com.huixin.article.service;

import com.huixin.common.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 *
 * @author 爱吃罗氏虾
 */
public interface CategoryService {

    List<Category> listAll();

    Category getById(Long id);

}
