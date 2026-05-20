package com.huixin.comment.feign;

import com.huixin.common.vo.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 用户服务Feign客户端
 * <p>
 * 用于获取评论者用户名和头像。
 * </p>
 *
 * @author Huixin Blog
 */
@FeignClient(name = "huixin-user", path = "/api/user")
public interface UserFeignClient {

    @GetMapping("/{id}")
    ResultVO<Map<String, Object>> getUserPublicInfo(@PathVariable("id") Long id);

}
