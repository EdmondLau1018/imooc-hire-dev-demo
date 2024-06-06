package com.imooc.filter;

import com.google.gson.Gson;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
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

        log.info("用户请求：{} 被拦截", url);
        return renderErrorMsg(exchange,ResponseStatusEnum.UN_LOGIN);

    }

    public Mono<Void> renderErrorMsg(ServerWebExchange exchange, ResponseStatusEnum responseStatusEnum) {

        //  获得 response
        ServerHttpResponse httpResponse = exchange.getResponse();
        //  构建 jsonResult 错误返回对象
        GraceJSONResult jsonResult = GraceJSONResult.exception(responseStatusEnum.UN_LOGIN);

        //  将返回对象的响应码修改为 500  (HttpStatus 也是一个枚举类)
        httpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);

        //  设定返回对象的相应类型为 json
        if (!httpResponse.getHeaders().containsKey("Content-type")) {
            // 如果返回对象中不包含 Content-type 属性 那么添加这个属性 并且设定为 application/json \
            //  MimeTypeUtils 是 spring Utils 提供的枚举类 包含各种返回类型
            httpResponse.getHeaders().add("Content-type", MimeTypeUtils.APPLICATION_JSON_VALUE);
        }

        //  将 之前构建的 json 对象转换成json字符串 写入到 返回对象中
        String resultJson = new Gson().toJson(jsonResult);
        //  构建成数据流进行写入
        DataBuffer buffer = httpResponse.bufferFactory().wrap(resultJson.getBytes(StandardCharsets.UTF_8));
        return httpResponse.writeWith(Mono.just(buffer));
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
