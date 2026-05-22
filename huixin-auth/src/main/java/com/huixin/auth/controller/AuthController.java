package com.huixin.auth.controller;

import com.huixin.auth.dto.LoginDTO;
import com.huixin.auth.dto.RegisterDTO;
import com.huixin.auth.service.AuthService;
import com.huixin.common.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * <p>
 * 提供注册、登录、Token刷新、登出四个核心认证接口。
 * 注意：这些接口在Gateway白名单中，无需携带Token访问。
 * </p>
 *
 * @author 爱吃罗氏虾
 */
@Tag(name = "认证管理", description = "用户注册、登录、Token管理等认证相关接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    /**
     * 用户注册
     * <p>注册成功后返回成功消息，用户需自行跳转登录页。</p>
     *
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @Operation(summary = "用户注册", description = "新用户注册，用户名和邮箱均需唯一")
    @PostMapping("/register")
    public ResultVO<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return ResultVO.success("注册成功，请登录");
    }

    /**
     * 用户登录
     * <p>支持用户名或邮箱登录，返回Access Token和Refresh Token。</p>
     *
     * @param loginDTO 登录信息
     * @return Token信息
     */
    @Operation(summary = "用户登录", description = "支持用户名或邮箱登录，返回JWT Token")
    @PostMapping("/login")
    public ResultVO<Object> login(@Valid @RequestBody LoginDTO loginDTO) {
        return ResultVO.success(authService.login(loginDTO));
    }

    /**
     * 刷新Token
     * <p>使用Refresh Token换取新的Access Token。</p>
     *
     * @param refreshToken 从请求头或参数获取的Refresh Token
     * @return 新的Token信息
     */
    @Operation(summary = "刷新Token", description = "使用Refresh Token换取新的Access Token")
    @PostMapping("/refresh")
    public ResultVO<Object> refreshToken(
            @Parameter(description = "Refresh Token", required = true)
            @RequestHeader("X-Refresh-Token") String refreshToken) {
        return ResultVO.success(authService.refreshToken(refreshToken));
    }

    /**
     * 用户登出
     * <p>清除服务端Refresh Token，客户端应同时清除本地Token。</p>
     *
     * @param userId 用户ID（从Gateway传递的请求头中获取）
     * @return 登出结果
     */
    @Operation(summary = "用户登出", description = "清除服务端Refresh Token")
    @PostMapping("/logout")
    public ResultVO<Void> logout(
            @Parameter(description = "用户ID", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        authService.logout(userId);
        return ResultVO.success("已退出登录");
    }

}
