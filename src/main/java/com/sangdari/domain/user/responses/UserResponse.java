package com.sangdari.domain.user.responses;

import lombok.Builder;

@Builder
public record UserResponse(
        long userId
        , String email
        , String name
        , String phone
        , String createdAt
) {
}
