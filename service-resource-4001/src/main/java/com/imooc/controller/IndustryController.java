package com.imooc.controller;

import com.imooc.api.mq.DelayConfig_Industry;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.Industry;
import com.imooc.service.IndustryService;
import com.imooc.utils.GsonUtils;
import com.imooc.utils.LocalDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/industry")
public class IndustryController extends BaseInfoProperties {

    private final IndustryService industryService;

    private final RabbitTemplate rabbitTemplate;

    public IndustryController(IndustryService industryService, RabbitTemplate rabbitTemplate) {
        this.industryService = industryService;
        this.rabbitTemplate = rabbitTemplate;
    }

    /******************************************业务分割：app 端接口 **********************************************************/
    /**
     * app 端 获取行业节点列表 father_id 为 0 的所有节点
     *
     * @param request
     * @return
     */
    @GetMapping("/app/initTopList")
    public GraceJSONResult initTopList(HttpServletRequest request) {

        //  先从 redis 中查询是否存在 请求的内容
        String topIndustryListStr = redis.get(TOP_INDUSTRY_LIST);
        List<Industry> topIndustryList = null;

        //  判断 redis 中存储的字符串结果是否存在 如果存在 将字符串结果转换为 List
        //  如果不存在 从 数据库中查询出对应的结果设置到 redis 对应的 key 上
        if (StringUtils.isNotBlank(topIndustryListStr)) {
            topIndustryList = GsonUtils.stringToListAnother(topIndustryListStr, Industry.class);
        } else {
            //  不存在的 情况 ，查询数据库 将结果转换成 String 设置到 redis 对应的 key 上
            topIndustryList = industryService.getTopIndustryList();
//            redis.set(TOP_INDUSTRY_LIST, GsonUtils.object2String(topIndustryList));

            // 判断 + 设空 避免缓存穿透的情况发生
            if (!CollectionUtils.isEmpty(topIndustryList)) {
                //  数据库中查询的数据 非空 ，将查询结果设置到 redis 中
                redis.set(TOP_INDUSTRY_LIST, GsonUtils.object2String(topIndustryList));
            } else {
                //  查询数据库的结果空值，给 redis 对应的 key 设置 20 分钟空值数组
                redis.set(TOP_INDUSTRY_LIST, "[]", 20 * 60);
            }
        }

        return GraceJSONResult.ok(topIndustryList);
    }

    /**
     * app 端 根据 根节点行业 id 查询三级行业 信息
     *
     * @param topIndustryId
     * @return
     */
    @GetMapping("/app/getThirdListByTop/{topIndustryId}")
    public GraceJSONResult getThirdListByTop(@PathVariable("topIndustryId") String topIndustryId) {

        //  拼接 三级行业节点的  redis key
        String thirdKey = THIRD_INDUSTRY_LIST + ":byTopId:" + topIndustryId;

        //  从 redis 中 获取对应的查询结果
        String thirdIndustryListStr = redis.get(thirdKey);
        List<Industry> thirdIndustryList = null;

        if (StringUtils.isNotBlank(thirdIndustryListStr)) {
            thirdIndustryList = GsonUtils.stringToListAnother(thirdIndustryListStr, Industry.class);
        } else {
            //  数据库查询结果
            thirdIndustryList = industryService.getThirdListByTop(topIndustryId);
            //  同步到 redis 对应的 key 中
            // redis.set(thirdKey, GsonUtils.object2String(thirdIndustryList));
            //  缓存穿透防御机制
            if (!CollectionUtils.isEmpty(thirdIndustryList)) {
                redis.set(thirdKey, GsonUtils.object2String(thirdIndustryList));
            } else {
                redis.set(thirdKey, "[]", 20 * 60);
            }
        }

        return GraceJSONResult.ok(thirdIndustryList);
    }

    /******************************************业务分割：运营管理端 admin 接口**********************************************************/

    /**
     * 创建行业节点
     *
     * @param industry
     * @return
     */
    @PostMapping("/createNode")
    public GraceJSONResult createNode(@RequestBody Industry industry) {

        //  判断节点名称是否存在 (行业存在的情况 返回 行业已存在的结果)
        if (!industryService.getIndustryIsExistByName(industry.getName()))
            return GraceJSONResult.errorMsg("该行业已存在，请重新取名~~~~");

        //  在创建行业节点之前 清空 redis 中的信息
        // resetRedisIndustry(industry);
        //  行业不存在 创建 行业节点
        industryService.createNode(industry);
        return GraceJSONResult.ok();
    }

    /**
     * 获取行业节点列表 father_id 为 0 的所有节点
     *
     * @param request
     * @return
     */
    @GetMapping("/getTopList")
    public GraceJSONResult getTopList(HttpServletRequest request) {

        return GraceJSONResult.ok(industryService.getTopIndustryList());
    }

    /**
     * 根据行业 id 获取子节点
     * 行业id可以为 0 获取的就是行业根节点
     *
     * @param topIndustryId
     * @return
     */
    @GetMapping("/children/{topIndustryId}")
    public GraceJSONResult getChildrenIndustryList(@PathVariable("topIndustryId") String topIndustryId) {

        return GraceJSONResult.ok(industryService.getChildrenIndustryList(topIndustryId));
    }

