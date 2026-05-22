package com.huixin.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huixin.common.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 标签Mapper接口
 *
 * @author 爱吃罗氏虾
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 增加标签下文章数量
     */
    @Update("UPDATE tag SET article_count = article_count + #{count} WHERE id = #{tagId} AND article_count + #{count} >= 0")
    int incrementArticleCount(@Param("tagId") Long tagId, @Param("count") int count);

}
