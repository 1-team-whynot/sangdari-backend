package com.sangdari.global.exception.custom;

public class AuthLoginRequiredException extends RuntimeException {
  public AuthLoginRequiredException() {
    super("로그인이 필요한 기능입니다.");
  }
}
