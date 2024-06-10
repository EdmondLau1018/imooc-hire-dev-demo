package com.imooc.api;

import com.imooc.api.interceptor.JWTCurrentUserInterceptor;
import com.imooc.api.interceptor.SMSInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    /**
     * 将拦截器注入到spring容器中
     *
     * @return
     */
    @Bean
    public SMSInterceptor smsInterceptor() {
        return new SMSInterceptor();
    }

    @Bean
    public JWTCurrentUserInterceptor jwtCurrentUserInterceptor() {
        return new JWTCurrentUserInterceptor();
    }

    /**
     * 注册拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //  SMSCode 拦截器 仅对 配置的路由生效
        registry.addInterceptor(smsInterceptor())
                .addPathPatterns("/passport/getSMSCode");

        //  注册 JWT 用户信息拦截器 ，这个拦截器对所有请求都生效
        registry.addInterceptor(jwtCurrentUserInterceptor())
                .addPathPatterns("/**");
    }
}
