package com.imooc.service.impl;

import com.google.gson.Gson;
import com.imooc.base.BaseInfoProperties;
import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.ModifyUserBO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UsersService;
import com.imooc.utils.JWTUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsersServiceImpl extends BaseInfoProperties implements UsersService {

    private final UsersMapper usersMapper;

    private final JWTUtils jwtUtils;

    public UsersServiceImpl(UsersMapper usersMapper, JWTUtils jwtUtils) {
        this.usersMapper = usersMapper;
        this.jwtUtils = jwtUtils;
    }


    @Override
    public void modifyUserInfo(ModifyUserBO modifyUserBO) {

        //  获取用户 id
        String userId = modifyUserBO.getUserId();
        if (StringUtils.isBlank(userId)) {
            //  未查询到当前用户 用户信息修改失败
            GraceException.displayException(ResponseStatusEnum.USER_INFO_UPDATED_ERROR);
        }

        // 进入修改流程
        Users pendingUser = new Users();
        //  设定id 修改时间
        pendingUser.setId(userId);
        pendingUser.setUpdatedTime(LocalDateTime.now());
        //  拷贝信息
        BeanUtils.copyProperties(modifyUserBO, pendingUser);
        usersMapper.updateById(pendingUser);
    }

    @Override
    public UsersVO getUserinfo(String userId) {

        Users users = usersMapper.selectById(userId);
        //  生成新的 jwt Token 信息
        String uToken = jwtUtils.createJWTWithPrefix(TOKEN_ADMIN_PREFIX, new Gson().toJson(users));
        //  信息拷贝
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users, usersVO);
        usersVO.setUserToken(uToken);
        return usersVO;
    }
}
