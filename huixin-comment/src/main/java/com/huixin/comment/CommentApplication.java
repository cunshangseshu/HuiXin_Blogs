package com.huixin.comment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 慧芯博客 - 评论服务启动类
 * <p>
 * 负责评论发布、回复、删除、评论列表。
 * 通过OpenFeign调用文章服务和用户服务。
 * </p>
 *
 * @author 爱吃罗氏虾
 */
@SpringBootApplication(scanBasePackages = {"com.huixin.comment", "com.huixin.common"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.huixin.comment.feign")
@MapperScan("com.huixin.comment.mapper")
public class CommentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommentApplication.class, args);
        System.out.println("====================================");
        System.out.println("  慧芯博客-评论服务 启动成功！");
        System.out.println("  Port: 8084");
        System.out.println("====================================");
    }

}
