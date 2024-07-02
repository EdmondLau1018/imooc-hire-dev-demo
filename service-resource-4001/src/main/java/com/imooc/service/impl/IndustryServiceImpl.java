package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.mapper.IndustryMapper;
import com.imooc.mapper.IndustryMapperCustom;
import com.imooc.pojo.Industry;
import com.imooc.pojo.vo.TopIndustryWithThirdListVO;
import com.imooc.service.IndustryService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 行业表 服务实现类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
@Service
public class IndustryServiceImpl extends ServiceImpl<IndustryMapper, Industry> implements IndustryService {

    private final IndustryMapper industryMapper;

    private final IndustryMapperCustom industryMapperCustom;

    public IndustryServiceImpl(IndustryMapper industryMapper, IndustryMapperCustom industryMapperCustom) {
        this.industryMapper = industryMapper;
        this.industryMapperCustom = industryMapperCustom;
    }

    /**
     * 根据参数 行业名称 判断当前行业是否存在 （在行业表中查询信息）
     *
     * @param industryName
     * @return
     */
    @Override
    public boolean getIndustryIsExistByName(String industryName) {

        // 根据行业名称查询当前行业是否存在
        Industry industry = industryMapper.selectOne(
                new QueryWrapper<Industry>()
                        .eq("name", industryName)
        );

        //  三元表达式 返回行业是否存在 的判断信息
        //  如果当前 不存在这个行业 返回true 否则返回 false
        return industry == null ? true : false;
    }

    /**
     * 创建行业 根节点 向行业表冲插入一条数据
     *
     * @param industry
     */
    @Override
    public void createNode(Industry industry) {

        industryMapper.insert(industry);
    }

    /**
     * 获取行业节点列表
     *
     * @return
     */
    @Override
    public List<Industry> getTopIndustryList() {

        List<Industry> industryList = industryMapper.selectList(
                new QueryWrapper<Industry>()
                        .eq("father_id", 0)
                        .orderByAsc("sort"));

        return industryList;
    }

    /**
     * 根据 industryId 获取对应行业子节点
     *
     * @param industryId
     * @return
     */
    @Override
    public List<Industry> getChildrenIndustryList(String industryId) {

        List<Industry> list = industryMapper.selectList(
                new QueryWrapper<Industry>()
                        .eq("father_id", industryId)
                        .orderByAsc("sort"));
        return list;
    }

    /**
     * 更新行业节点
     *
     * @param industry
     */
    @Override
    public void updateNode(Industry industry) {

        industryMapper.updateById(industry);
    }

    /**
     * 获取当前节点下子节点的数量 返回 long 类型
     *
     * @param industryId
     * @return
     */
    @Override
    public Long getChildrenIndustryCounts(String industryId) {

        Long count = industryMapper.selectCount(new QueryWrapper<Industry>().eq("father_id", industryId));
        return count;
    }

    /**
     * 根据行业根节点信息查询三级行业节点信息
     *
     * @param topIndustryId
     * @return
     */
    @Override
    public List<Industry> getThirdListByTop(String topIndustryId) {

        //  构建查询参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("topIndustryId", topIndustryId);

        List<Industry> industryList = industryMapperCustom.getThirdListByTop(paramMap);

        return industryList;
    }

    /**
     * 根据 三级节点 id 反向查询一级节点 id
     *
     * @param thirdIndustryId
     * @return
     */
    @Override
    public String getTopIndustryId(String thirdIndustryId) {

        //  拼接查询参数
        Map<String, Object> map = new HashMap<>();
        map.put("thirdIndustryId", thirdIndustryId);
        //  执行查询
        String topIndustryId = industryMapperCustom.getTopIndustryId(map);

        return topIndustryId;
    }

    /**
     * 查询三级行业节点 列表和一级行业节点 id
     * 实现
     * @return
     */
    @Override
    public List<TopIndustryWithThirdListVO> getAllThirdIndustryList() {
        return industryMapperCustom.getAllThirdIndustryList();
    }
}
