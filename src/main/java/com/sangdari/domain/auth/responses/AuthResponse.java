package com.sangdari.domain.auth.responses;

import com.sangdari.domain.user.responses.UserResponse;
import lombok.Builder;

@Builder
public record AuthResponse(
        UserResponse userResponse
        , String accessToken
) {
}
