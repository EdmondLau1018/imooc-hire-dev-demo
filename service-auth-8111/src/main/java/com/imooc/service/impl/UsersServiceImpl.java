package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.api.feign.WorkMicroServiceFeign;
import com.imooc.api.mq.InitResumeMQConfig;
import com.imooc.enums.Sex;
import com.imooc.enums.ShowWhichName;
import com.imooc.enums.UserRole;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.service.UsersService;
import com.imooc.utils.DesensitizationUtil;
import com.imooc.utils.LocalDateUtils;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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

    private final RabbitTemplate rabbitTemplate;

    private static final String USER_FACE1 = "http://122.152.205.72:88/group1/M00/00/05/CpoxxF6ZUySASMbOAABBAXhjY0Y649.png";

    public UsersServiceImpl(UsersMapper usersMapper, WorkMicroServiceFeign workMicroServiceFeign, RabbitTemplate rabbitTemplate) {
        this.usersMapper = usersMapper;
        this.workMicroServiceFeign = workMicroServiceFeign;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 根据手机号查询用户是否在数据库中存在
     *
     * @param mobile
     * @return
     */
    @Override
    public Users queryMobileIsExist(String mobile) {
        Users users = usersMapper.selectOne(new QueryWrapper<Users>().eq("mobile", mobile));
        return users;
    }

//    @GlobalTransactional
    @Transactional
    @Override
    public Users createUser(String mobile) {

        Users user = new Users();

        // 用户基本（名称）信息
        user.setMobile(mobile);
        user.setNickname("用户" + DesensitizationUtil.commonDisplay(mobile));
        user.setRealName("用户" + DesensitizationUtil.commonDisplay(mobile));
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
//        GraceJSONResult graceJSONResult = workMicroServiceFeign.initResume(user.getId());
//
//        //  判断远程调用的服务返回结果是否正确 如果不正确 手动回滚事务
//        if (graceJSONResult.getStatus() != 200) {
//
//            try {
//                //  手动回滚当前事务
//                if (StringUtils.isNotBlank(RootContext.getXID())) {
//                    GlobalTransaction globalTransaction = GlobalTransactionContext.reload(RootContext.getXID());
//                    globalTransaction.rollback();
//                }
//            } catch (TransactionException e) {
//                e.printStackTrace();
//            } finally {
//                //  返回当前 用户注册错误
//                GraceException.displayException(ResponseStatusEnum.USER_REGISTER_ERROR);
//            }
//        }

        return user;
    }

    /**
     * 创建用户 发送初始化简历消息 到 MQ
     *
     * @param mobile
     * @return
     */
    @Override
    public Users createUserAndInitResumeMQ(String mobile) {

        //  调用创建用户的方法
        Users user = createUser(mobile);
        //  向 MQ 发送 消息
        rabbitTemplate.convertAndSend(InitResumeMQConfig.INIT_RESUME_EXCHANGE,
                InitResumeMQConfig.INIT_RESUME_ROUTING_KEY,
                user.getId(),
                //  给发送的消息新增 关联 id
                new CorrelationData(UUID.randomUUID().toString()));

        return user;
    }
}
