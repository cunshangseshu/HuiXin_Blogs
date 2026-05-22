package com.huixin.user.service;

import com.huixin.user.dto.BloggerApplyDTO;
import com.huixin.user.dto.ChangePasswordDTO;
import com.huixin.user.dto.UpdateUserDTO;
import com.huixin.user.vo.UserVO;

/**
 * 用户服务接口
 * <p>
 * 提供用户个人中心相关的所有操作：
 * 信息查询、修改、密码更新、头像上传、博主申请。
 * </p>
 *
 * @author 爱吃罗氏虾
 */
public interface UserService {

    /**
     * 获取当前登录用户信息
     *
     * @param userId 用户ID（从请求头获取）
     * @return 用户信息VO
     */
    UserVO getUserInfo(Long userId);

    /**
     * 获取用户公开信息（供其他用户查看）
     *
     * @param userId 被查看的用户ID
     * @return 公开用户信息
     */
    UserVO getUserPublicInfo(Long userId);

    /**
     * 更新用户个人信息（昵称、简介）
     *
     * @param userId 当前用户ID
     * @param dto    更新内容
     */
    void updateUserInfo(Long userId, UpdateUserDTO dto);

    /**
     * 修改密码
     * <p>
     * 需验证旧密码正确性，新旧密码不能相同。
     * </p>
     *
     * @param userId 当前用户ID
     * @param dto    旧密码 + 新密码
     */
    void changePassword(Long userId, ChangePasswordDTO dto);

    /**
     * 上传/更新头像
     * <p>
     * 保存头像URL到用户记录。
     * </p>
     *
     * @param userId    当前用户ID
     * @param avatarUrl 头像URL（由文件上传服务返回）
     */
    void updateAvatar(Long userId, String avatarUrl);

    /**
     * 申请成为博主
     * <p>
     * 普通用户可以提交申请，同一用户不能重复申请。
     * </p>
     *
     * @param userId 申请人用户ID
     * @param dto    申请理由
     */
    void applyBlogger(Long userId, BloggerApplyDTO dto);

}
