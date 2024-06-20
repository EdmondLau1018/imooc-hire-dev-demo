package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.api.feign.WorkMicroServiceFeign;
import com.imooc.enums.Sex;
import com.imooc.enums.ShowWhichName;
import com.imooc.enums.UserRole;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.service.UsersService;
import com.imooc.utils.DesensitizationUtil;
import com.imooc.utils.LocalDateUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Sharn
 * @since 2024-05-27
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    private final UsersMapper usersMapper;

    private final WorkMicroServiceFeign workMicroServiceFeign;

    private static final String USER_FACE1 = "http://122.152.205.72:88/group1/M00/00/05/CpoxxF6ZUySASMbOAABBAXhjY0Y649.png";

    public UsersServiceImpl(UsersMapper usersMapper, WorkMicroServiceFeign workMicroServiceFeign) {
        this.usersMapper = usersMapper;
        this.workMicroServiceFeign = workMicroServiceFeign;
    }

    /**
     * 根据手机号查询用户是否在数据库中存在
     * @param mobile
     * @return
     */
    @Override
    public Users queryMobileIsExist(String mobile) {
        Users users = usersMapper.selectOne(new QueryWrapper<Users>().eq("mobile", mobile));
        return users;
    }

    @Override
    public Users createUser(String mobile) {

        Users user = new Users();

        // 用户基本（名称）信息
        user.setMobile(mobile);
        user.setNickname("用户" + DesensitizationUtil.commonDisplay(mobile));
        user.setRealName("用户" +  DesensitizationUtil.commonDisplay(mobile));
        user.setShowWhichName(ShowWhichName.realname.type);

        //  用户个人信息（info）
        user.setSex(Sex.secret.type);
        user.setFace(USER_FACE1);
        user.setEmail("");

        //  用户生日信息
        LocalDate localDateBirthday = LocalDateUtils.parseLocalDate("1960-01-01", LocalDateUtils.DATE_PATTERN);
        user.setBirthday(localDateBirthday);

        //  用户所在地区： region
        user.setCountry("中国大陆");
        user.setProvince("");
        user.setCity("");
        user.setDistrict("");

        //  用户个人描述
        user.setDescription("当前用户未添加个人描述");

        //  用户参加工作的信息
        user.setStartWorkDate(LocalDate.now());
        user.setPosition("程序员");
        user.setRole(UserRole.CANDIDATE.type);
        user.setHrInWhichCompanyId("");

        //  用户信息创建时间
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        //  调用 mapper 信息入库
        usersMapper.insert(user);

        //  调用远程服务，初始化简历（在简历表中新增一条记录）
        workMicroServiceFeign.initResume(user.getId());

        return user;
    }
}
