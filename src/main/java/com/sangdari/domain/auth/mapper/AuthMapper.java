package com.sangdari.domain.auth.mapper;

import com.sangdari.domain.user.entities.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper {
    int updateRefreshToken(long userId, String refreshToken);

    int create(User user);
}
