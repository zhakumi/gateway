package com.wlyuan.wangcan.gateway.config;

import com.wlyuan.wangcan.gateway.filter.CustomDebugFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomFilterConfig {

    @Bean
    public CustomDebugFilter customDebugFilter() {
        return new CustomDebugFilter();
    }
}
