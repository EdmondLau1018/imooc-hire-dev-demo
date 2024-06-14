package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.bo.CreateAdminBO;
import com.imooc.service.AdminService;
import com.imooc.utils.PagedGridResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/admininfo")
public class AdminController extends BaseInfoProperties {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
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
}
