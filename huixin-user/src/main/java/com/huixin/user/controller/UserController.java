package com.huixin.user.controller;

import com.huixin.common.vo.ResultVO;
import com.huixin.user.dto.BloggerApplyDTO;
import com.huixin.user.dto.ChangePasswordDTO;
import com.huixin.user.dto.UpdateUserDTO;
import com.huixin.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 * <p>
 * 提供个人中心相关的所有操作接口。
 * 所有接口（除公开查询外）需要登录态，用户ID从Gateway传递的请求头获取。
 * </p>
 *
 * @author Huixin Blog
 */
@Tag(name = "用户管理", description = "用户个人信息、密码、头像、博主申请等接口")
@Validated
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    /* ==================== 信息查询 ==================== */

    /**
     * 获取当前登录用户的完整信息
     *
     * @param userId 用户ID（Gateway鉴权后注入请求头）
     * @return 当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "返回当前登录用户的完整信息，包含邮箱等私密字段")
    @GetMapping("/info")
    public ResultVO<Object> getUserInfo(
            @Parameter(description = "用户ID", required = true, hidden = true)
            @RequestHeader("X-User-Id") Long userId) {
        return ResultVO.success(userService.getUserInfo(userId));
    }

    /**
     * 获取指定用户的公开信息
     * <p>
     * 用于查看文章作者信息、评论者信息等场景。
     * 注意：该接口为公开接口，生产环境应在 Gateway 层配置限流防止用户枚举。
     * </p>
     *
     * @param id 用户ID（路径参数）
     * @return 用户公开信息（不包含邮箱）
     */
    @Operation(summary = "获取用户公开信息", description = "根据用户ID获取公开信息，不包含邮箱等私密字段")
    @GetMapping("/{id}")
    public ResultVO<Object> getUserPublicInfo(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long id) {
        return ResultVO.success(userService.getUserPublicInfo(id));
    }

    /* ==================== 信息修改 ==================== */

    /**
     * 更新个人信息（昵称、简介）
     *
     * @param userId 当前用户ID
     * @param dto    更新内容
     * @return 更新结果
     */
    @Operation(summary = "修改个人信息", description = "更新当前用户的昵称和个人简介")
    @PutMapping("/info")
    public ResultVO<Void> updateUserInfo(
            @Parameter(description = "用户ID", required = true, hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdateUserDTO dto) {
        userService.updateUserInfo(userId, dto);
        return ResultVO.success("个人信息更新成功");
    }

    /**
     * 修改密码
     * <p>
     * 需要提供旧密码验证身份，新密码与旧密码不能相同。
     * 修改成功后，建议用户重新登录。
     * </p>
     *
     * @param userId 当前用户ID
     * @param dto    旧密码 + 新密码
     * @return 更新结果
     */
    @Operation(summary = "修改密码", description = "验证旧密码后更新为新密码")
    @PutMapping("/password")
    public ResultVO<Void> changePassword(
            @Parameter(description = "用户ID", required = true, hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(userId, dto);
        return ResultVO.success("密码修改成功，请重新登录");
    }

    /* ==================== 头像管理 ==================== */

    /**
     * 上传头像
     * <p>
     * 接收上传的图片文件，保存到本地/OSS并返回URL，
     * 然后将URL更新到用户记录中。
     * 当前简化实现：接收base64或直接传URL。
     * </p>
     *
     * @param userId    当前用户ID
     * @param avatarUrl 头像URL
     * @return 更新结果
     */
    @Operation(summary = "上传头像", description = "更新当前用户的头像URL")
    @PostMapping("/avatar")
    public ResultVO<Void> uploadAvatar(
            @Parameter(description = "用户ID", required = true, hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "头像URL", required = true)
            @NotBlank(message = "头像URL不能为空")
            @Pattern(regexp = "^(https?://|data:image/).+", message = "头像URL格式不正确")
            @RequestParam String avatarUrl) {
        userService.updateAvatar(userId, avatarUrl);
        return ResultVO.success("头像更新成功");
    }

    /* ==================== 博主申请 ==================== */

    /**
     * 申请成为博主
     * <p>
     * 普通用户可提交申请，已有待审核申请或已是博主的用户不可重复申请。
     * 申请提交后由管理员审核（后续实现）。
     * </p>
     *
     * @param userId 申请人用户ID
     * @param dto    申请理由
     * @return 申请结果
     */
    @Operation(summary = "申请成为博主", description = "普通用户提交博主申请，管理员审核后生效")
    @PostMapping("/blogger/apply")
    public ResultVO<Void> applyBlogger(
            @Parameter(description = "用户ID", required = true, hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody BloggerApplyDTO dto) {
        userService.applyBlogger(userId, dto);
        return ResultVO.success("申请已提交，请等待审核");
    }

}
