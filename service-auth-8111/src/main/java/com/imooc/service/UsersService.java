package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.Users;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-05-27
 */
public interface UsersService extends IService<Users> {

    /**
     * 根据手机号判断当前用户是否存在
     * @param mobile
     * @return
     */
    public Users queryMobileIsExist(String mobile);

    /**
     * 根据手机号创建用户
     * 用户信息默认
     * @param mobile
     * @return
     */
    public Users createUser(String mobile);

}
