package com.imooc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

//@Configuration
public class CorsConfig {

    /**
     * 解决跨域请求的 网关 配置
     * 返回的新的 CorsWebFilter 通过这个对象添加允许跨域的配置
     * @return
     */
    @Bean
    public CorsWebFilter corsWebFilter() {

        //  添加允许跨域的配置信息
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //  允许各种域名的请求对当前服务进行调用
        corsConfiguration.addAllowedOriginPattern("*");
        //  设置是否允许发送 cookie 信息
        corsConfiguration.setAllowCredentials(true);
        //  设置允许的请求方式
        corsConfiguration.addAllowedMethod("*");
        //  设置允许的 header
        corsConfiguration.addAllowedHeader("*");

        //  为 url 添加映射路径 定义 corSource 跨域过滤器
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        //  返回重新定义好的 corSource
        return new CorsWebFilter(source);
    }

}
