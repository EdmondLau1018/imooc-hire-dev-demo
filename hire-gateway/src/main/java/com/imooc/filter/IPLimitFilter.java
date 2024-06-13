package com.imooc.filter;

import com.google.gson.Gson;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
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

@Component
@Slf4j
public class IPLimitFilter extends BaseInfoProperties implements GlobalFilter, Ordered {

    @Value("${blackIp.continueCounts}")
    private Integer continueCounts;

    @Value("${blackIp.timeInterval}")
    private Integer timeInterval;

    @Value("${blackIp.limitTimes}")
    private Integer limitTimes;


    private ExcludeUrlProperties excludeUrlProperties;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public IPLimitFilter(ExcludeUrlProperties excludeUrlProperties) {
        this.excludeUrlProperties = excludeUrlProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //  获取 用户请求的 url 路径
        String url = exchange.getRequest().getURI().getPath();

        // 获得所有需要进行 IP 限流校验的请求路径
        List<String> ipLimitUrls = excludeUrlProperties.getIpLimitUrls();
        //  遍历这个列表 与请求中的 url 路径比对 是否需要校验
        for (String ipLimitUrl : ipLimitUrls) {
            if (antPathMatcher.matchStart(ipLimitUrl, url)) {
                //  匹配到对应的请求路径 说明这个 ip 需要限流校验
                doLimit(exchange, chain);
            }
        }
        //  没有捕获到需要限流 的 请求路径 默认放行
        return chain.filter(exchange);
    }

    /**
     * 对 ip 进行限制 限流
     *
     * @return
     */
    public Mono<Void> doLimit(ServerWebExchange exchange, GatewayFilterChain chain) {

        //  根据 request 对象获取请求 ip
        ServerHttpRequest request = exchange.getRequest();
        String userIp = IPUtil.getIP(request);

        // 构建 redis 中存储的 ip 信息 记录当前 ip 是否被限流
        final String ipRediskey = "gateway-ip:" + userIp;
        final String ipRedisLimitKey = "gateway-ip-limit:" + userIp;
        //  检查 当前 ip 在 redis 中是否被标记为限流 ip 时间是否超过限流时间
        long limitLeftTime = redis.ttl(ipRedisLimitKey);
        if (limitLeftTime > 0) {
            // 当前 IP 正在被限流
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }

        // 若当前 ip 是否第一次请求服务 给对应的请求 count + 1  并且设定过期时间
        long requestCounts = redis.increment(ipRediskey, 1);
        if (requestCounts == 1) {
            //  这个 IP 之前没有请求服务 ，设定访问时间
            redis.expire(ipRediskey, timeInterval);
        }

        //  一旦在规定时间 【limitTimes】 内超过访问次数 则进行 黑名单标记（ip 在redis中标记小黑屋）
        if (requestCounts > continueCounts) {
            redis.set(ipRedisLimitKey, ipRedisLimitKey, limitTimes);
            return renderErrorMsg(exchange,ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }

        return chain.filter(exchange);

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

    @Override
    public int getOrder() {
        return 1;
    }
}
