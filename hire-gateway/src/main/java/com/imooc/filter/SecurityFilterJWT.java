package com.imooc.filter;

import com.imooc.base.BaseInfoProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class SecurityFilterJWT extends BaseInfoProperties implements GlobalFilter, Ordered {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final ExcludeUrlProperties excludeUrlProperties;

    public SecurityFilterJWT(ExcludeUrlProperties excludeUrlProperties) {
        this.excludeUrlProperties = excludeUrlProperties;
    }

    /**
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //  通过比对 用户请求路径与 资源文件中的放行路径 判断当前请求是否可以直接放行
        //  获取用户请求路由
        String url = exchange.getRequest().getURI().getPath();
        //  获取资源文件中配置的可以直接放行的路由信息
        List<String> excludeUrls = excludeUrlProperties.getUrls();
        for (String excludeUrl : excludeUrls) {
            //  匹配路由信息 如果一致可以直接放行
            if (antPathMatcher.match(excludeUrl, url)) {
                log.info("匹配到用户请求路由：{} ,直接放行", url);
                return chain.filter(exchange);
            }
        }

        log.info("用户请求：{} 被拦截",url);
        return chain.filter(exchange);   //这行代码代表放行 通过 链路 chain 放行交换机中的对应路由;

    }

    /**
     * 过滤器的顺序，数字越小优先级越高
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
