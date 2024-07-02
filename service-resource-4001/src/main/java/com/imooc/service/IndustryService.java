package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.Industry;
import com.imooc.pojo.vo.TopIndustryWithThirdListVO;

import java.util.List;

/**
 * <p>
 * 行业表 服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
public interface IndustryService extends IService<Industry> {

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

    /**
     * 获取当前节点下子节点的数量
     * @param industryId
     * @return
     */
    public Long getChildrenIndustryCounts(String industryId);

    /**
     * 根据 行业根节点信息获取 三级行业信息
     * 三级查询
     * @param topIndustryId
     * @return
     */
    public List<Industry> getThirdListByTop(String topIndustryId);

    /**
     * 根据 三级节点 id 反向查询一级节点 id
     *
     * @param thirdIndustryId
     * @return
     */
    public String getTopIndustryId(String thirdIndustryId);

    /**
     * 获取 三级行业节点列表
     * 和对应的 一级节点 id （多对一关系）
     * @return
     */
    public List<TopIndustryWithThirdListVO> getAllThirdIndustryList();

}
