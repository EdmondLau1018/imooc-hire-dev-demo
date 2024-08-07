package com.imooc.service;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.ModifyUserBO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

public interface UsersService {

    public void modifyUserInfo(ModifyUserBO modifyUserBO);

    public UsersVO getUserinfo(String userId);

    /**
     * 根据公司 id 获取公司绑定的 HR 数量 （查询的是用户表 HR 信息）
     *
     * @param companyId
     * @return
     */
    public Long getCountsByCompanyId(String companyId);

    public void updateUserCompanyId(String hrUserId, String realname, String companyId);

    public Users getById(String userId);

    /**
     * 将提交企业的用户身份修改为 HR
     *
     * @param hrUserId
     */
    public void updateUserToHR(String hrUserId);

    /**
     * 分页查询当前企业对应的用户信息
     * @param companyId
     * @param page
     * @param limit
     * @return
     */
    public PagedGridResult getHrList(String companyId, Integer page, Integer limit);

    /**
     * hr 用户离职公司  修改为普通用户
     * @param hrUserId
     */
    public void updateUsersToCand(String hrUserId);

    /**
     * 根据用户 id 获取用户信息
     * @param userIds
     * @return
     */
    public List<Users> getByIds(List<String> userIds);
}
