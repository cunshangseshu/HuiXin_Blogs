package com.huixin.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huixin.common.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口（用户模块）
 *
 * @author Huixin Blog
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
