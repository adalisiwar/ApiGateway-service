package com.example.api_gateway.config;

import com.example.api_gateway.filter.JwtAuthenticationFilter;
import com.example.api_gateway.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                "/api/auth/**",
                                "/actuator/**",
                                "/health/**",
                                "/api/gateway/health",
                                "/api/gateway/info",
                                "/api/gateway/ready"
                        ).permitAll()
                        .pathMatchers(HttpMethod.GET,
                                "/api/restaurants/**",
                                "/api/menu/**",
                                "/api/delivery/**",
                                "/api/reviews/**"
                        ).permitAll()
                        .pathMatchers(HttpMethod.POST,
                                "/api/restaurants/**",
                                "/api/menu/**",
                                "/api/delivery-personnel/**",
                                "/api/reviews/**"
                        ).authenticated()
                        .pathMatchers(HttpMethod.PUT,
                                "/api/restaurants/**",
                                "/api/menu/**",
                                "/api/delivery-personnel/**",
                                "/api/reviews/**"
                        ).authenticated()
                        .pathMatchers(HttpMethod.DELETE,
                                "/api/restaurants/**",
                                "/api/menu/**",
                                "/api/delivery-personnel/**",
                                "/api/reviews/**"
                        ).authenticated()
                        .pathMatchers("/api/users/**").authenticated()
                        .pathMatchers("/api/orders/**").authenticated()
                        .pathMatchers("/api/order-history/**").authenticated()
                        .pathMatchers("/api/payments/**").authenticated()
                        .pathMatchers("/api/notifications/**").authenticated()
                        .anyExchange().authenticated()
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:3001",
                "http://localhost:8080",
                "http://localhost:8081",
                "http://localhost:8082"
        ));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList(
                "Content-Type",
                "Authorization",
                "X-Requested-With",
                "X-User-Id",
                "X-User-Email",
                "X-User-Role"
        ));
        corsConfig.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-User-Id",
                "X-User-Email",
                "X-User-Role"
        ));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}
