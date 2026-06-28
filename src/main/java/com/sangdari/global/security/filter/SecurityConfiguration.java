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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(corsConfig.allowedOrigins());

        configuration.setAllowedMethods(List.of(
            HttpMethod.GET.name()
            , HttpMethod.POST.name()
            , HttpMethod.PUT.name()
            , HttpMethod.PATCH.name()
            , HttpMethod.DELETE.name()
            , HttpMethod.OPTIONS.name()
        ));

        configuration.setAllowedHeaders(List.of(
            HttpHeaders.AUTHORIZATION
            , HttpHeaders.CONTENT_TYPE
            , HttpHeaders.ACCEPT
        ));

        configuration.setAllowCredentials(true);

        configuration.setMaxAge(corsConfig.maxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http
        , SecurityExceptionHandler securityExceptionHandler
        , TokenAuthenticationFilter tokenAuthenticationFilter
    ) {
        return http
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 비활성화 설정
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(this.corsConfigurationSource()))
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(req ->
                req.requestMatchers(HttpMethod.GET, SecurityUrlRegistry.AUTH_REQUIRED_GET_URLS).authenticated()
                    .requestMatchers(HttpMethod.POST, SecurityUrlRegistry.AUTH_REQUIRED_POST_URLS).authenticated()
                    .requestMatchers(HttpMethod.PUT, SecurityUrlRegistry.AUTH_REQUIRED_PUT_URLS).authenticated()
                    .requestMatchers(HttpMethod.PATCH, SecurityUrlRegistry.AUTH_REQUIRED_PATCH_URLS).authenticated()
                    .requestMatchers(HttpMethod.DELETE, SecurityUrlRegistry.AUTH_REQUIRED_DELETE_URLS).authenticated()
                    .anyRequest().permitAll() // 그 외는 인증 불필요
            )
            .exceptionHandling(e ->
                e.authenticationEntryPoint(securityExceptionHandler)
                    .accessDeniedHandler(securityExceptionHandler)
                )
                .build();
    }
}
