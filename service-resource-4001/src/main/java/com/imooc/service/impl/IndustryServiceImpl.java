package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.imooc.mapper.IndustryMapper;
import com.imooc.pojo.Industry;
import com.imooc.service.IndustryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 行业表 服务实现类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
@Service
public class IndustryServiceImpl implements IndustryService {

    private final IndustryMapper industryMapper;

    public IndustryServiceImpl(IndustryMapper industryMapper) {
        this.industryMapper = industryMapper;
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
     * @param industry
     */
    @Override
    public void createNode(Industry industry) {

        industryMapper.insert(industry);
    }

    /**
     * 获取行业节点列表
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
     *  根据 industryId 获取对应行业子节点
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
}
