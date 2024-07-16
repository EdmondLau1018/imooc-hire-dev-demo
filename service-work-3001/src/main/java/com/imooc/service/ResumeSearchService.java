package com.imooc.service;

public interface ResumeSearchService {

    /**
     * 获取关键的简历信息 加载 到 ES 中
     * @param userId
     */
    public void transformAndFlush(String userId);
}
