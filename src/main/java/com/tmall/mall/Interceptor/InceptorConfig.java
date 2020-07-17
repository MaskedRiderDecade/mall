package com.tmall.mall.Interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserLoginInInterceptor())
                .addPathPatterns("/**").excludePathPatterns(
                        "/error","/user/login", "/user/register","/categories","/products","/products/*");
    }
}
