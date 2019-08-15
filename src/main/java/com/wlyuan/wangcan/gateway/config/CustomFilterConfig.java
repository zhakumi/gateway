package com.wlyuan.wangcan.gateway.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.wlyuan.wangcan.gateway.filter.CustomDebugFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.UnsupportedEncodingException;

@Configuration
public class CustomFilterConfig {

    @Bean
    public CustomDebugFilter customDebugFilter() {
        return new CustomDebugFilter();
    }

    @Value("${api.auth.iss}")
    private String iss;
    @Value("${api.auth.secret}")
    private String secret;



    @Bean
    public Algorithm algorithm() {
        try {
            return Algorithm.HMAC256(secret);
        } catch (IllegalArgumentException | UnsupportedEncodingException e) {
            return null;
        }
    }

    @Bean
    public JWTVerifier jWTVerifier() {
        return JWT.require(algorithm()).withIssuer(iss).build();
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setMaxAge(3600L);
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}
