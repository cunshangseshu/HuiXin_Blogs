package com.huixin.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huixin.article.mapper.TagMapper;
import com.huixin.article.service.TagService;
import com.huixin.common.entity.Tag;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Resource
    private TagMapper tagMapper;

    @Override
    public List<Tag> listAll() {
        return tagMapper.selectList(
                new LambdaQueryWrapper<Tag>()
                        .orderByDesc(Tag::getArticleCount)
        );
    }

    @Override
    public Tag getById(Long id) {
        return tagMapper.selectById(id);
    }

}
