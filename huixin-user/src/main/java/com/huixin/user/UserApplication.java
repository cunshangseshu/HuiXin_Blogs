package com.huixin.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 慧芯博客 - 用户服务启动类
 * <p>
 * 负责用户信息管理、头像上传、博主申请。
 * </p>
 *
 * @author 爱吃罗氏虾
 */
@SpringBootApplication(scanBasePackages = {"com.huixin.user", "com.huixin.common"})
@EnableDiscoveryClient
@MapperScan("com.huixin.user.mapper")
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
        System.out.println("====================================");
        System.out.println("  慧芯博客-用户服务 启动成功！");
        System.out.println("  Port: 8082");
        System.out.println("====================================");
    }

}