    /**
     * 更新行业节点信息
     *
     * @param industry
     * @return
     */
    @PostMapping("/updateNode")
    public GraceJSONResult updateNode(@RequestBody Industry industry) {

        //  新增：修改节点之前 清除 redis 中缓存的节点信息
        // resetRedisIndustry(industry);

        industryService.updateNode(industry);
        return GraceJSONResult.ok();
    }

    /**
     * 删除行业节点信息
     * 判断当前节点是否是一级或者二级节点如果是 那么需要确定 当前的节点下面没有子节点才可以删除
     * 三级节点可以直接删除
     *
     * @param industryId
     * @return
     */
    @DeleteMapping("/deleteNode/{industryId}")
    public GraceJSONResult deleteNode(@PathVariable("industryId") String industryId) {

        //  获取当前节点信息
        Industry industry = industryService.getById(industryId);
        //  判断当前节点是否是一级或者二级节点
        if (industry.getLevel() == 1 || industry.getLevel() == 2) {
            //  判断当前节点下是否有子节点
            if (industryService.getChildrenIndustryCounts(industryId) > 0) {
                //  当前节点下含有子节点 不可以直接删除
                return GraceJSONResult.errorMsg("当前节点下含有子节点，请先移除子节点信息，再进行删除！！！");
            }
        }

        //  新增：运营管理端删除节点之前清空 redis 中的内容
        // resetRedisIndustry(industry);
        //  三级节点可以直接删除 || 不含有子节点的一二级节点也可以删除
        industryService.removeById(industryId);

        return GraceJSONResult.ok();
    }


    /**
     * 运营管理端在对 行业节点信息进行修改或者删除后 调用的方法
     * 目的是清除 redis 中缓存的行业信息数据
     *
     * @param industry
     */
    public void resetRedisIndustry(Industry industry) {

        //  如果 发生变化的是一级节点，直接通过对应的 key 删除信息
        if (industry.getLevel() == 1) {

            redis.del(TOP_INDUSTRY_LIST);
            //  数据发生变化之后重新查询 数据库设置到 redis 中 防止用户请求直接打到数据库
            List<Industry> topIndustryList = industryService.getTopIndustryList();
            redis.set(TOP_INDUSTRY_LIST, GsonUtils.object2String(topIndustryList));

            //  缓存双删机制：重新查询之后覆盖 redis 中的数据 ，睡眠 n 毫秒删除并重新覆盖
            try {
                Thread.sleep(300);
                redis.del(TOP_INDUSTRY_LIST);
                redis.set(TOP_INDUSTRY_LIST, GsonUtils.object2String(topIndustryList));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (industry.getLevel() == 3) {

            //  发生变化的是 三级节点，根据三级节点的 id 查询出 一级节点的 id 组成对应的 key 后 从redis中删除
            String topIndustryId = industryService.getTopIndustryId(industry.getId());
            //  拼接 redis key
            String topIdKey = THIRD_INDUSTRY_LIST + ":topId:" + topIndustryId;
            redis.del(topIdKey);

            //  重新加载数据 防止 缓存击穿 + 缓存双删
            List<Industry> thirdList = industryService.getThirdListByTop(topIndustryId);
            redis.set(topIdKey, GsonUtils.object2String(thirdList));

            try {
                Thread.sleep(300);
                redis.del(topIdKey);
                redis.set(topIdKey, GsonUtils.object2String(thirdList));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 调用延迟队列 发送延迟消息
     * 默认同步的时间是 第二天凌晨三点钟
     * 同步 DB  redis 中的信息
     *
     * @return
     */
    @PostMapping("/refreshIndustry")
    public GraceJSONResult refreshIndustry() {

        //  计算第二天凌晨三点钟到现在的时间 // 第二天凌晨三点钟
        LocalDateTime futureTime = LocalDateUtils.parseLocalDateTime(
                LocalDateUtils.getTomorrow() + " 03:00:00", LocalDateUtils.DATETIME_PATTERN);

        //  计算当前时间与 预期发布时间的时间差 单位 毫秒
        Long publishTimes = LocalDateUtils.getChronoUnitBetween(LocalDateTime.now(),
                futureTime,
                ChronoUnit.MILLIS,
                true);

        // 格式转换
        // int delayTimes = publishTimes.intValue();
        int delayTimes = 10 * 1000;

        //  设置消息属性 当前消息为延迟消息
        MessagePostProcessor processor = DelayConfig_Industry.setDelayedTimes(delayTimes);

        //  发送消息到延迟队列
        rabbitTemplate.convertAndSend(DelayConfig_Industry.EXCHANGE_DELAY_REFRESH,
                DelayConfig_Industry.DELAY_REFRESH_INDUSTRY,
                "调用延迟队列消息，处理redis DB 数据不一致问题",
                processor);

        return GraceJSONResult.ok("调用延迟队列成功~~~");
    }
}
