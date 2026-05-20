package com.huixin.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huixin.common.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 文章Mapper接口
 * <p>
 * 提供文章的自定义查询方法，基础CRUD由BaseMapper自动提供。
 * </p>
 *
 * @author Huixin Blog
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 增加文章阅读量（原子操作，线程安全）
     *
     * @param articleId 文章ID
     * @param increment 增加数量
     * @return 影响行数
     */
    @Update("UPDATE article SET view_count = view_count + #{increment} WHERE id = #{articleId}")
    int incrementViewCount(@Param("articleId") Long articleId, @Param("increment") int increment);

    /**
     * 增加文章点赞数
     *
     * @param articleId 文章ID
     * @param increment 增加数量（点赞+1，取消点赞-1）
     * @return 影响行数
     */
    @Update("UPDATE article SET like_count = like_count + #{increment} WHERE id = #{articleId} AND like_count + #{increment} >= 0")
    int incrementLikeCount(@Param("articleId") Long articleId, @Param("increment") int increment);

    /**
     * 增加文章评论数
     *
     * @param articleId 文章ID
     * @param increment 增加数量
     * @return 影响行数
     */
    @Update("UPDATE article SET comment_count = comment_count + #{increment} WHERE id = #{articleId} AND comment_count + #{increment} >= 0")
    int incrementCommentCount(@Param("articleId") Long articleId, @Param("increment") int increment);

}
