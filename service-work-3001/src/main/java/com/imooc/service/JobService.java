package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.Job;
import com.imooc.pojo.bo.EditJobBO;

/**
 * <p>
 * HR发布的职位表 服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
public interface JobService {

    public void modifyJobDetail(EditJobBO editJobBO);
}
