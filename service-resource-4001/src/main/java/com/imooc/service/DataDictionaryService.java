package com.imooc.service;

import com.imooc.pojo.DataDictionary;
import com.imooc.pojo.bo.DataDictionaryBO;
import com.imooc.utils.PagedGridResult;

/**
 * <p>
 * 数据字典表 服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
public interface DataDictionaryService {

    /**
     * 根据 BO 是否含有 id值确定是后创建还是新增数据字典
     *
     * @param dataDictionaryBO
     */
    public void createOrUpdateDataDictionary(DataDictionaryBO dataDictionaryBO);

    /**
     * 数据字典列表分页查询
     *
     * @param typeName
     * @param itemValue
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult getDictionaryListPaged(String typeName, String itemValue, Integer page, Integer pageSize);

    /**
     * 获取数据字典 id
     * @param dictId
     * @return
     */
    public DataDictionary getDataDictionary(String dictId);

    /**
     * 删除数据字典接口
     * @param dictId
     */
    public void deleteDataDictionary(String dictId);
}
