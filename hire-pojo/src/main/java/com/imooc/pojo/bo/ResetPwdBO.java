package com.imooc.pojo.bo;

import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.ar.AdminAR;
import com.imooc.utils.MD5Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResetPwdBO {

    private String adminId;
    private String password;
    private String rePassword;

    /**
     * DDD 领域驱动 AR 模式 通过 AR 模型操作数据库
     * 修改密码的方法
     */
    public void modifyPwd() {

        validate();

        //  修改密码的业务逻辑
        AdminAR adminAR = new AdminAR();
        adminAR.setId(adminId);
        // 生成六位随机数作为盐值
        String salt = (int) (Math.random() * 90000) + 100000 + "";
        //  使用盐对当前密码进行加密
        String passwordMD5 = MD5Utils.encrypt(password, salt);
        adminAR.setPassword(passwordMD5);
        adminAR.setSlat(salt);
        adminAR.setUpdatedTime(LocalDateTime.now());

        adminAR.updateById();
    }

    /**
     * 校验密码 和 adminId 的方法
     */
    private void validate() {

        //  密码校验
        if (StringUtils.isBlank(password))
            GraceException.displayException(ResponseStatusEnum.ADMIN_PASSWORD_NULL_ERROR);
        if (StringUtils.isBlank(rePassword))
            GraceException.displayException(ResponseStatusEnum.ADMIN_PASSWORD_NULL_ERROR);
        if (!password.equalsIgnoreCase(rePassword))
            GraceException.displayException(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);

        //  adminId 校验
        //  adminId 判空
        if (StringUtils.isBlank(adminId)) GraceException.displayException(ResponseStatusEnum.ADMIN_NOT_EXIST);
        AdminAR adminAR = new AdminAR();
        adminAR.setId(adminId);
        adminAR = adminAR.selectById();
        if (adminAR == null) GraceException.displayException(ResponseStatusEnum.ADMIN_NOT_EXIST);

    }


}
