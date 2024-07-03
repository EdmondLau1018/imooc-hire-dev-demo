package com.imooc.canal;

import com.imooc.base.BaseInfoProperties;
import com.imooc.pojo.DataDictionary;
import com.imooc.pojo.co.DataDictionaryCO;
import com.imooc.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@CanalTable("data_dictionary")      //  指定当前助手类监听的表名称
@Component
public class DataDictSyncHelper extends BaseInfoProperties implements EntryHandler<DataDictionaryCO> {
    //  implements EntryHandler<DataDictionaryCO> 指定 数据库关联的 实体类对象，
    //  这里的对象属性名称要与 数据表中的字段名称一致 不存在驼峰到 下划线映射
    private static final String DDKEY_PREFIX = DATA_DICTIONARY_LIST_TYPECODE + ":";

    @Override
    public void insert(DataDictionaryCO dataDictionaryCO) {

        //  查询 redis 中是否存在对应的数据字典 List
        String ddKey = DDKEY_PREFIX + dataDictionaryCO.getType_code();
        String ddListStr = redis.get(ddKey);
        List<DataDictionary> redisDataDictionaryList = null;

        //  判断 redis 中的 数据字典列表是否为空 如果为空就新建一个这个 列表 如果不为空 就解析这个在原有列表上新增即可
        if (StringUtils.isBlank(ddListStr)) {
            //  新建一个列表
            redisDataDictionaryList = new ArrayList<DataDictionary>();
        } else {
            //    redis 中存储的列表不为空 ，解析这个列表
            redisDataDictionaryList = GsonUtils.stringToListAnother(ddListStr, DataDictionary.class);
        }

        //  将监听到变化的 CO 对象 转换成 pojo对象
        DataDictionary pendingDataDictionary = convertDD(dataDictionaryCO);
        redisDataDictionaryList.add(pendingDataDictionary);
        redis.set(ddKey, GsonUtils.object2String(redisDataDictionaryList));
    }

    @Override
    public void update(DataDictionaryCO before, DataDictionaryCO after) {
        EntryHandler.super.update(before, after);
    }

    @Override
    public void delete(DataDictionaryCO dataDictionary) {
        EntryHandler.super.delete(dataDictionary);
    }

    /**
     * 手动类型转换 ，将 canal 监听到的 CO  属性复制到 pojo 属性中
     * 返回新的 pojo 对象
     *
     * @param dataDictionaryCO
     * @return
     */
    private DataDictionary convertDD(DataDictionaryCO dataDictionaryCO) {

        DataDictionary pendingDataDictionary = new DataDictionary();
        BeanUtils.copyProperties(dataDictionaryCO, pendingDataDictionary);
        pendingDataDictionary.setTypeCode(dataDictionaryCO.getType_code());
        pendingDataDictionary.setTypeName(dataDictionaryCO.getType_name());
        pendingDataDictionary.setItemKey(dataDictionaryCO.getItem_key());
        pendingDataDictionary.setItemValue(dataDictionaryCO.getItem_value());

        return pendingDataDictionary;
    }
}
