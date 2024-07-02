package com.imooc.mapper;

import com.imooc.pojo.Industry;
import com.imooc.pojo.vo.TopIndustryWithThirdListVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 行业表  自定义 Mapper 接口
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
@Repository
public interface IndustryMapperCustom {

    /**
     * 自连接三级查询 根据行业一级节点 id 获取行业三级节点信息
     *
     * @param map
     * @return
     */
    public List<Industry> getThirdListByTop(@Param("paramMap") Map<String, Object> map);


    /**
     * 根据三级节点 id 查询 对应的 一级节点 id
     * @param map
     * @return
     */
    public String getTopIndustryId(@Param("paramMap") Map<String, Object> map);

    /**
     * 查询 所有的三级列表和对应的 一级 id保留一对多的关系
     * @return
     */
    public List<TopIndustryWithThirdListVO> getAllThirdIndustryList();

}
