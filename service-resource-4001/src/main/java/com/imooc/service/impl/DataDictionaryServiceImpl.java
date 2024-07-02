package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.mapper.DataDictionaryMapper;
import com.imooc.pojo.DataDictionary;
import com.imooc.pojo.bo.DataDictionaryBO;
import com.imooc.service.DataDictionaryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 数据字典表 服务实现类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
@Service
public class DataDictionaryServiceImpl extends ServiceImpl<DataDictionaryMapper, DataDictionary> implements DataDictionaryService {

    private final DataDictionaryMapper dataDictionaryMapper;

    public DataDictionaryServiceImpl(DataDictionaryMapper dataDictionaryMapper) {
        this.dataDictionaryMapper = dataDictionaryMapper;
    }

    /**
     * 根据参数条件创建或者新增数据字典 实现方法
     *
     * @param dataDictionaryBO
     */
    @Override
    public void createOrUpdateDataDictionary(DataDictionaryBO dataDictionaryBO) {

        DataDictionary dataDictionary = new DataDictionary();
        BeanUtils.copyProperties(dataDictionaryBO, dataDictionary);

        //   参数 id 不存在的情况 适用新增
        if (StringUtils.isBlank(dataDictionaryBO.getId())) {
            //  判断数据字典是否存在（是否与数据库中的数据字典项重复）
            DataDictionary existDD = dataDictionaryMapper.selectOne(
                    new QueryWrapper<DataDictionary>()
                            .eq("item_key", dataDictionaryBO.getItemKey())
                            .or()
                            .eq("item_value", dataDictionaryBO.getItemValue()));

            if (existDD != null) {
                //  当前数据字典的内容在 数据库中存在 （非耦合自定义异常返回）
                GraceException.displayException(ResponseStatusEnum.DATA_DICT_EXIST_ERROR);
            }

            //  当前数据字典在系统中不存在 新增数据字典信息到对应的表中
            dataDictionaryMapper.insert(dataDictionary);
        } else {
            //  当前数据字典信息 在数据库中存在 id 适用于更新数据字典信息
            dataDictionaryMapper.updateById(dataDictionary);
        }

    }
}
