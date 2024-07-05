package com.imooc.api.feign;

import com.imooc.grace.result.GraceJSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserMicroServiceFeign {

    /**
     * 远程调用接口：根据 企业id获取 企业绑定的 HR 数量
     *
     * @param companyId
     * @return
     */
    @PostMapping("/userinfo/getCountsByCompanyId")
    public GraceJSONResult getCountsByCompanyId(@RequestParam("companyId") String companyId);

    /**
     * 远程调用接口：绑定 HR 和 企业关系 （更新用户表的 in_which_company_id 字段）
     *
     * @param hrUserId
     * @param realname
     * @param companyId
     * @return
     */
    @PostMapping("/userinfo/bindingHRToCompany")
    public GraceJSONResult bindingHRToCompany(@RequestParam("hrUserId") String hrUserId,
                                              @RequestParam("realname") String realname,
                                              @RequestParam("companyId") String companyId);

    /**
     * 远程调用接口 根据 userId 查询当前用户信息
     *
     * @param userId
     * @return
     */
    @PostMapping("/userinfo/get")
    public GraceJSONResult get(@RequestParam("userId") String userId);

    /**
     * 将当前企业提交审核的用户信息修改为 hr
     * @param hrUserId
     * @return
     */
    @PostMapping("/userinfo/changeUserToHR")
    public GraceJSONResult changeUserToHR(@RequestParam("hrUserId") String hrUserId);

}
