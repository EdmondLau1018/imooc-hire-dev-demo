package com.imooc;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class StaticResourceConfig extends WebMvcConfigurationSupport {

    /**
     * 添加静态资源的映射路径
     * 将服务器上指定路径的资源文件映射成对外暴露的指定的地址
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {

        /**
         * addResourceHandler ：对外暴露的访问路径
         * addResourceLocations：服务器上的资源文件路径
         */
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:F:\\personal_codes_for_git\\imgs\\");
        super.addResourceHandlers(registry);
    }
}
