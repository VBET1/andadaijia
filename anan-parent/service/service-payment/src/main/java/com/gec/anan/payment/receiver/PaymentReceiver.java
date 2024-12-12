package com.gec.anan.payment.receiver;

import com.alibaba.fastjson.JSONObject;
import com.gec.anan.common.constant.MqConst;
import com.gec.anan.model.form.payment.ProfitsharingForm;
import com.gec.anan.payment.service.WxPayService;
import com.gec.anan.payment.service.WxProfitsharingService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class PaymentReceiver {

    @Autowired
    private WxPayService wxPayService;

    /**
     * 订单支付成功，处理支付回调
     *
     * @param orderNo
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_PAY_SUCCESS, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_ORDER),
            key = {MqConst.ROUTING_PAY_SUCCESS}
    ))
    public void paySuccess(String orderNo, Message message, Channel channel) throws IOException {
        wxPayService.handleOrder(orderNo);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    @Autowired
    private WxProfitsharingService wxProfitsharingService;

    /**
     * 分账消息
     * @param param
     * @throws IOException
     */
    @RabbitListener(queues = MqConst.QUEUE_PROFITSHARING)
    public void profitsharingMessage(String param, Message message, Channel channel) throws IOException {
        try {
            ProfitsharingForm profitsharingForm = JSONObject.parseObject(param, ProfitsharingForm.class);
            log.info("分账：{}", param);
            wxProfitsharingService.profitsharing(profitsharingForm);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.info("分账调用失败：{}", e.getMessage());
            //任务执行失败，就退回队列继续执行，优化：设置退回次数，超过次数记录日志
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

}