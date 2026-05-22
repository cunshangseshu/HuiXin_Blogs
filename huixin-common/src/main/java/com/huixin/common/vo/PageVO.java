package com.huixin.common.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 统一分页结果封装
 * <p>
 * 用于Controller层返回分页数据，避免直接暴露MyBatis Plus的Page对象。
 * </p>
 *
 * @param <T> 分页数据的类型
 * @author 爱吃罗氏虾
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 每页显示条数
     */
    private Long size;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 从 MyBatis Plus 分页结果转换
     *
     * @param page MP分页对象
     * @param <T>  数据类型
     * @return PageVO
     */
    public static <T> PageVO<T> from(IPage<T> page) {
        PageVO<T> vo = new PageVO<>();
        vo.setRecords(page.getRecords());
        vo.setTotal(page.getTotal());
        vo.setSize(page.getSize());
        vo.setCurrent(page.getCurrent());
        vo.setPages(page.getPages());
        return vo;
    }

    /**
     * 创建空分页结果
     *
     * @param <T> 数据类型
     * @return 空PageVO
     */
    public static <T> PageVO<T> empty() {
        PageVO<T> vo = new PageVO<>();
        vo.setRecords(Collections.emptyList());
        vo.setTotal(0L);
        vo.setSize(10L);
        vo.setCurrent(1L);
        vo.setPages(0L);
        return vo;
    }

}
