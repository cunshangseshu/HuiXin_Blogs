package com.huixin.auth.service;

import com.huixin.auth.dto.LoginDTO;
import com.huixin.auth.dto.RegisterDTO;
import com.huixin.auth.vo.TokenVO;

/**
 * 认证服务接口
 * <p>
 * 提供用户注册、登录、Token刷新、登出等认证相关操作。
 * </p>
 *
 * @author Huixin Blog
 */
public interface AuthService {

    /**
     * 用户注册
     * <p>
     * 校验用户名唯一性和邮箱唯一性，密码BCrypt加密后存储。
     * </p>
     *
     * @param registerDTO 注册信息
     */
    void register(RegisterDTO registerDTO);

    /**
     * 用户登录
     * <p>
     * 支持用户名或邮箱登录，验证成功后签发Access Token和Refresh Token。
     * Refresh Token存入Redis。
     * </p>
     *
     * @param loginDTO 登录信息
     * @return Token信息
     */
    TokenVO login(LoginDTO loginDTO);

    /**
     * 刷新Token
     * <p>
     * 使用Refresh Token换取新的Access Token。
     * Refresh Token必须有效且在Redis中存在。
     * </p>
     *
     * @param refreshToken 刷新令牌
     * @return 新的Token信息
     */
    TokenVO refreshToken(String refreshToken);

    /**
     * 用户登出
     * <p>
     * 删除Redis中的Refresh Token，使该用户的所有会话失效。
     * </p>
     *
     * @param userId 用户ID
     */
    void logout(Long userId);

    /**
     * 验证Refresh Token是否有效
     *
     * @param userId       用户ID
     * @param refreshToken Refresh Token
     * @return true-有效
     */
    boolean validateRefreshToken(Long userId, String refreshToken);

}
