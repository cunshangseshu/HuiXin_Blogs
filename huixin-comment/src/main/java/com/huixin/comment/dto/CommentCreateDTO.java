package com.huixin.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建评论请求DTO
 *
 * @author 爱吃罗氏虾
 */
@Data
@Schema(description = "创建评论请求")
public class CommentCreateDTO {

    @NotNull(message = "文章ID不能为空")
    @Schema(description = "所属文章ID", example = "1")
    private Long articleId;

    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 500, message = "评论内容需在1-500字符之间")
    @Schema(description = "评论内容", example = "写得非常好，受益匪浅！")
    private String content;

    @Schema(description = "父评论ID（回复某条评论时传入）")
    private Long parentId;

    @Schema(description = "被回复的用户ID（二级回复时传入）")
    private Long replyToUserId;

}
