package com.huixin.comment.feign;

import com.huixin.common.vo.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 用户服务Feign客户端
 *
 * @author 爱吃罗氏虾
 */
@FeignClient(name = "huixin-user", path = "/user")
public interface UserFeignClient {

    @GetMapping("/{id}")
    ResultVO<Map<String, Object>> getUserPublicInfo(@PathVariable("id") Long id);

    /**
     * 批量获取用户信息（避免 N+1）
     */
    @GetMapping("/batch")
    ResultVO<List<Map<String, Object>>> getUsersByIds(@RequestParam("ids") List<Long> ids);

}
