package com.huixin.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j (Swagger增强) API文档配置
 * <p>
 * 为每个微服务提供在线API文档，通过Gateway聚合访问。
 * 访问地址：http://{host}:{port}/doc.html
 * </p>
 *
 * @author Huixin Blog
 */
@Configuration
public class Knife4jConfig {

    /**
     * API文档基本信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("慧芯博客 - API文档")
                        .version("1.0.0")
                        .description("慧芯博客（Huixin Blog）系统API接口文档，基于RESTful风格设计。")
                        .contact(new Contact()
                                .name("Huixin Blog Team")
                                .url("https://github.com/huixin-blog")
                                .email("dev@huixin.blog"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }

}
