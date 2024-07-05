package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.DataDictionary;
import com.imooc.pojo.bo.DataDictionaryBO;
import com.imooc.pojo.bo.QueryDictItemsBO;
import com.imooc.pojo.vo.CompanyPointsVO;
import com.imooc.service.DataDictionaryService;
import com.imooc.utils.GsonUtils;
import com.imooc.utils.PagedGridResult;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/dataDict")
public class DataDictController extends BaseInfoProperties {

    private final DataDictionaryService dataDictionaryService;

    private static final String DDKEY_PREFIX = DATA_DICTIONARY_LIST_TYPECODE + ":";

    public DataDictController(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /******************************************业务分割：app 端接口 **********************************************************/

    /**
     * 根据字典码获取数据字典列表
     *
     * @param typeCode
     * @return
     */
    @PostMapping("/app/getDataByCode")
    public GraceJSONResult getDataByCode(String typeCode) {

        if (StringUtils.isBlank(typeCode))
            return GraceJSONResult.errorMsg("字典项不能为空~~~");

        //  拼接 redis 查询的 key
        String ddKey = DDKEY_PREFIX + typeCode;
        //  从 redis 中查询缓存信息
        String redisDDStr = redis.get(ddKey);
        List<DataDictionary> redisDataDictionaryList = null;
        if (StringUtils.isNotBlank(redisDDStr)) {
            //  进行格式转换
            redisDataDictionaryList = GsonUtils.stringToListAnother(redisDDStr,DataDictionary.class);
        }

        //  调用 service 查询对应的结果
        redisDataDictionaryList = dataDictionaryService.getDataBydCode(typeCode);

        // 将 redis 查询的数据字典项结果设置到缓存中
        redis.set(ddKey,GsonUtils.object2String(redisDataDictionaryList));

        return GraceJSONResult.ok(redisDataDictionaryList);
    }

    /**
     * app 端 查询企业优势对应的数据字典 单线程查询四个列表
     * @param itemsBO
     * @return
     */
    @PostMapping("/app/getItemByKeys")
    public GraceJSONResult getItemByKeys(@RequestBody QueryDictItemsBO itemsBO){

        String[] advantage = itemsBO.getAdvantage();
        String[] benefits = itemsBO.getBenefits();
        String[] bonus = itemsBO.getBonus();
        String[] subsidy = itemsBO.getSubsidy();

        List<DataDictionary> advantageList = dataDictionaryService.getItemsByKeys(advantage);
        List<DataDictionary> benefitsList = dataDictionaryService.getItemsByKeys(benefits);
        List<DataDictionary> bonusList = dataDictionaryService.getItemsByKeys(bonus);
        List<DataDictionary> subsidyList = dataDictionaryService.getItemsByKeys(subsidy);

        CompanyPointsVO companyPointsVO = new CompanyPointsVO();
        companyPointsVO.setAdvantageList(advantageList);
        companyPointsVO.setBenefitsList(benefitsList);
        companyPointsVO.setBonusList(bonusList);
        companyPointsVO.setSubsidyList(subsidyList);

        return GraceJSONResult.ok(companyPointsVO);
    }


    /******************************************业务分割：运营管理端接口 **********************************************************/

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

    /**
     * 根据数据字典 id 获取单个数据字典信息
     * 在修改之前查询单个数据字典的信息
     *
     * @param dictId
     * @return
     */
    @PostMapping("/item")
    public GraceJSONResult item(String dictId) {

        DataDictionary dataDictionary = dataDictionaryService.getDataDictionary(dictId);
        return GraceJSONResult.ok(dataDictionary);
    }

    /**
     * 数据字典修改接口
     *
     * @param dataDictionaryBO
     * @return
     */
    @PostMapping("/modify")
    public GraceJSONResult modify(@RequestBody @Valid DataDictionaryBO dataDictionaryBO) {


        //  判断当前 BO 中是否存在 id 如果不存在 则直接抛出异常
        if (StringUtils.isBlank(dataDictionaryBO.getId()))
            return GraceJSONResult.errorMsg("数据字典修改发生错误，未获取到当前数据字典 id ~~");

        dataDictionaryService.createOrUpdateDataDictionary(dataDictionaryBO);
        return GraceJSONResult.ok();
    }

    /**
     * 根据数据字典 id 删除数据字典
     *
     * @param dictId
     * @return
     */
    @PostMapping("/delete")
    public GraceJSONResult delete(String dictId) {

        dataDictionaryService.deleteDataDictionary(dictId);
        return GraceJSONResult.ok();
    }
}
