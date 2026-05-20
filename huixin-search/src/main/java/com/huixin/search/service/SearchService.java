package com.huixin.search.service;

import java.util.List;
import java.util.Map;

/**
 * 搜索服务接口
 *
 * @author Huixin Blog
 */
public interface SearchService {

    /**
     * 关键词搜索文章（委托文章服务）
     *
     * @param keyword 搜索关键词
     * @param page    页码
     * @param size    每页数量
     * @return 分页搜索结果
     */
    Map<String, Object> search(String keyword, Integer page, Integer size);

    /**
     * 获取热门搜索词（Redis ZSet排行）
     *
     * @param limit 数量
     * @return 热词列表
     */
    List<String> getHotKeywords(int limit);

}
