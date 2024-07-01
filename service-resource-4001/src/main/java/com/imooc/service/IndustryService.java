package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.Industry;

/**
 * <p>
 * 行业表 服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
public interface IndustryService {

    public boolean getIndustryIsExistByName(String industryName);

    public void createNode(Industry industry);

}
