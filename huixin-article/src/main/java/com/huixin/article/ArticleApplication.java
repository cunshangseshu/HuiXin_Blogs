package com.huixin.article;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 慧芯博客 - 文章服务启动类
 * <p>
 * 负责文章CRUD、分类管理、标签管理。
 * 通过OpenFeign调用用户服务和统计服务。
 * </p>
 *
 * @author Huixin Blog
 */
@SpringBootApplication(scanBasePackages = {"com.huixin.article", "com.huixin.common"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.huixin.article.feign")
@MapperScan("com.huixin.article.mapper")
public class ArticleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArticleApplication.class, args);
        System.out.println("====================================");
        System.out.println("  慧芯博客-文章服务 启动成功！");
        System.out.println("  Port: 8083");
        System.out.println("====================================");
    }

}
