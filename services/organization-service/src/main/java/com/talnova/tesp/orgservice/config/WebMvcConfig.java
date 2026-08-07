package com.talnova.tesp.orgservice.config;

import com.talnova.tesp.orgservice.security.SubTreeScopeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final SubTreeScopeInterceptor subTreeScopeInterceptor;

    public WebMvcConfig(SubTreeScopeInterceptor subTreeScopeInterceptor) {
        this.subTreeScopeInterceptor = subTreeScopeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(subTreeScopeInterceptor)
                .addPathPatterns("/api/v1/nodes/**");
    }
}
