package com.imooc.service;

import com.imooc.pojo.bo.ModifyUserBO;
import com.imooc.pojo.vo.UsersVO;

public interface UsersService {

    public void modifyUserInfo(ModifyUserBO modifyUserBO);

    public UsersVO getUserinfo(String userId);
}
