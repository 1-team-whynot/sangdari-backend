package com.sangdari.domain.user.mapper;

import com.sangdari.domain.user.entities.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findById(long id);

    User findByEmail(String email);
}
