package com.imooc.mq;

import com.google.gson.Gson;
import com.imooc.api.mq.DelayConfig_Industry;
import com.imooc.base.BaseInfoProperties;
import com.imooc.pojo.Industry;
import com.imooc.pojo.vo.TopIndustryWithThirdListVO;
import com.imooc.service.IndustryService;
import com.imooc.utils.GsonUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RabbitMQDelayConsumer_Industry extends BaseInfoProperties {

    private final IndustryService industryService;

    public RabbitMQDelayConsumer_Industry(IndustryService industryService) {
        this.industryService = industryService;
    }


    /**
     * 死信队列消费者 监听死信队列
     */
    @RabbitListener(queues = {DelayConfig_Industry.QUEUE_DELAY_REFRESH})
    public void watchQueue(Message message, Channel channel) throws Exception {

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        log.info("获取的路由信息为：{}", routingKey);
        log.info("获取的消息为：{}", new String(message.getBody()));

        //  检查 routing_key 是否符合拟定的 路由规则 如果符合执行 缓存更新业务
        if (routingKey.equalsIgnoreCase(DelayConfig_Industry.DELAY_REFRESH_INDUSTRY)) {
            log.info("监听延迟队列消息 执行业务~~~");

            //  从 redis 中删除一级分类列表
            redis.del(TOP_INDUSTRY_LIST);
            //  查询一级分类
            List<Industry> topIndustryList = industryService.getTopIndustryList();
            //  将一级分类的查询结果设置在 redis 中
            redis.set(TOP_INDUSTRY_LIST, GsonUtils.object2String(topIndustryList));
            //  从 redis 中删除三级分类列表
            String thirdKeyMulti = THIRD_INDUSTRY_LIST + ":topId:";
            //  redis 工具中提供批量删除的方法
            redis.allDel(thirdKeyMulti);
            //  查询三级分类
            List<TopIndustryWithThirdListVO> allThirdIndustryList = industryService.getAllThirdIndustryList();
            //  将三级分类的查询结果设置在 redis 中
            for (TopIndustryWithThirdListVO thirdVO : allThirdIndustryList) {

                //  拼接三级行业 列表 redis key
                String thirdKey = thirdKeyMulti + thirdVO.getTopId();
                //  将 对应的 KV 设置到 redis 中
                redis.set(thirdKey, GsonUtils.object2String(thirdVO.getThirdIndustryList()));
            }
        }

        //  获取到 delivery 信息直接丢弃消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
