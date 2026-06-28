package com.sangdari.global.responses;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GlobalResponse<T> {
    private String code;
    private String message;
    private T data;
}
