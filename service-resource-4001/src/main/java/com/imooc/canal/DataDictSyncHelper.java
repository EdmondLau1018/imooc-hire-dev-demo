package com.imooc.canal;

import com.imooc.pojo.co.DataDictionary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@Slf4j
@CanalTable("data_dictionary")      //  指定当前助手类监听的表名称
@Component
public class DataDictSyncHelper implements EntryHandler<DataDictionary> {
    //  implements EntryHandler<DataDictionary> 指定 数据库关联的 实体类对象，
    //  这里的对象属性名称要与 数据表中的字段名称一致 不存在驼峰到 下划线映射


    @Override
    public void insert(DataDictionary dataDictionary) {
        EntryHandler.super.insert(dataDictionary);
    }

    @Override
    public void update(DataDictionary before, DataDictionary after) {
        EntryHandler.super.update(before, after);
    }

    @Override
    public void delete(DataDictionary dataDictionary) {
        EntryHandler.super.delete(dataDictionary);
    }
}
