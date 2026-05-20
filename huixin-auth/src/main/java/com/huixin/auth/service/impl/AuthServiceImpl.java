package com.huixin.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huixin.auth.dto.LoginDTO;
import com.huixin.auth.dto.RegisterDTO;
import com.huixin.auth.mapper.UserMapper;
import com.huixin.auth.service.AuthService;
import com.huixin.auth.vo.TokenVO;
import com.huixin.common.entity.User;
import com.huixin.common.enums.ResultCode;
import com.huixin.common.exception.BusinessException;
import com.huixin.common.utils.JwtUtil;
import com.huixin.common.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 * <p>
 * 处理用户注册、登录、Token管理全流程。
 * 密码使用BCrypt加密，Token使用JWT + Redis双重管理。
 * </p>
 *
 * @author Huixin Blog
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisUtil redisUtil;

    /**
     * BCrypt密码编码器（强度10轮）
     */
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder(10);

    /**
     * Redis中Refresh Token的Key前缀
     */
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    /* ==================== 注册 ==================== */

    /**
     * 用户注册
     * <p>
     * 步骤：
     * 1. 校验用户名唯一性
     * 2. 校验邮箱唯一性
     * 3. BCrypt加密密码
     * 4. 保存用户（默认角色为普通用户0，状态为正常1）
     * </p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String email = registerDTO.getEmail();

        // 1. 检查用户名是否已存在
        Long usernameCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
        if (usernameCount > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXIST);
        }

        // 2. 检查邮箱是否已存在
        Long emailCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getEmail, email)
        );
        if (emailCount > 0) {
            throw new BusinessException(ResultCode.EMAIL_EXIST);
        }

        // 3. 构建用户对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(PASSWORD_ENCODER.encode(registerDTO.getPassword()));
        user.setEmail(email);
        // 昵称默认使用用户名
        user.setNickname(username);
        // 默认角色：普通用户（0）
        user.setRoleType(0);
        // 默认状态：正常（1）
        user.setStatus(1);

        // 4. 保存到数据库
        int rows = userMapper.insert(user);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "注册失败，请稍后重试");
        }

        log.info("[注册成功] username={}, userId={}", username, user.getId());
    }

    /* ==================== 登录 ==================== */

    /**
     * 用户登录
     * <p>
     * 步骤：
     * 1. 根据用户名或邮箱查找用户
     * 2. 校验账号状态
     * 3. BCrypt密码比对
     * 4. 生成Access Token + Refresh Token
     * 5. Refresh Token存入Redis
     * </p>
     */
    @Override
    public TokenVO login(LoginDTO loginDTO) {
        String account = loginDTO.getAccount();

        // 1. 按用户名或邮箱查找用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, account)
                        .or()
                        .eq(User::getEmail, account)
        );
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 检查账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 3. 密码比对
        if (!PASSWORD_ENCODER.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 4. 生成Token
        TokenVO tokenVO = generateTokens(user);

        // 5. 更新最后登录时间（简单处理，用updateById）
        user.setId(user.getId());
        userMapper.updateById(user); // 自动填充 updateTime

        log.info("[登录成功] username={}, userId={}, role={}", user.getUsername(), user.getId(), user.getRoleType());

        return tokenVO;
    }

    /* ==================== Token刷新 ==================== */

    /**
     * 刷新Token
     * <p>
     * 步骤：
     * 1. 验证Refresh Token的JWT签名和有效期
     * 2. 从Token中提取userId
     * 3. 校验Redis中是否存在对应的Refresh Token
     * 4. 查询用户信息
     * 5. 生成新的Access Token
     * </p>
     */
    @Override
    public TokenVO refreshToken(String refreshToken) {
        // 1. 验证JWT签名
        if (!JwtUtil.validateToken(refreshToken)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        // 2. 提取userId
        Long userId = JwtUtil.getUserId(refreshToken);

        // 3. 校验Redis中的Refresh Token
        if (!validateRefreshToken(userId, refreshToken)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID, "Refresh Token已失效");
        }

        // 4. 查询用户
        User user = userMapper.selectById(userId);
        if (user == null || user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 5. 生成新Token
        TokenVO tokenVO = generateTokens(user);

        log.info("[Token刷新成功] userId={}", userId);

        return tokenVO;
    }

    /* ==================== 登出 ==================== */

    /**
     * 用户登出
     * <p>
     * 删除Redis中的Refresh Token。
     * Access Token由于其无状态特性，不做特殊处理（或在Gateway层加入短期黑名单）。
     * </p>
     */
    @Override
    public void logout(Long userId) {
        String redisKey = REFRESH_TOKEN_PREFIX + userId;
        redisUtil.delete(redisKey);
        log.info("[登出成功] userId={}", userId);
    }

    /* ==================== 工具方法 ==================== */

    /**
     * 验证Refresh Token是否有效
     * <p>
     * 对比Redis中存储的Refresh Token与传入的是否一致。
     * </p>
     */
    @Override
    public boolean validateRefreshToken(Long userId, String refreshToken) {
        String redisKey = REFRESH_TOKEN_PREFIX + userId;
        String storedToken = redisUtil.get(redisKey, String.class);
        return StrUtil.isNotEmpty(storedToken) && storedToken.equals(refreshToken);
    }

    /**
     * 生成Access Token + Refresh Token
     * <p>
     * Refresh Token存入Redis（7天过期），用户下次刷新时使用。
     * </p>
     *
     * @param user 用户信息
     * @return TokenVO
     */
    private TokenVO generateTokens(User user) {
        Long userId = user.getId();
        String username = user.getUsername();
        Integer role = user.getRoleType();

        // 生成Access Token（2小时）
        String accessToken = JwtUtil.generateAccessToken(userId, username, role);

        // 生成Refresh Token（7天）
        String refreshToken = JwtUtil.generateRefreshToken(userId, username, role);

        // Refresh Token存入Redis（7天过期）
        String redisKey = REFRESH_TOKEN_PREFIX + userId;
        redisUtil.set(redisKey, refreshToken, 7, TimeUnit.DAYS);

        return TokenVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(JwtUtil.ACCESS_TOKEN_EXPIRE / 1000) // 转为秒
                .build();
    }

}
