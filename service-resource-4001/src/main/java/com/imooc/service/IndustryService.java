package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.Industry;

import java.util.List;

/**
 * <p>
 * 行业表 服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
public interface IndustryService {

    /**
     * 根据行业名称判断行业节点是否存在
     * @param industryName
     * @return
     */
    public boolean getIndustryIsExistByName(String industryName);

    /**
     * 创建行业节点
     * @param industry
     */
    public void createNode(Industry industry);

    /**
     * 查询行业节点列表 father_id = 0
     * @return
     */
    public List<Industry> getTopIndustryList();

    /**
     * 获取行业子节点
     * @param industryId
     * @return
     */
    public List<Industry> getChildrenIndustryList(String industryId);

    /**
     * 更新行业节点
     * @param industry
     */
    public void updateNode(Industry industry);

}
