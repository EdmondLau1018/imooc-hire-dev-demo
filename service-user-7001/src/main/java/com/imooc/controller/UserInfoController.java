package com.imooc.controller;


import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.ModifyUserBO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户信息接口
 */
@Slf4j
@RestController
@RequestMapping("/userinfo")
public class UserInfoController extends BaseInfoProperties {

    private final UsersService usersService;

    public UserInfoController(UsersService usersService) {
        this.usersService = usersService;
    }

    /**
     * 修改用户信息
     *
     * @param modifyUserBO
     * @return
     */
    @PostMapping("/modify")
    public GraceJSONResult modify(@RequestBody ModifyUserBO modifyUserBO) {
        usersService.modifyUserInfo(modifyUserBO);

        UsersVO userInfo = getUserInfo(modifyUserBO.getUserId(), true);
        return GraceJSONResult.ok(userInfo);
    }

    /**
     * 根据 userId 获取用户信息
     *
     * @param userId
     * @return
     */
    public UsersVO getUserInfo(String userId, boolean needJWT) {
        return usersService.getUserinfo(userId);
    }

    /**
     * 根据 企业 id 查询 企业数量，
     * 远程调用接口 company-service 通过 feign 调用这个接口
     *
     * @param companyId
     * @return
     */
    @PostMapping("/getCountsByCompanyId")
    public GraceJSONResult getCountsByCompanyId(@RequestParam("companyId") String companyId) {

        //  从 redis 缓存中获取当前公司对应的 HR 数量
        String hrCountsStr = redis.get(REDIS_COMPANY_HR_COUNTS + ":" + companyId);
        Long hrCounts = 0l;

        // 针对 redis 中的 查询结果执行具体的业务
        if (StringUtils.isBlank(hrCountsStr)) {
            //  redis 中未查询到有关数据 ，从 DB  中查询当前企业绑定的HR 数量
            hrCounts = usersService.getCountsByCompanyId(companyId);

            //  将查询出的结果设置到 redis 缓存中
            redis.set(REDIS_COMPANY_HR_COUNTS + ":" + companyId,
                    hrCounts + "",
                    60 * 60);
        } else {

            //  从 redis 中解析当前企业绑定的 HR 数量
            hrCounts = Long.valueOf(hrCountsStr);
        }
        return GraceJSONResult.ok(hrCounts);
    }

    /**
     * 绑定 HR 用户和对应的 企业关系
     * 远程调用接口
     *
     * @param hrUserId
     * @param realname
     * @param companyId
     * @return
     */
    @PostMapping("/bindingHRToCompany")
    public GraceJSONResult bindingHRToCompany(@RequestParam("hrUserId") String hrUserId,
                                              @RequestParam("realname") String realname,
                                              @RequestParam("companyId") String companyId) {

        //  调用 service 更新用户信息
        usersService.updateUserCompanyId(hrUserId, realname, companyId);

        //  查询更新后的用户信息
        Users hrUser = usersService.getById(hrUserId);

        // 将获取用户的 手机号返回给 调用方
        return GraceJSONResult.ok(hrUser.getMobile());
    }

    /**
     * app 端刷新用户信息
     *
     * @param userId
     * @return
     */
    @PostMapping("/freshUserInfo")
    public GraceJSONResult freshUserInfo(String userId) {

        UsersVO usersVO = getUserInfo(userId, true);
        return GraceJSONResult.ok(usersVO);
    }

    /**
     * 服务远程调用接口，根据用户id 获取单个用户的信息
     * @param userId
     * @return
     */
    @PostMapping("/get")
    public GraceJSONResult get(@RequestParam("userId") String userId) {

        UsersVO usersVO = getUserInfo(userId, false);
        return GraceJSONResult.ok(usersVO);
    }
}
