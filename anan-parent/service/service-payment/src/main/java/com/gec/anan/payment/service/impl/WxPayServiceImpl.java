package com.gec.anan.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gec.anan.common.constant.MqConst;
import com.gec.anan.common.constant.SystemConstant;
import com.gec.anan.common.execption.AnanException;
import com.gec.anan.common.result.ResultCodeEnum;
import com.gec.anan.common.service.RabbitService;
import com.gec.anan.common.util.RequestUtils;
import com.gec.anan.driver.client.DriverAccountFeignClient;
import com.gec.anan.model.entity.payment.PaymentInfo;
import com.gec.anan.model.entity.payment.ProfitsharingInfo;
import com.gec.anan.model.enums.TradeType;
import com.gec.anan.model.form.driver.TransferForm;
import com.gec.anan.model.form.payment.PaymentInfoForm;
import com.gec.anan.model.form.payment.ProfitsharingForm;
import com.gec.anan.model.vo.order.OrderProfitsharingVo;
import com.gec.anan.model.vo.order.OrderRewardVo;
import com.gec.anan.model.vo.payment.WxPrepayVo;
import com.gec.anan.order.client.OrderInfoFeignClient;

import com.gec.anan.payment.mapper.PaymentInfoMapper;
import com.gec.anan.payment.mapper.ProfitsharingInfoMapper;
import com.gec.anan.payment.service.WxPayService;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import io.seata.common.util.DateUtil;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class WxPayServiceImpl implements WxPayService {

    @Autowired
    PaymentInfoMapper paymentInfoMapper;
    @Autowired
    RSAAutoCertificateConfig rsaAutoCertificateConfig;
//    @Autowired
//    WxPayV3Properties wxPayV3Properties;
    @Autowired
    private OrderInfoFeignClient orderInfoFeignClient;
    @Autowired
    private DriverAccountFeignClient driverAccountFeignClient;

    @Autowired
    private RabbitService rabbitService;

    //主动查询接口实现逻辑
    @Override
    public Boolean queryPayStatus(String orderNo) {
//        //1、构建jsapi的服务接口
//        JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(rsaAutoCertificateConfig).build();
//        //2、提供交易的请求对象
//        QueryOrderByOutTradeNoRequest queryRequest = new QueryOrderByOutTradeNoRequest();
//        queryRequest.setMchid(wxPayV3Properties.getMerchantId());
//        queryRequest.setOutTradeNo(orderNo);
//        try {
//            //3、发起查询
//            Transaction transaction = service.queryOrderByOutTradeNo(queryRequest);
//            log.info(JSONObject.toJSONString(transaction));
//            //4、判断订单状态
//            if (null != transaction && transaction.getTradeState() == Transaction.TradeStateEnum.SUCCESS) {
//                //更改订单状态
//                this.handlePayment(transaction);
//                return true;
//            }
//        } catch (ServiceException e) {
//            // API返回失败, 例如ORDER_NOT_EXISTS
//
//            System.out.printf("code=[%s], message=[%s]\n", e.getErrorCode(), e.getErrorMessage());
//        }
        System.out.println(orderNo);
        log.info("主动查询订单状态：{}", orderNo);
//        this.handlePayment(orderNo);
        //return false;
        return true;
    }

    //微信支付响应后的异步处理的业务方法
    @Override
    public Map<String, Object> wxnotify(HttpServletRequest request) {
        //1、获取回调通知的请求参数【校验签名、解密】
        //1.回调通知的验签与解密\从request头信息获取参数
        //HTTP 头 Wechatpay-Signature\ Wechatpay-Nonce\Wechatpay-Timestamp\Wechatpay-Serial\ Wechatpay-Signature-Type
        //HTTP 请求体 body。切记使用原始报文，不要用 JSON 对象序列化后的字符串，避免验签的 body 和原文不一致。
        String wechatPaySerial = request.getHeader("Wechatpay-Serial");
        String nonce = request.getHeader("Wechatpay-Nonce");
        String timestamp = request.getHeader("Wechatpay-Timestamp");
        String signature = request.getHeader("Wechatpay-Signature");
        String requestBody = RequestUtils.readData(request);
        log.info("wechatPaySerial：{}", wechatPaySerial);
        log.info("nonce：{}", nonce);
        log.info("timestamp：{}", timestamp);
        log.info("signature：{}", signature);
        log.info("requestBody：{}", requestBody);
        //2、构建请求参数
//        RequestParam requestParam = new RequestParam.Builder()
//                .serialNumber(wechatPaySerial)
//                .nonce(nonce)
//                .signature(signature)
//                .timestamp(timestamp)
//                .body(requestBody)
//                .build();
        //提供一个消息的解析器
//        NotificationParser parser = new NotificationParser(rsaAutoCertificateConfig);
        //3、调用支付处理的业务逻辑代码【更新支付数据、更新订单状态....】
//        Transaction transaction = parser.parse(requestParam, Transaction.class);
//        log.info("成功解析：{}", JSONObject.toJSONString(transaction));
//        //判断交易是否成功
//        if (null != transaction && transaction.getTradeState() == Transaction.TradeStateEnum.SUCCESS) {
//            //5.处理支付成功后的业务实现
//            this.handlePayment(transaction);
//        }
        //保留数据返回

        this.handlePayment("1");
        return null;
    }


    //业务逻辑代码【更新支付数据、更新订单状态....】
    private void handlePayment(String orderNo) {
        //1、获取payment信息
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(
                new LambdaQueryWrapper<PaymentInfo>()
                        .eq(PaymentInfo::getOrderNo,orderNo)
        );

        if (paymentInfo.getPaymentStatus() != 1) {
            return;
        }
        //2、支付记录的状态改为1
        paymentInfo.setPaymentStatus(1);
//        paymentInfo.setOrderNo(transaction.getOutTradeNo());
//        paymentInfo.setTransactionId(transaction.getTransactionId());
        paymentInfo.setCallbackTime(new Date());
//        paymentInfo.setCallbackContent(JSONObject.toJSONString(transaction));
        paymentInfoMapper.updateById(paymentInfo);
        //3、其他的业务就使用mq进行异步通信处理
        rabbitService.sendMessage(MqConst.EXCHANGE_ORDER, MqConst.ROUTING_PAY_SUCCESS, paymentInfo.getOrderNo());
    }


 @Override
 public WxPrepayVo createWxPayment(PaymentInfoForm paymentInfoForm) {
//     try {


          System.out.println("自动我paymentInfoForm = "+paymentInfoForm);
         //1、把订单支付的数据信获取、判断记录是否存在，不在则插入数据
         PaymentInfo paymentInfo = paymentInfoMapper.selectOne(
                 new LambdaQueryWrapper<PaymentInfo>()
                         .eq(PaymentInfo::getOrderNo, paymentInfoForm.getOrderNo())
         );
     PaymentInfo info = new PaymentInfo();
//         if (paymentInfo == null) {
             System.out.println("自动我psdsdsadasdasdasdaymentInfoForm = "+paymentInfoForm);
             info.setPaymentStatus(1);//默认为未支付状态
             info.setAmount(paymentInfoForm.getAmount());
             info.setContent(paymentInfoForm.getContent());
             info.setCustomerOpenId(paymentInfoForm.getCustomerOpenId());
             info.setDriverOpenId(paymentInfoForm.getDriverOpenId());
             info.setOrderNo(paymentInfoForm.getOrderNo());
             info.setPayWay(paymentInfoForm.getPayWay());
             info.setCallbackTime(new Date());
             info.setTransactionId("13322637136");
             paymentInfoMapper.insert(info);
    // wxProfitsharingService.profitsharing(profitsharingForm);
        //     paymentInfoMapper.insert(info);
         //2、JSapi-Service接口构建、注入支付密钥的config配置对象
         JsapiServiceExtension service = new JsapiServiceExtension.Builder()
                 .config(rsaAutoCertificateConfig).build();
         //2-1提供request、参数
         PrepayRequest request = new PrepayRequest();
         Amount amount = new Amount();//微信支付的金额对象 微信支付的金额单位为分
         //金额的设置
         amount.setTotal(paymentInfoForm.getAmount().multiply(new BigDecimal(100)).intValue());
         amount.setTotal(1);//微信支付的金额单位为分 所以为1分钱
         request.setAmount(amount);
         request.setAppid("wx7ec065065b4cde18");
         request.setMchid("1631833859");
         //支付的描述
         String description = info.getContent();
         if (description.length() > 127) {//字符串截取【限制长度不能超过127个字符】
             description = description.substring(0, 127);
         }
         request.setDescription(description);
         //支付请求操作成功回调的请求url
         request.setNotifyUrl("http://192.168.126.5:8600/payment/wxPay/notify");
         request.setOutTradeNo(info.getOrderNo());
         //用户信息【openid】
         Payer payer = new Payer();
         payer.setOpenid(paymentInfoForm.getCustomerOpenId());
         request.setPayer(payer);
         //分账配置[商家收款、分账给司机]
         SettleInfo settleInfo = new SettleInfo();
         settleInfo.setProfitSharing(true);
         request.setSettleInfo(settleInfo);
         //发起请求
//         PrepayWithRequestPaymentResponse response = service.prepayWithRequestPayment(request);
//         log.info("微信支付下单返回参数：{}", JSONObject.toJSONString(response));
         //封装数据并且返回
         WxPrepayVo wxPrepayVo = new WxPrepayVo();
         BeanUtils.copyProperties(request, wxPrepayVo);
         //获取时间戳
         wxPrepayVo.setTimeStamp(String.valueOf(System.currentTimeMillis()));
         this.handleOrder(info.getOrderNo());
         return wxPrepayVo;
//     } catch (Exception e) {
//
//
//          throw new AnanException(ResultCodeEnum.WX_CREATE_ERROR);
//     }
 }

 @Autowired
 ProfitsharingInfoMapper profitsharingInfoMapper;

    @GlobalTransactional
    @Override
    public void handleOrder(String orderNo) {
        //更改订单支付状态
        orderInfoFeignClient.updateOrderPayStatus(orderNo);

        //处理系统奖励，打入司机账户
        OrderRewardVo orderRewardVo = orderInfoFeignClient.getOrderRewardFee(orderNo).getData();
        System.out.println(orderRewardVo);
        if(null != orderRewardVo.getRewardFee() && orderRewardVo.getRewardFee().doubleValue() > 0) {
            TransferForm transferForm = new TransferForm();
            transferForm.setTradeNo(orderNo);
            transferForm.setTradeType(TradeType.REWARD.getType());
            transferForm.setContent(TradeType.REWARD.getContent());
            transferForm.setAmount(orderRewardVo.getRewardFee());
            transferForm.setDriverId(orderRewardVo.getDriverId());
            driverAccountFeignClient.transfer(transferForm);
        }

        //分账处理
        OrderProfitsharingVo orderProfitsharingVo = orderInfoFeignClient.getOrderProfitsharing(orderRewardVo.getOrderId()).getData();
        //封装分账参数对象
        ProfitsharingForm profitsharingForm = new ProfitsharingForm();
        profitsharingForm.setOrderNo(orderNo);
        profitsharingForm.setAmount(orderProfitsharingVo.getDriverIncome());
        profitsharingForm.setDriverId(orderRewardVo.getDriverId());

        ProfitsharingInfo profitsharingInfo = new ProfitsharingInfo();
        profitsharingInfo.setOrderNo(orderNo);
        profitsharingInfo.setAmount(String.valueOf(orderProfitsharingVo.getDriverIncome()));
        profitsharingInfo.setDriverId(orderRewardVo.getDriverId());
        profitsharingInfo.setState("FINISHED");
        //BeanUtils.copyProperties(profitsharingForm, profitsharingInfo);
        profitsharingInfoMapper.insert(profitsharingInfo);



        //分账有延迟，支付成功后最少2分钟执行分账申请
        rabbitService.sendDelayMessage(MqConst.EXCHANGE_PROFITSHARING, MqConst.ROUTING_PROFITSHARING, JSONObject.toJSONString(profitsharingForm), SystemConstant.PROFITSHARING_DELAY_TIME);
    }


}
