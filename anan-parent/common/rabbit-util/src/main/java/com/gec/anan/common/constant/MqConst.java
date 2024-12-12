package com.gec.anan.common.constant;

public class MqConst {


    public static final String EXCHANGE_ORDER = "anan.order";
    public static final String ROUTING_PAY_SUCCESS = "anan.pay.success";
    public static final String ROUTING_PROFITSHARING_SUCCESS = "anan.profitsharing.success";
    public static final String QUEUE_PAY_SUCCESS = "anan.pay.success";
    public static final String QUEUE_PROFITSHARING_SUCCESS = "anan.profitsharing.success";


    //取消订单延迟消息
    public static final String EXCHANGE_CANCEL_ORDER = "anan.cancel.order";
    public static final String ROUTING_CANCEL_ORDER = "anan.cancel.order";
    public static final String QUEUE_CANCEL_ORDER = "anan.cancel.order";

    //分账延迟消息
    public static final String EXCHANGE_PROFITSHARING = "anan.profitsharing";
    public static final String ROUTING_PROFITSHARING = "anan.profitsharing";
    public static final String QUEUE_PROFITSHARING  = "anan.profitsharing";

}
