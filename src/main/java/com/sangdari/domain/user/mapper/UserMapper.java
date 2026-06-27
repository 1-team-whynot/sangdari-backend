package com.sangdari.domain.user.mapper;

import com.sangdari.domain.user.entities.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findByPk(long userId);
    User findByEmail(String email);
    User findByPhone(String phone);
}