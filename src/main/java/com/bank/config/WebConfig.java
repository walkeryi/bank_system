package com.bank.config;

import com.bank.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    /** 将 /static/** 映射到 classpath:/static/，使页面可用 /static/js、/static/css 绝对路径引用 */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry r) {
        r.addInterceptor(new LoginInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/login", "/api/login", "/logout", "/static/**", "/webjars/**", "/favicon.ico", "/error");
    }
}
