package com.sangdari.domain.user.entities;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {
    private long userId;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String refreshToken;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
    private String withdrawAt;
}
