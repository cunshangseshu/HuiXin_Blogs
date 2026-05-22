package com.huixin.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huixin.common.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章-标签关联Mapper接口
 *
 * @author 爱吃罗氏虾
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

}
