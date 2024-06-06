package com.imooc.filter;

import com.google.gson.Gson;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.utils.JWTUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
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

    private final JWTUtils jwtUtils;

    public SecurityFilterJWT(ExcludeUrlProperties excludeUrlProperties, JWTUtils jwtUtils) {
        this.excludeUrlProperties = excludeUrlProperties;
        this.jwtUtils = jwtUtils;
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

        log.info("当前用户请求：{}", url);
        //  拦截到的 请求 ，进行 JWT 校验业务逻辑
        //  获取请求中 headers 中的 JWT 信息
        HttpHeaders headers = exchange.getRequest().getHeaders();
        //  通过名称获取 请求 headers 中携带的用户信息
        String userToken = headers.getFirst("headerUserToken");
        //  判断 header 中的用户信息是否符合协议标准
        //  判断 header 中的信息是否为空
        if (StringUtils.isNotBlank(userToken)) {
            //  不为空 判断 userToken 是否 通过规定的 @ 进行分割
            String[] tokenArr = userToken.split("@");
            if (tokenArr.length < 2) {
                //  token 不符合预先规定的格式
                return renderErrorMsg(exchange, ResponseStatusEnum.UN_LOGIN);
            }

            // 获取 jwt 对应的 令牌 和 subject 信息
            String prefix = tokenArr[0];
            String jwt = tokenArr[1];

            //  判断 prefix 是从哪个端发送过来的请求 然后根据对应的 端 确定 对应的key
            if (prefix.equalsIgnoreCase(TOKEN_USER_PREFIX)) {
                return dealJWT(jwt, exchange, chain, APP_USER_JSON);
            } else if (prefix.equalsIgnoreCase(TOKEN_SAAS_PREFIX)) {
                return dealJWT(jwt, exchange, chain, SAAS_USER_JSON);
            } else {
                return dealJWT(jwt, exchange, chain, ADMIN_USER_JSON);
            }

        }
        return renderErrorMsg(exchange, ResponseStatusEnum.UN_LOGIN);

    }

    /**
     * 用于调用 工具方法 checkJWT(jwt 校验) 的方法
     * 返回 Mono 对象 在这个方法中可以抛出异常 或者 让链路放行当前请求
     *
     * @param jwt
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> dealJWT(String jwt, ServerWebExchange exchange, GatewayFilterChain chain, String key) {

        try {
            //  如果校验成功 返回的是 对应用户信息的 json 字符串 如果校验失败则抛出异常
            String body = jwtUtils.checkJWT(jwt);
            log.info("JWT 校验成功，userToken = {}", body);

            //  将解析出的 用户信息 存到 header 中 （重构 exchange）
            ServerWebExchange newExchange = setExchangeHeader(exchange, key, body);
            return chain.filter(newExchange);
        } catch (ExpiredJwtException e) {

            //  捕获异常 JWT 信息失效（超时重新登录）
            e.printStackTrace();
            return renderErrorMsg(exchange, ResponseStatusEnum.JWT_EXPIRE_ERROR);
        } catch (Exception e) {

            //  捕获异常 JWT 解析失败 （重新登录）
            e.printStackTrace();
            return renderErrorMsg(exchange, ResponseStatusEnum.JWT_SIGNATURE_ERROR);
        }

    }

    /**
     * 将 JWT 解析后出现的用户 信息 封装在header 中
     * 通过重构 request 和 exchange 实现
     *
     * @param exchange
     * @param key
     * @param value
     * @return
     */
    public ServerWebExchange setExchangeHeader(ServerWebExchange exchange, String key, String value) {
        //  构建一个 带有用户信息的 新的 request 对象
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(key, value)
                .build();

        //  用新的 request 对象替换旧的 构建新的 exchange 对象
        ServerWebExchange newExchange = exchange.mutate().request(request).build();

        return newExchange;
    }

    public Mono<Void> renderErrorMsg(ServerWebExchange exchange, ResponseStatusEnum responseStatusEnum) {

        //  获得 response
        ServerHttpResponse httpResponse = exchange.getResponse();
        //  构建 jsonResult 错误返回对象
        GraceJSONResult jsonResult = GraceJSONResult.exception(responseStatusEnum);

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
