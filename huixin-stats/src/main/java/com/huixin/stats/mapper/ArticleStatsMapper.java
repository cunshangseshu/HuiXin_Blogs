package com.huixin.stats.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huixin.common.entity.ArticleStats;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章统计Mapper接口
 *
 * @author Huixin Blog
 */
@Mapper
public interface ArticleStatsMapper extends BaseMapper<ArticleStats> {

}
