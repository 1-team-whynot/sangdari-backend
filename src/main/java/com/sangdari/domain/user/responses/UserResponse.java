package com.sangdari.domain.user.responses;

import lombok.Builder;

@Builder
public record UserResponse(
        Long userId,
        String email,
        String name,
        String createdAt
) {
}
