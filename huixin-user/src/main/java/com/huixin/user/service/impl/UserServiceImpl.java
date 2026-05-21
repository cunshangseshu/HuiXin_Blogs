package com.huixin.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huixin.common.entity.BloggerApply;
import com.huixin.common.entity.User;
import com.huixin.common.enums.ResultCode;
import com.huixin.common.exception.BusinessException;
import com.huixin.user.dto.BloggerApplyDTO;
import com.huixin.user.dto.ChangePasswordDTO;
import com.huixin.user.dto.UpdateUserDTO;
import com.huixin.user.mapper.BloggerApplyMapper;
import com.huixin.user.mapper.UserMapper;
import com.huixin.user.service.UserService;
import com.huixin.user.vo.UserVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 * <p>
 * 处理用户个人中心的所有业务逻辑。
 * 密码使用BCrypt加密，头像URL由上层(Controller)负责文件上传后传入。
 * </p>
 *
 * @author Huixin Blog
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private BloggerApplyMapper bloggerApplyMapper;

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder(10);

    /* ==================== 查询用户信息 ==================== */

    /**
     * 获取当前登录用户的完整信息
     */
    @Override
    public UserVO getUserInfo(Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户身份信息缺失");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return buildUserVO(user);
    }

    /**
     * 获取用户公开信息（屏蔽邮箱等敏感字段）
     */
    @Override
    public UserVO getUserPublicInfo(Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户ID不能为空");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 公开信息不暴露邮箱
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .roleType(user.getRoleType())
                .createTime(user.getCreateTime())
                .build();
    }

    /* ==================== 更新用户信息 ==================== */

    /**
     * 更新昵称和个人简介
     * <p>
     * 仅更新传入的非null字段，未传入的字段保持原值。
     * </p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(Long userId, UpdateUserDTO dto) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户身份信息缺失");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 使用 LambdaUpdateWrapper 仅更新传入字段，避免全行 UPDATE
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId);
        boolean needUpdate = false;
        if (dto.getNickname() != null) {
            wrapper.set(User::getNickname, dto.getNickname());
            needUpdate = true;
        }
        if (dto.getBio() != null) {
            wrapper.set(User::getBio, dto.getBio());
            needUpdate = true;
        }

        if (needUpdate) {
            userMapper.update(null, wrapper);
            log.info("[更新用户信息] userId={}, nickname={}, bio={}", userId, dto.getNickname(), dto.getBio());
        }
    }

    /* ==================== 修改密码 ==================== */

    /**
     * 修改密码
     * <p>
     * 步骤：
     * 1. 验证旧密码是否正确
     * 2. 检查新旧密码是否相同
     * 3. BCrypt加密新密码后更新
     * </p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户身份信息缺失");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 1. 验证旧密码
        if (!PASSWORD_ENCODER.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 2. 新旧密码不能相同（明文比较即可，避免昂贵的 BCrypt.matches 调用）
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "新密码不能与旧密码相同");
        }

        // 3. 使用 LambdaUpdateWrapper 仅更新密码字段
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId)
               .set(User::getPassword, PASSWORD_ENCODER.encode(dto.getNewPassword()));
        userMapper.update(null, wrapper);

        log.info("[修改密码成功] userId={}", userId);
    }

    /* ==================== 头像管理 ==================== */

    /**
     * 更新头像URL
     * <p>
     * 实际的文件上传操作由Controller层完成，
     * 此方法仅负责将最终的URL保存到数据库。
     * </p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAvatar(Long userId, String avatarUrl) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户身份信息缺失");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId)
               .set(User::getAvatarUrl, avatarUrl);
        userMapper.update(null, wrapper);

        log.info("[头像更新成功] userId={}, avatarUrl={}", userId, avatarUrl);
    }

    /* ==================== 博主申请 ==================== */

    /**
     * 申请成为博主
     * <p>
     * 检查是否已有待审核或已通过的申请记录，
     * 防止重复申请。
     * </p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyBlogger(Long userId, BloggerApplyDTO dto) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户身份信息缺失");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 已经是博主的无需申请
        if (user.getRoleType() == 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "您已经是博主，无需重复申请");
        }

        // 检查是否已有待审核的申请
        Long pendingCount = bloggerApplyMapper.selectCount(
                new LambdaQueryWrapper<BloggerApply>()
                        .eq(BloggerApply::getUserId, userId)
                        .eq(BloggerApply::getApplyStatus, 0) // 待审核
        );
        if (pendingCount > 0) {
            throw new BusinessException(ResultCode.BLOGGER_APPLY_DUPLICATE);
        }

        // 创建申请记录
        BloggerApply apply = new BloggerApply();
        apply.setUserId(userId);
        apply.setApplyReason(dto.getApplyReason());
        apply.setApplyStatus(0); // 待审核
        // 防御性设置 createTime（MyBatis Plus @TableField fill 机制自动处理，此处作为兜底）
        apply.setCreateTime(LocalDateTime.now());

        try {
            bloggerApplyMapper.insert(apply);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ResultCode.BLOGGER_APPLY_DUPLICATE);
        }

        log.info("[博主申请提交] userId={}, applyId={}", userId, apply.getId());
    }

    /* ==================== 内部方法 ==================== */

    /**
     * 将User实体转换为UserVO
     */
    private UserVO buildUserVO(User user) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .roleType(user.getRoleType())
                .createTime(user.getCreateTime())
                .build();
    }

}
