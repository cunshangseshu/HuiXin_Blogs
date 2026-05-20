package com.huixin.common.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 * <p>
 * 所有数据库实体类均继承此类，提供通用字段：
 * id（主键自增）、createTime、updateTime、isDeleted（逻辑删除）。
 * 配合MyBatis Plus自动填充和逻辑删除功能使用。
 * </p>
 *
 * @author Huixin Blog
 */
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间（插入时自动填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间（插入和更新时自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识：0-未删除，1-已删除
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted;

    /* ==================== Getter/Setter ==================== */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

}
