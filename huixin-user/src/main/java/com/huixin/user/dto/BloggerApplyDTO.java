package com.huixin.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 博主申请请求DTO
 *
 * @author Huixin Blog
 */
@Data
@Schema(description = "博主申请请求")
public class BloggerApplyDTO {

    /**
     * 申请理由（10-500字符）
     */
    @NotBlank(message = "申请理由不能为空")
    @Size(min = 10, max = 500, message = "申请理由需在10-500字符之间")
    @Schema(description = "申请理由", example = "我是一名资深Java开发者，有5年博客写作经验，希望能在慧芯博客分享技术文章")
    private String applyReason;

}
