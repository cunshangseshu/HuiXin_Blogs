package com.huixin.article.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huixin.article.dto.ArticleCreateDTO;
import com.huixin.article.dto.ArticleQueryDTO;
import com.huixin.article.feign.StatsFeignClient;
import com.huixin.article.feign.UserFeignClient;
import com.huixin.article.mapper.ArticleMapper;
import com.huixin.article.mapper.ArticleTagMapper;
import com.huixin.article.mapper.CategoryMapper;
import com.huixin.article.mapper.TagMapper;
import com.huixin.article.service.ArticleService;
import com.huixin.article.vo.ArticleListVO;
import com.huixin.article.vo.ArticleVO;
import com.huixin.article.vo.TagVO;
import com.huixin.common.entity.*;
import com.huixin.common.enums.ResultCode;
import com.huixin.common.exception.BusinessException;
import com.huixin.common.vo.PageVO;
import com.huixin.common.vo.ResultVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 *
 * @author Huixin Blog
 */
@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private ArticleTagMapper articleTagMapper;
    @Resource
    private UserFeignClient userFeignClient;
    @Resource
    private StatsFeignClient statsFeignClient;

    /* ==================== 创建文章 ==================== */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createArticle(Long authorId, ArticleCreateDTO dto) {
        // 1. 验证分类存在
        Category category = categoryMapper.selectById(dto.getCategoryId());
        if (category == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }

        // 2. 构建并保存文章
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setSummary(dto.getSummary());
        article.setContent(dto.getContent());
        article.setContentHtml(dto.getContentHtml());
        article.setCoverImageUrl(dto.getCoverImageUrl());
        article.setAuthorId(authorId);
        article.setCategoryId(dto.getCategoryId());
        article.setArticleStatus(1); // 直接发布，0为草稿
        article.setIsOriginal(dto.getIsOriginal() != null ? dto.getIsOriginal() : 1);
        article.setPublishTime(LocalDateTime.now());

        articleMapper.insert(article);
        Long articleId = article.getId();

        // 3. 保存文章-标签关联
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            saveArticleTags(articleId, dto.getTagIds());
        }

        // 4. 更新分类和标签的文章计数
        categoryMapper.incrementArticleCount(dto.getCategoryId(), 1);
        if (dto.getTagIds() != null) {
            dto.getTagIds().forEach(tagId -> tagMapper.incrementArticleCount(tagId, 1));
        }

        log.info("[文章发布成功] articleId={}, authorId={}, title={}", articleId, authorId, dto.getTitle());
        return articleId;
    }

    /* ==================== 编辑文章 ==================== */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(Long articleId, Long authorId, ArticleCreateDTO dto) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        if (!article.getAuthorId().equals(authorId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        // 更新基本信息
        article.setTitle(dto.getTitle());
        article.setSummary(dto.getSummary());
        article.setContent(dto.getContent());
        article.setContentHtml(dto.getContentHtml());
        article.setCoverImageUrl(dto.getCoverImageUrl());
        article.setIsOriginal(dto.getIsOriginal() != null ? dto.getIsOriginal() : 1);

        // 分类变更处理
        if (!article.getCategoryId().equals(dto.getCategoryId())) {
            categoryMapper.incrementArticleCount(article.getCategoryId(), -1);
            categoryMapper.incrementArticleCount(dto.getCategoryId(), 1);
            article.setCategoryId(dto.getCategoryId());
        }

        articleMapper.updateById(article);

        // 标签变更处理
        if (dto.getTagIds() != null) {
            // 删除旧关联
            List<ArticleTag> oldTags = articleTagMapper.selectList(
                    new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, articleId)
            );
            oldTags.forEach(t -> tagMapper.incrementArticleCount(t.getTagId(), -1));
            articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, articleId));

            // 保存新关联
            saveArticleTags(articleId, dto.getTagIds());
            dto.getTagIds().forEach(tagId -> tagMapper.incrementArticleCount(tagId, 1));
        }

        log.info("[文章编辑成功] articleId={}, authorId={}", articleId, authorId);
    }

    /* ==================== 删除文章 ==================== */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long articleId, Long authorId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        if (!article.getAuthorId().equals(authorId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        // 逻辑删除
        articleMapper.deleteById(articleId);

        // 更新分类和标签计数
        categoryMapper.incrementArticleCount(article.getCategoryId(), -1);
        List<ArticleTag> tags = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, articleId)
        );
        tags.forEach(t -> tagMapper.incrementArticleCount(t.getTagId(), -1));

        log.info("[文章删除成功] articleId={}, authorId={}", articleId, authorId);
    }

    /* ==================== 查询文章详情 ==================== */

    @Override
    public ArticleVO getArticleDetail(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null || article.getArticleStatus() == 0) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }

        // 异步上报阅读量（容错处理）
        try {
            statsFeignClient.recordView(articleId);
        } catch (Exception e) {
            log.warn("[阅读量上报失败] articleId={}", articleId);
        }

        return buildArticleVO(article);
    }

    /* ==================== 文章列表 ==================== */

    @Override
    public PageVO<ArticleListVO> listArticles(ArticleQueryDTO queryDTO) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getArticleStatus, 1) // 只查已发布
                .eq(Article::getIsDeleted, 0);

        // 分类筛选
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Article::getCategoryId, queryDTO.getCategoryId());
        }
        // 作者筛选
        if (queryDTO.getAuthorId() != null) {
            wrapper.eq(Article::getAuthorId, queryDTO.getAuthorId());
        }
        // 搜索关键词
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isBlank()) {
            wrapper.and(w -> w.like(Article::getTitle, queryDTO.getKeyword())
                    .or().like(Article::getSummary, queryDTO.getKeyword()));
        }
        // 排序
        if ("hot".equals(queryDTO.getSort())) {
            wrapper.orderByDesc(Article::getViewCount);
        } else {
            // 默认最新：置顶优先 + 时间倒序
            wrapper.orderByDesc(Article::getIsTop)
                    .orderByDesc(Article::getPublishTime);
        }

        Page<Article> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        Page<Article> result = articleMapper.selectPage(page, wrapper);

        // 转换为列表VO
        List<ArticleListVO> list = result.getRecords().stream()
                .map(this::buildArticleListVO)
                .collect(Collectors.toList());

        PageVO<ArticleListVO> pageVO = PageVO.from(result);
        pageVO.setRecords(list);
        return pageVO;
    }

    @Override
    public PageVO<ArticleListVO> listUserArticles(Long authorId, Integer page, Integer size) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getAuthorId, authorId)
                .eq(Article::getIsDeleted, 0)
                .orderByDesc(Article::getPublishTime);

        Page<Article> mpPage = new Page<>(page, size);
        Page<Article> result = articleMapper.selectPage(mpPage, wrapper);

        List<ArticleListVO> list = result.getRecords().stream()
                .map(this::buildArticleListVO)
                .collect(Collectors.toList());

        PageVO<ArticleListVO> pageVO = PageVO.from(result);
        pageVO.setRecords(list);
        return pageVO;
    }

    /* ==================== 内部方法 ==================== */

    /**
     * 保存文章-标签关联
     */
    private void saveArticleTags(Long articleId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            Tag tag = tagMapper.selectById(tagId);
            if (tag == null) continue;
            ArticleTag at = new ArticleTag();
            at.setArticleId(articleId);
            at.setTagId(tagId);
            articleTagMapper.insert(at);
        }
    }

    /**
     * 构建文章详情VO（含作者信息）
     */
    private ArticleVO buildArticleVO(Article article) {
        // 获取分类名
        String categoryName = "";
        Category category = categoryMapper.selectById(article.getCategoryId());
        if (category != null) categoryName = category.getCategoryName();

        // 获取标签
        List<ArticleTag> articleTags = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, article.getId())
        );
        List<TagVO> tags = articleTags.stream().map(at -> {
            Tag tag = tagMapper.selectById(at.getTagId());
            return tag != null ? TagVO.builder().id(tag.getId()).tagName(tag.getTagName()).tagColor(tag.getTagColor()).build() : null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        // 获取作者信息（Feign，容错）
        String authorName = "";
        String authorAvatar = "";
        try {
            ResultVO<Map<String, Object>> result = userFeignClient.getUserPublicInfo(article.getAuthorId());
            if (result.getCode() == 200 && result.getData() != null) {
                Map<String, Object> userData = result.getData();
                // 安全获取：getOrDefault 在 key 存在但值为 null 时返回 null 而非默认值
                Object nickname = userData.get("nickname");
                Object username = userData.get("username");
                authorName = nickname != null ? nickname.toString()
                           : username != null ? username.toString() : "";
                Object avatar = userData.get("avatarUrl");
                authorAvatar = avatar != null ? avatar.toString() : "";
            }
        } catch (Exception e) {
            log.warn("[获取作者信息失败] authorId={}", article.getAuthorId());
        }

        return ArticleVO.builder()
                .id(article.getId()).title(article.getTitle()).summary(article.getSummary())
                .content(article.getContent()).contentHtml(article.getContentHtml())
                .coverImageUrl(article.getCoverImageUrl())
                .authorId(article.getAuthorId()).authorName(authorName).authorAvatar(authorAvatar)
                .categoryId(article.getCategoryId()).categoryName(categoryName).tags(tags)
                .viewCount(article.getViewCount()).likeCount(article.getLikeCount()).commentCount(article.getCommentCount())
                .publishTime(article.getPublishTime()).isTop(article.getIsTop()).isOriginal(article.getIsOriginal())
                .build();
    }

    /**
     * 构建文章列表VO（不含正文）
     */
    private ArticleListVO buildArticleListVO(Article article) {
        String categoryName = "";
        Category category = categoryMapper.selectById(article.getCategoryId());
        if (category != null) categoryName = category.getCategoryName();

        String authorName = "", authorAvatar = "";
        try {
            ResultVO<Map<String, Object>> result = userFeignClient.getUserPublicInfo(article.getAuthorId());
            if (result.getCode() == 200 && result.getData() != null) {
                Map<String, Object> userData = result.getData();
                Object nickname = userData.get("nickname");
                Object username = userData.get("username");
                authorName = nickname != null ? nickname.toString()
                           : username != null ? username.toString() : "";
                Object avatar = userData.get("avatarUrl");
                authorAvatar = avatar != null ? avatar.toString() : "";
            }
        } catch (Exception ignored) {}

        return ArticleListVO.builder()
                .id(article.getId()).title(article.getTitle()).summary(article.getSummary())