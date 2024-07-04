package com.imooc.service;

import com.imooc.pojo.bo.ModifyUserBO;
import com.imooc.pojo.vo.UsersVO;

public interface UsersService {

    public void modifyUserInfo(ModifyUserBO modifyUserBO);

    public UsersVO getUserinfo(String userId);

    /**
     *  根据公司 id 获取公司绑定的 HR 数量 （查询的是用户表 HR 信息）
     * @param companyId
     * @return
     */
    public Long getCountsByCompanyId(String companyId);
}
