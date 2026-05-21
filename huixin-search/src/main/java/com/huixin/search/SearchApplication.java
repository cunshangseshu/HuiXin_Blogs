package com.huixin.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 慧芯博客 - 搜索服务启动类
 * <p>
 * 负责全文搜索，通过OpenFeign调用文章服务获取数据。
 * 该模块不直接操作数据库，故无需 @MapperScan。
 * </p>
 *
 * @author Huixin Blog
 */
@SpringBootApplication(scanBasePackages = {"com.huixin.search", "com.huixin.common"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.huixin.search.feign")
public class SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
        System.out.println("====================================");
        System.out.println("  慧芯博客-搜索服务 启动成功！");
        System.out.println("  Port: 8085");
        System.out.println("====================================");
    }

}
