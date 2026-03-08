package com.rescue.petrescue.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rescue.petrescue.entity.User;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface UserMapper extends BaseMapper<User> {}
