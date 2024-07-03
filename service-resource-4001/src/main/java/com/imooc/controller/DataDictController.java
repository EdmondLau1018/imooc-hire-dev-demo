package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.bo.DataDictionaryBO;
import com.imooc.service.DataDictionaryService;
import com.imooc.utils.PagedGridResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/dataDict")
public class DataDictController extends BaseInfoProperties {

    private final DataDictionaryService dataDictionaryService;

    public DataDictController(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * 创建（或新增）数据字典项接口
     *
     * @return
     */
    @PostMapping("/create")
    public GraceJSONResult create(@RequestBody @Valid DataDictionaryBO dataDictionaryBO) {

        dataDictionaryService.createOrUpdateDataDictionary(dataDictionaryBO);
        return GraceJSONResult.ok();
    }

    /**
     * 数据字典列表分页查询
     *
     * @param typeName
     * @param itemValue
     * @param page
     * @param limit
     * @return
     */
    @PostMapping("/list")
    public GraceJSONResult list(String typeName, String itemValue, Integer page, Integer limit) {

        if (page == null) page = 1;
        if (limit == null) limit = 10;

        PagedGridResult gridResult = dataDictionaryService
                .getDictionaryListPaged(typeName, itemValue, page, limit);

        return GraceJSONResult.ok(gridResult);
    }
}
