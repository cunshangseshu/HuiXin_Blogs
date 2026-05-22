package com.huixin.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置实体类
 * <p>
 * 对应系统配置表（system_config），存储博客名称、公告等系统级配置。
 * </p>
 *
 * @author 爱吃罗氏虾
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_config")
@Schema(description = "系统配置")
public class SystemConfig extends BaseEntity {

    /**
     * 配置键名
     */
    @TableField("config_key")
    @Schema(description = "配置键名", example = "blog_name")
    private String configKey;

    /**
     * 配置值
     */
    @TableField("config_value")
    @Schema(description = "配置值", example = "慧芯博客")
    private String configValue;

    /**
     * 配置项描述
     */
    @TableField("config_desc")
    @Schema(description = "配置项描述", example = "博客名称")
    private String configDesc;

}
