package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.Job;
import com.imooc.pojo.bo.EditJobBO;
import com.imooc.utils.PagedGridResult;

/**
 * <p>
 * HR发布的职位表 服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
public interface JobService {

    /**
     * 新增 更新 工作机会
     *
     * @param editJobBO
     */
    public void modifyJobDetail(EditJobBO editJobBO);

    /**
     * hr 查询发布的工作机会 列表
     * @param hrId
     * @param companyId
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    public PagedGridResult queryHrJobList(String hrId, String companyId, Integer page, Integer pageSize, Integer status);
}
