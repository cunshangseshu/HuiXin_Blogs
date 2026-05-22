package com.huixin.article.service;

import com.huixin.common.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 *
 * @author 爱吃罗氏虾
 */
public interface TagService {

    List<Tag> listAll();

    Tag getById(Long id);

}
