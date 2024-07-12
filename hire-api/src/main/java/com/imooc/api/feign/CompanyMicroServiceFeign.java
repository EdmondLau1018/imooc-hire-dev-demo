package com.imooc.api.feign;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.bo.SearchBO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient("company-service")
public interface CompanyMicroServiceFeign {

    /**
     * 远程调用接口：根据企业 id 查询企业信息列表
     * @param searchBO
     * @return
     */
    @PostMapping("/list/get")
    public GraceJSONResult getList(@RequestBody @Valid SearchBO searchBO);
}
