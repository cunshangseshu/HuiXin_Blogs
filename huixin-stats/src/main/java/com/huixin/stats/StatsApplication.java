package com.huixin.stats;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 慧芯博客 - 统计服务启动类
 * <p>
 * 负责阅读量统计、点赞记录、热门排行。
 * 包含定时任务（Redis数据定期同步到MySQL）。
 * </p>
 *
 * @author Huixin Blog
 */
@SpringBootApplication(scanBasePackages = {"com.huixin.stats", "com.huixin.common"})
@EnableDiscoveryClient
@EnableScheduling
@MapperScan("com.huixin.stats.mapper")
public class StatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatsApplication.class, args);
        System.out.println("====================================");
        System.out.println("  慧芯博客-统计服务 启动成功！");
        System.out.println("  Port: 8086");
        System.out.println("====================================");
    }

}
