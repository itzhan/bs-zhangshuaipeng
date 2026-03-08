package com.rescue.petrescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rescue.petrescue.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
