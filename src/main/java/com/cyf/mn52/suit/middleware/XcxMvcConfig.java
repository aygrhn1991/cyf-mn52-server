package com.cyf.mn52.suit.middleware;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class XcxMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new XcxInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login", "/admin/doLogin");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("admin/login");
    }

}
