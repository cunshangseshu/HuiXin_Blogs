package com.huixin.article.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huixin.article.dto.ArticleCreateDTO;
import com.huixin.article.dto.ArticleQueryDTO;
import com.huixin.article.vo.ArticleListVO;
import com.huixin.article.vo.ArticleVO;
import com.huixin.common.vo.PageVO;

/**
 * 文章服务接口
 *
 * @author 爱吃罗氏虾
 */
public interface ArticleService {

    /**
     * 发布文章
     *
     * @param authorId 作者用户ID
     * @param dto      文章内容
     * @return 文章ID
     */
    Long createArticle(Long authorId, ArticleCreateDTO dto);

    /**
     * 编辑文章
     *
     * @param articleId 文章ID
     * @param authorId  作者ID（验证权限）
     * @param dto       新内容
     */
    void updateArticle(Long articleId, Long authorId, ArticleCreateDTO dto);

    /**
     * 删除文章（逻辑删除）
     *
     * @param articleId 文章ID
     * @param authorId  作者ID（验证权限）
     */
    void deleteArticle(Long articleId, Long authorId);

    /**
     * 获取文章详情
     *
     * @param articleId 文章ID
     * @return 文章详情（含作者信息）
     */
    ArticleVO getArticleDetail(Long articleId);

    /**
     * 分页查询文章列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageVO<ArticleListVO> listArticles(ArticleQueryDTO queryDTO);

    /**
     * 获取指定用户的文章列表
     *
     * @param authorId 作者ID
     * @param page     页码
     * @param size     每页数量
     * @return 分页结果
     */
    PageVO<ArticleListVO> listUserArticles(Long authorId, Integer page, Integer size);

}
