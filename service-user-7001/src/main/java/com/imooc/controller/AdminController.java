package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.bo.CreateAdminBO;
import com.imooc.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @param createAdminBO
     * @return
     */
    @PostMapping("/create")
    public GraceJSONResult create(@RequestBody @Valid CreateAdminBO createAdminBO){
        adminService.createAdmin(createAdminBO);
        return GraceJSONResult.ok();
    }
}
