package com.huixin.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 慧芯博客 - 认证服务启动类
 * <p>
 * 负责用户注册、登录、JWT Token签发与验证。
 * </p>
 *
 * @author 爱吃罗氏虾
 */
@SpringBootApplication(scanBasePackages = {"com.huixin.auth", "com.huixin.common"})
@EnableDiscoveryClient
@MapperScan("com.huixin.auth.mapper")
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
        System.out.println("====================================");
        System.out.println("  慧芯博客-认证服务 启动成功！");
        System.out.println("  Port: 8081");
        System.out.println("====================================");
    }

}
