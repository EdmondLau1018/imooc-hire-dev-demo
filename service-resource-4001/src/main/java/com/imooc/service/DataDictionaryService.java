package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.DataDictionary;
import com.imooc.pojo.bo.DataDictionaryBO;

/**
 * <p>
 * 数据字典表 服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
public interface DataDictionaryService extends IService<DataDictionary> {

    /**
     * 根据 BO 是否含有 id值确定是后创建还是新增数据字典
     * @param dataDictionaryBO
     */
    public void createOrUpdateDataDictionary(DataDictionaryBO dataDictionaryBO);

}
