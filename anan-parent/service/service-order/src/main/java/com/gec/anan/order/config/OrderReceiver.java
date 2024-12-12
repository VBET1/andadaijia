package com.gec.anan.order.config;

import com.gec.anan.common.constant.MqConst;
import com.gec.anan.order.service.OrderInfoService;
import com.rabbitmq.client.Channel;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OrderReceiver {


    @Autowired
    private OrderInfoService orderInfoService;
    /**
     * 订单分账成功，更新分账状态
     *
     * @param orderNo
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_PROFITSHARING_SUCCESS, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_ORDER),
            key = {MqConst.ROUTING_PROFITSHARING_SUCCESS}
    ))
    public void profitsharingSuccess(String orderNo, Message message, Channel channel) throws IOException {
        orderInfoService.updateProfitsharingStatus(orderNo);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    @Autowired
    private RedissonClient redissonClient;
    /**
     * 发送延迟消息
     */
    private void sendDelayMessage(Long orderId) {
        try {
            //  创建一个队列
            RBlockingDeque<Object> blockingDeque = redissonClient
                    .getBlockingDeque("queue_cancel");
            //  将队列放入延迟队列中
            RDelayedQueue<Object> delayedQueue = redissonClient
                    .getDelayedQueue(blockingDeque);
            //  发送的内容
            delayedQueue.offer(orderId.toString(),
                    15, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
