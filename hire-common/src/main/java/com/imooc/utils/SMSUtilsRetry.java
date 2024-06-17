package com.imooc.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

/**
 * 用于模拟短信发送失败的工具
 */
@Slf4j
public class SMSUtilsRetry {
    public static boolean sendSMSCode() {

        //  apache 提供的 工具 用于生成随机值
        int i = RandomUtils.nextInt(0, 4);
        //  根据生成的随机数，模拟错误抛出异常
        switch (i) {
            case 0:
                throw new IllegalArgumentException("参数有误，传入的参数为 ：" + i);
            case 1:
                throw new NullPointerException("触发兜底异常捕获方法 ：" + i);
            case 2:
                throw new IllegalArgumentException("参数有误，传入的参数为 ：" + i);
            case 3:
                throw new ArrayIndexOutOfBoundsException("参数有误，传入的参数为： " + i);
            default:
                return false;
        }
    }
}


