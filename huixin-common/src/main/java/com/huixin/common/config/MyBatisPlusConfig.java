package com.huixin.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 全局配置
 * <p>
 * 配置项包括：
 * 1. 分页插件（PaginationInnerInterceptor）
 * 2. 自动填充处理器（createTime、updateTime、isDeleted）
 * </p>
 * <p>
 * 注意：驼峰命名转换已在application.yml中通过 mybatis-plus.configuration.map-underscore-to-camel-case=true 开启。
 * 数据库字段 create_time 会自动映射到Java属性 createTime。
 * </p>
 *
 * @author Huixin Blog
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * MyBatis Plus 拦截器配置
     * <p>添加分页插件，支持不同数据库类型。</p>
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 设置单页最大查询数量，防止恶意查询
        paginationInterceptor.setMaxLimit(100L);
        interceptor.addInnerInterceptor(paginationInterceptor);
        return interceptor;
    }

    /**
     * 字段自动填充处理器
     * <p>
     * 在插入/更新时自动填充 createTime、updateTime、isDeleted 字段，
     * 无需手动设置。
     * </p>
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {

            /**
             * 插入时自动填充
             */
            @Override
            public void insertFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                // 创建时间
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
                // 更新时间
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
                // 逻辑删除标识，默认0（未删除）
                this.strictInsertFill(metaObject, "isDeleted", Integer.class, 0);
            }

            /**
             * 更新时自动填充
             */
            @Override
            public void updateFill(MetaObject metaObject) {
                // 更新时间
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }

}
