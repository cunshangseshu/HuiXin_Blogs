package com.huixin.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 博主申请记录实体类
 * <p>
 * 对应博主申请表（blogger_apply），记录普通用户申请成为博主的审核流程。
 * </p>
 *
 * @author Huixin Blog
 */
@Data
@TableName("blogger_apply")
@Schema(description = "博主申请记录")
public class BloggerApply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请ID（自增）
     */
    @TableField("id")
    @Schema(description = "申请ID")
    private Long id;

    /**
     * 申请人用户ID
     */
    @TableField("user_id")
    @Schema(description = "申请人用户ID", example = "2")
    private Long userId;

    /**
     * 申请理由
     */
    @TableField("apply_reason")
    @Schema(description = "申请理由", example = "我是一名资深Java开发者，希望分享技术文章")
    private String applyReason;

    /**
     * 审核状态：0-待审核，1-通过，2-拒绝
     */
    @TableField("apply_status")
    @Schema(description = "审核状态：0-待审核，1-通过，2-拒绝", example = "0")
    private Integer applyStatus;

    /**
     * 审核意见
     */
    @TableField("review_comment")
    @Schema(description = "审核意见", example = "审核通过，欢迎加入")
    private String reviewComment;

    /**
     * 审核时间
     */
    @TableField("review_time")
    @Schema(description = "审核时间")
    private LocalDateTime reviewTime;

    /**
     * 申请时间
     */
    @TableField("create_time")
    @Schema(description = "申请时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
