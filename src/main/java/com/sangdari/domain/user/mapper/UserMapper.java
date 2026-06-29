package com.sangdari.domain.user.mapper;

import com.sangdari.domain.user.entities.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findByPk(long userId);
    User findByEmail(String email);
    User findByPhone(String phone);
    int updateUserInfo(@Param("userId") Long userId, @Param("name") String name, @Param("phone") String phone);
    User findByEmailAndNameAndPhone(@Param("email") String email, @Param("name") String name, @Param("phone") String phone);
    int updateUserPassword(@Param("userId") Long userId, @Param("password") String password);
    int withdrawUser(long userId);
}