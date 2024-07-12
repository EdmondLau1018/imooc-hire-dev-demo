package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import com.imooc.base.BaseInfoProperties;
import com.imooc.enums.UserRole;
import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.ModifyUserBO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UsersService;
import com.imooc.utils.JWTUtils;
import com.imooc.utils.PagedGridResult;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 根据公司  id 获取当前公司 HR 绑定的数量
     *
     * @param companyId
     * @return
     */
    @Override
    public Long getCountsByCompanyId(String companyId) {

        Long counts = usersMapper.selectCount(new QueryWrapper<Users>()
                .eq("hr_in_which_company_id", companyId));

        return counts;
    }

    /**
     * 根据 hr 用户 id 更新对应企业的 id 实现方法
     * 业务上：关联 hr 用户和企业关系
     *
     * @param hrUserId
     * @param realname
     * @param companyId
     */
    @Transactional
    @Override
    public void updateUserCompanyId(String hrUserId, String realname, String companyId) {

        Users hrUser = new Users();
        hrUser.setId(hrUserId);
        hrUser.setRealName(realname);
        hrUser.setHrInWhichCompanyId(companyId);

        hrUser.setUpdatedTime(LocalDateTime.now());

        usersMapper.updateById(hrUser);
    }

    @Override
    public Users getById(String userId) {
        return usersMapper.selectById(userId);
    }

    /**
     * 将提交企业的用户身份修改为 HR
     *
     * @param hrUserId
     */
    @Transactional
    @Override
    public void updateUserToHR(String hrUserId) {

        //    * 结合 MP 的更新策略为 not_empty 新建对象的其他空属性不会更新到数据库中
        Users hrUser = new Users();
        hrUser.setId(hrUserId);
        //  将用户的角色设置成 招聘者
        hrUser.setRole(UserRole.RECRUITER.type);
        hrUser.setUpdatedTime(LocalDateTime.now());

        usersMapper.updateById(hrUser);
    }

    /**
     * 分页查询当前当前企业对应的 hr 列表
     *
     * @param companyId
     * @param page
     * @param limit
     * @return
     */
    @Override
    public PagedGridResult getHrList(String companyId, Integer page, Integer limit) {

        PageHelper.startPage(page, limit);

        //  分页查询 当前企业下 绑定的 hr 用户信息
        List<Users> hrUsersList = usersMapper.selectList(new QueryWrapper<Users>()
                .eq("hr_in_which_company_id", companyId));

        return setterPagedGrid(hrUsersList, page);
    }

    /**
     * 更新当前 hr 用户 角色变成普通用户
     *
     * @param hrUserId
     */
    @Override
    public void updateUsersToCand(String hrUserId) {

        Users hrUser = new Users();
        hrUser.setId(hrUserId);
        hrUser.setRole(UserRole.CANDIDATE.type);

        hrUser.setHrInWhichCompanyId("0");

        hrUser.setUpdatedTime(LocalDateTime.now());

        usersMapper.updateById(hrUser);
    }

    /**
     * 根据 id 列表查询用户信息
     *
     * @param userIds
     * @return
     */
    @Override
    public List<Users> getByIds(List<String> userIds) {

        List<Users> usersList = usersMapper.selectList(new QueryWrapper<Users>()
                .in("id", userIds));
        return usersList;
    }
}
