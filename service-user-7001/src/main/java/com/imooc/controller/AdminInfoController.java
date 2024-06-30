package com.imooc.controller;

import com.imooc.api.interceptor.JWTCurrentUserInterceptor;
import com.imooc.base.BaseInfoProperties;
import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Admin;
import com.imooc.pojo.bo.AdminBO;
import com.imooc.pojo.bo.CreateAdminBO;
import com.imooc.pojo.bo.ResetPwdBO;
import com.imooc.pojo.bo.UpdateAdminBO;
import com.imooc.pojo.vo.AdminInfoVO;
import com.imooc.service.AdminService;
import com.imooc.utils.PagedGridResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/admininfo")
public class AdminInfoController extends BaseInfoProperties {

    private final AdminService adminService;

    public AdminInfoController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 用已有的管理员账户分配账号
     * 创建新的管理员账号
     *
     * @param createAdminBO
     * @return
     */
    @PostMapping("/create")
    public GraceJSONResult create(@RequestBody @Valid CreateAdminBO createAdminBO) {
        adminService.createAdmin(createAdminBO);
        return GraceJSONResult.ok();
    }

    /**
     * 获取 用户名相似的 管理员列表 模糊查询
     *
     * @param accountName
     * @param page
     * @param limit
     * @return
     */
    @PostMapping("/list")
    public GraceJSONResult list(String accountName, Integer page, Integer limit) {

        //  设定参数默认值
        if (page == null) page = 1;
        if (limit == null) limit = 10;

        PagedGridResult gridResult = adminService.getAdminList(accountName, page, limit);

        return GraceJSONResult.ok(gridResult);
    }

    /**
     * 根据 username 查找并删除对应的管理员用户信息
     *
     * @param username
     * @return
     */
    @PostMapping("/delete")
    public GraceJSONResult delete(String username) {

        adminService.deleteAdmin(username);
        return GraceJSONResult.ok();
    }

    /**
     * 使用 DDD 的 AR 模式进行 admin 用户 密码修改接口
     *
     * @param resetPwdBO
     * @return
     */
    @PostMapping("/resetPwd")
    public GraceJSONResult resetPwd(@RequestBody ResetPwdBO resetPwdBO) {
        resetPwdBO.modifyPwd();
        return GraceJSONResult.ok();
    }

    @PostMapping("/myInfo")
    public GraceJSONResult myInfo() {

        //  获取当前 ThreadLocal 中存储的 admin 用户信息
        Admin admin = JWTCurrentUserInterceptor.adminUser.get();
        if (admin == null) {
            admin = adminService.getAdminInfoByName("admin");
        }
        //  根据 当前线程对象中的 admin 用户信息查找对应的 admin 用户信息
        Admin adminInfo = adminService.getById(admin.getId());
        //  对象信息拷贝 保留前端需要的信息
        AdminInfoVO adminInfoVO = new AdminInfoVO();
        BeanUtils.copyProperties(adminInfo, adminInfoVO);

        //  返回结果
        return GraceJSONResult.ok(adminInfoVO);
    }

    @PostMapping("/updateMyInfo")
    public GraceJSONResult updateMyInfo(@RequestBody @Valid UpdateAdminBO updateAdminBO){

        //  从 ThreadLocal 对象中获取当前登陆的 admin 用户
        Admin currentAdmin = JWTCurrentUserInterceptor.adminUser.get();
        if (currentAdmin == null) {
            currentAdmin = adminService.getAdminInfoByName("admin");
        }

        updateAdminBO.setId(currentAdmin.getId());
        adminService.updateAdmin(updateAdminBO);

        return GraceJSONResult.ok();
    }
}
