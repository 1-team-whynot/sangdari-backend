package com.sangdari.domain.auth.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper {
    int updateRefreshToken(long userId, String refreshToken);
}
