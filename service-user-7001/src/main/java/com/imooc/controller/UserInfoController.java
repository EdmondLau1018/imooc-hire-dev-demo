package com.imooc.controller;


import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.bo.ModifyUserBO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @param modifyUserBO
     * @return
     */
    @PostMapping("/modify")
    public GraceJSONResult modify(@RequestBody ModifyUserBO modifyUserBO) {
        usersService.modifyUserInfo(modifyUserBO);

        UsersVO userInfo = getUserInfo(modifyUserBO.getUserId());
        return GraceJSONResult.ok(userInfo);
    }

    /**
     * 根据 userId 获取用户信息
     * @param userId
     * @return
     */
    private UsersVO getUserInfo(String userId) {
        return usersService.getUserinfo(userId);
    }
}
