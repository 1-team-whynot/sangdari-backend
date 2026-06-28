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
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        Optional<String> tokenOptional = jwtProvider.extractAccessToken(request);

        if(tokenOptional.isPresent()) {
            try {
                SecurityContextHolder.getContext().setAuthentication(securityAuthenticationProvider.authentication(tokenOptional.get()));
            } catch (Exception e) {
                handlerExceptionResolver.resolveException(request, response, null, e);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
