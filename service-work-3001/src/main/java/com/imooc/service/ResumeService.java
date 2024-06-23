package com.imooc.service;

/**
 * <p>
 * 简历表 服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-06-20
 */
public interface ResumeService  {

    /**
     * 根据 userid 初始化用户简历
     * @param userId
     */
    public void initResume(String userId,String msgId);

}
