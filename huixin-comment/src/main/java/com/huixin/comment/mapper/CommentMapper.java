package com.huixin.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huixin.common.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论Mapper接口
 *
 * @author 爱吃罗氏虾
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

}
