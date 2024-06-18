package com.imooc;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JasyptTest {

    /**
     * 测试加密流程
     */
    @Test
    public void testEncrypt() {

        //  实例化加密器
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

        //  配置加密算法和密钥
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPassword("Sharn");
        encryptor.setConfig(config);

        //设置原密码 打印输出加密后的密码
        String oriPassword = "imooc";
        String enPassword = encryptor.encrypt(oriPassword);

        System.out.println(enPassword);
    }

    /**
     * 测试解密算法
     */
    @Test
    public void testDecrypt(){

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPassword("Sharn");
        encryptor.setConfig(config);

        String passwordEn = "vd9djC5TY3egQ0W8tDEymA==";

        String decryptPwd = encryptor.decrypt(passwordEn);
        System.out.println(decryptPwd);

    }

}
