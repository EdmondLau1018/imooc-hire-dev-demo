package com.imooc.api.feign;

import com.imooc.grace.result.GraceJSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 远程调用 work-service 的声明式客户端
 */
@FeignClient("work-service")    //  通过在 nacos 中配置的名称获取远程调用的服务地址
public interface WorkMicroServiceFeign {

    //  远程调用 init 方法接口 路径要写请求的全路径
    @PostMapping("/resume/init")
    public GraceJSONResult initResume(@RequestParam("userId") String userId);
}
