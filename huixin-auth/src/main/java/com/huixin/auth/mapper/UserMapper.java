package com.huixin.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huixin.common.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口（认证模块）
 * <p>
 * 继承MyBatis Plus的BaseMapper，自动获得CRUD能力。
 * 用于登录时的用户名/邮箱查找用户。
 * </p>
 *
 * @author Huixin Blog
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
