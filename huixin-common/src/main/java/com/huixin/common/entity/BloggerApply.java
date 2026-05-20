package com.huixin.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("blogger_apply")
@Schema(description = "博主申请记录")
public class BloggerApply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "申请ID")
    private Long id;

    @TableField("user_id")
    @Schema(description = "申请人用户ID", example = "2")
    private Long userId;

    @TableField("apply_reason")
    @Schema(description = "申请理由", example = "我是一名资深Java开发者，希望分享技术文章")
    private String applyReason;

    @TableField("apply_status")
    @Schema(description = "审核状态：0-待审核，1-通过，2-拒绝", example = "0")
    private Integer applyStatus;

    @TableField("review_comment")
    @Schema(description = "审核意见", example = "审核通过，欢迎加入")
    private String reviewComment;

    @TableField("review_time")
    @Schema(description = "审核时间")
    private LocalDateTime reviewTime;

    @TableField("create_time")
    @Schema(description = "申请时间")
    private LocalDateTime createTime;

    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
