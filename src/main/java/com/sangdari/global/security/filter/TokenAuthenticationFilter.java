package com.sangdari.global.security.filter;

import com.sangdari.global.security.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final SecurityAuthenticationProvider securityAuthenticationProvider;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request
            , @NonNull HttpServletResponse response
            , @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 Access 토큰 추출
        Optional<String> extractedAccessToken = jwtProvider.extractAccessToken(request);

        // Access 토큰이 존재할 때만 인증 로직 실행
        if (extractedAccessToken.isPresent()) {
            try {
                // 인증 정보를 스프링 시큐리티에 전달
                SecurityContextHolder.getContext()
                        .setAuthentication(securityAuthenticationProvider.authentication(extractedAccessToken.get()));
            } catch (Exception e) {
                // 예외를 핸들러 리졸버로 위임시킴(@RestControllerAdvice가 처리하게 됨)
                handlerExceptionResolver.resolveException(request, response, null, e);
                return; // 예외 위임 응답 완료 후 다음 필터 체인을 중단시키기 위해 return
            }
        }

        // 다음 필터를 호출
        filterChain.doFilter(request, response);
    }
}
