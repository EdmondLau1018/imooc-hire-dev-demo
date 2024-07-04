package com.imooc.api.feign;

import com.imooc.grace.result.GraceJSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserMicroServiceFeign {

    /**
     * 远程调用接口：根据 企业id获取 企业绑定的 HR 数量
     * @param companyId
     * @return
     */
    @PostMapping("/userinfo/getCountsByCompanyId")
    public GraceJSONResult getCountsByCompanyId(@RequestParam("companyId") String companyId);
}
