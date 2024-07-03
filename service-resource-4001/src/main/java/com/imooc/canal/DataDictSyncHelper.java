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
import java.util.Dictionary;
import java.util.List;

@Slf4j
@CanalTable("data_dictionary")      //  指定当前助手类监听的表名称
@Component
public class DataDictSyncHelper extends BaseInfoProperties implements EntryHandler<DataDictionaryCO> {
    //  implements EntryHandler<DataDictionaryCO> 指定 数据库关联的 实体类对象，
    //  这里的对象属性名称要与 数据表中的字段名称一致 不存在驼峰到 下划线映射
    private static final String DDKEY_PREFIX = DATA_DICTIONARY_LIST_TYPECODE + ":";

    /**
     * 监听数据库 新增的业务逻辑
     * @param dataDictionaryCO
     */
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

    /**
     * canal 监听 更新的业务逻辑
     * @param before 更新前的数据库对象
     * @param after  更新后的数据库对象
     */
    @Override
    public void update(DataDictionaryCO before, DataDictionaryCO after) {

        String ddKey = DDKEY_PREFIX + after.getType_code();

        //  查询 redis 根据 key 获取 redis 中的数据字典缓存
        String ddListStr = redis.get(ddKey);
        List<DataDictionary> redisDataDictionaryList = null;
        //  判断对应的 缓存是否存在，执行对应的业务流程
        if (StringUtils.isBlank(ddListStr)) {
            //  未查询到数据字典项，不做任何操作
        } else {
            //  查询到对应的 redis 缓存 进行对象转换
            redisDataDictionaryList = GsonUtils.stringToListAnother(ddListStr, DataDictionary.class);
            for (DataDictionary redisDD : redisDataDictionaryList) {
                //  根据 数据字典项 id 进行匹配
                if (redisDD.getId().equalsIgnoreCase(after.getId())) {
                    //  将修改后的 内容转换成 pojo 对象
                    DataDictionary pendingDictionary = convertDD(after);
                    //  列表中删除原来的 pojo 对象 添加新的 pojo 对象
                    redisDataDictionaryList.remove(redisDD);
                    redisDataDictionaryList.add(pendingDictionary);
                    break;
                }
            }

            //  将设置新对象的额数据字典列表写入 redis
            redis.set(ddKey, GsonUtils.object2String(redisDataDictionaryList));
        }
    }

    /**
     * canal 监听数据库删除的业务逻辑
     * @param dataDictionary   被删除的映射数据库的 CO 对象
     */
    @Override
    public void delete(DataDictionaryCO dataDictionary) {

        String ddKey = DDKEY_PREFIX + dataDictionary.getType_code();

        //  查询 redis 中是否含有对应的数据字典 key
        String ddListStr = redis.get(ddKey);
        List<DataDictionary> redisDataDictionaryList = null;

        //  根据 redis 中的查询结果判断 执行对应的删除 逻辑
        if (StringUtils.isBlank(ddListStr)) {
            //  未查询到 redis 中存在数据字典项  不做任何操作
        } else {
            //  解析查询出的 数据字典项列表 删除指定的 数据字典项 ，重新放回 redis 中
            redisDataDictionaryList = GsonUtils.stringToListAnother(ddListStr, DataDictionary.class);
            for (DataDictionary redisDD : redisDataDictionaryList) {

                //  根据 数据字典项 id 进行逐个匹配
                if (redisDD.getId().equalsIgnoreCase(dataDictionary.getId())) {
                    //  匹配到数据库中的删除项 执行 redis 删除业务逻辑
                    redisDataDictionaryList.remove(redisDD);
                    //  删除流程指的是删除具体发的某一项，删除之后退出流程
                    break;
                }
            }
            //  将删除之后的 List 重新写入 redis
            redis.set(ddKey, GsonUtils.object2String(redisDataDictionaryList));
        }
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
