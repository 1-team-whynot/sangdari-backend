package com.sangdari.global.security.filter;

import com.sangdari.global.config.CorsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CorsConfig corsConfig;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cors 설정 메서드
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 Origin = [Protocol] + [Host(Domain)] + [Port] 설정
        config.setAllowedOrigins(corsConfig.allowedOrigins());

        // 허용할 HTTP 메서드 지정
        config.setAllowedMethods(List.of(
                HttpMethod.GET.name()
                , HttpMethod.POST.name()
                , HttpMethod.PUT.name()
                , HttpMethod.PATCH.name()
                , HttpMethod.DELETE.name()
                , HttpMethod.OPTIONS.name() // Pre-Flight 요청 허용
        ));

        // 허용할 헤더 지정
        config.setAllowedHeaders(List.of(
                HttpHeaders.AUTHORIZATION
                , HttpHeaders.CONTENT_TYPE
                , HttpHeaders.ACCEPT
        ));

        // 자격 증명(Cookie, Credential, 인증 헤더 정보 등등) 포함 여부 설정
        config.setAllowCredentials(true);

        // 브라우저가 Pre-Flight 요청 결과를 캐싱할 시간(초 단위) 설정
        config.setMaxAge(corsConfig.maxAge());

        // 모든 API 경로에 위 설정들을 적용시키는 로직
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
            , SecurityExceptionHandler securityExceptionHandler
            , TokenAuthenticationFilter tokenAuthenticationFilter
    ) throws Exception {
        return http
                // 세션 비활성화 설정
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 화면 생성 비활성 설정
                .httpBasic(AbstractHttpConfigurer::disable)
                // Form Login 기능 비활성 설정
                .formLogin(AbstractHttpConfigurer::disable)
                // CSRF 토큰 인증 비활성 설정
                .csrf(AbstractHttpConfigurer::disable)
                // CORS 설정 추가
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 필터 등록
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // request에 대한 권한 설정
                .authorizeHttpRequests(request ->
                        //
                        request.requestMatchers(HttpMethod.GET, SecurityUrlRegistry.AUTH_REQUIRED_GET_URLS).authenticated()
                                .requestMatchers(HttpMethod.POST, SecurityUrlRegistry.AUTH_REQUIRED_POST_URLS).authenticated()
                                .requestMatchers(HttpMethod.PUT, SecurityUrlRegistry.AUTH_REQUIRED_PUT_URLS).authenticated()
                                .requestMatchers(HttpMethod.PATCH, SecurityUrlRegistry.AUTH_REQUIRED_PATCH_URLS).authenticated()
                                .requestMatchers(HttpMethod.DELETE, SecurityUrlRegistry.AUTH_REQUIRED_DELETE_URLS).authenticated()
                                .anyRequest().permitAll()
                )
                // 예외 처리 설정
                .exceptionHandling(exception ->
                        // 예외 처리 핸들러 등록
                        exception.authenticationEntryPoint(securityExceptionHandler)
                                .accessDeniedHandler(securityExceptionHandler)
                )
                .build();
    }
}
