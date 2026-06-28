package com.sangdari.global.response;

import lombok.Builder;

@Builder
public record GlobalResponse<T>(
        String code
        , String message
        , T data
) {
}
