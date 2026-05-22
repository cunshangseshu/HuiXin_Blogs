package com.huixin.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 慧芯博客 - 网关服务启动类
 * <p>
 * Spring Cloud Gateway 统一API网关：
 * - 路由转发到各微服务
 * - JWT全局鉴权
 * - 跨域处理
 * - 接口限流
 * </p>
 *
 * @author 爱吃罗氏虾
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("====================================");
        System.out.println("  慧芯博客-网关服务 启动成功！");
        System.out.println("  Gateway: http://localhost:8080");
        System.out.println("====================================");
    }

}
