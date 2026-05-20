package com.huixin.article.service;

import com.huixin.common.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 *
 * @author Huixin Blog
 */
public interface TagService {

    List<Tag> listAll();

    Tag getById(Long id);

}
