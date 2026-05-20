package com.huixin.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huixin.common.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 分类Mapper接口
 *
 * @author Huixin Blog
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 增加分类下文章数量
     */
    @Update("UPDATE category SET article_count = article_count + #{count} WHERE id = #{categoryId}")
    int incrementArticleCount(@Param("categoryId") Long categoryId, @Param("count") int count);

}
