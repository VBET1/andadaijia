package com.gec.anan.customer.service.impl;

import com.gec.anan.common.execption.AnanException;
import com.gec.anan.common.result.ResultCodeEnum;
import com.gec.anan.coupon.client.CouponFeignClient;
import com.gec.anan.customer.client.CustomerInfoFeignClient;
import com.gec.anan.customer.service.OrderService;
import com.gec.anan.dispatch.client.NewOrderFeignClient;
import com.gec.anan.driver.client.DriverInfoFeignClient;
import com.gec.anan.map.client.LocationFeignClient;
import com.gec.anan.map.client.MapFeignClient;
import com.gec.anan.map.client.WxPayFeignClient;
import com.gec.anan.model.entity.order.OrderInfo;
import com.gec.anan.model.enums.OrderStatus;
import com.gec.anan.model.form.coupon.UseCouponForm;
import com.gec.anan.model.form.customer.ExpectOrderForm;
import com.gec.anan.model.form.customer.SubmitOrderForm;
import com.gec.anan.model.form.map.CalculateDrivingLineForm;
import com.gec.anan.model.form.order.OrderInfoForm;
import com.gec.anan.model.form.payment.CreateWxPaymentForm;
import com.gec.anan.model.form.payment.PaymentInfoForm;
import com.gec.anan.model.form.rules.FeeRuleRequestForm;
import com.gec.anan.model.vo.base.PageVo;
import com.gec.anan.model.vo.customer.ExpectOrderVo;
import com.gec.anan.model.vo.dispatch.NewOrderTaskVo;
import com.gec.anan.model.vo.driver.DriverInfoVo;
import com.gec.anan.model.vo.map.DrivingLineVo;
import com.gec.anan.model.vo.map.OrderLocationVo;
import com.gec.anan.model.vo.map.OrderServiceLastLocationVo;
import com.gec.anan.model.vo.order.CurrentOrderInfoVo;
import com.gec.anan.model.vo.order.OrderBillVo;
import com.gec.anan.model.vo.order.OrderInfoVo;
import com.gec.anan.model.vo.order.OrderPayVo;
import com.gec.anan.model.vo.payment.WxPrepayVo;
import com.gec.anan.model.vo.rules.FeeRuleResponseVo;
import com.gec.anan.order.client.OrderInfoFeignClient;
import com.gec.anan.rules.client.FeeRuleFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderServiceImpl implements OrderService {

    @Autowired
    private MapFeignClient mapFeignClient;

    @Autowired
    private FeeRuleFeignClient feeRuleFeignClient;


    @Override
    public PageVo findCustomerOrderPage(Long customerId, Long page, Long limit) {
        return orderInfoFeignClient.findCustomerOrderPage(customerId, page, limit).getData();
    }



    @Override
    public ExpectOrderVo expectOrder(ExpectOrderForm expectOrderForm) {
        //计算驾驶线路
        CalculateDrivingLineForm calculateDrivingLineForm = new CalculateDrivingLineForm();
        BeanUtils.copyProperties(expectOrderForm, calculateDrivingLineForm);
        DrivingLineVo drivingLineVo = mapFeignClient.calculateDrivingLine(calculateDrivingLineForm).getData();

        //计算订单费用
        FeeRuleRequestForm calculateOrderFeeForm = new FeeRuleRequestForm();
        calculateOrderFeeForm.setDistance(drivingLineVo.getDistance());
        calculateOrderFeeForm.setStartTime(new Date());
        calculateOrderFeeForm.setWaitMinute(0);
        FeeRuleResponseVo feeRuleResponseVo = feeRuleFeignClient.calculateOrderFee(calculateOrderFeeForm).getData();

        //预估订单实体
        ExpectOrderVo expectOrderVo = new ExpectOrderVo();
        expectOrderVo.setDrivingLineVo(drivingLineVo);
        expectOrderVo.setFeeRuleResponseVo(feeRuleResponseVo);
        return expectOrderVo;
    }

    @Autowired
    private NewOrderFeignClient newOrderFeignClient;

    @Override
    public Long submitOrder(SubmitOrderForm submitOrderForm) {
        //1.重新计算驾驶线路
        CalculateDrivingLineForm calculateDrivingLineForm = new CalculateDrivingLineForm();
        BeanUtils.copyProperties(submitOrderForm, calculateDrivingLineForm);
        DrivingLineVo drivingLineVo = mapFeignClient.calculateDrivingLine(calculateDrivingLineForm).getData();

        //2.重新计算订单费用
        FeeRuleRequestForm calculateOrderFeeForm = new FeeRuleRequestForm();
        calculateOrderFeeForm.setDistance(drivingLineVo.getDistance());
        calculateOrderFeeForm.setStartTime(new Date());
        calculateOrderFeeForm.setWaitMinute(0);
        FeeRuleResponseVo feeRuleResponseVo = feeRuleFeignClient.calculateOrderFee(calculateOrderFeeForm).getData();

        //3.封装订单信息对象
        OrderInfoForm orderInfoForm = new OrderInfoForm();
        //订单位置信息
        BeanUtils.copyProperties(submitOrderForm, orderInfoForm);
        //预估里程
        orderInfoForm.setExpectDistance(drivingLineVo.getDistance());
        orderInfoForm.setExpectAmount(feeRuleResponseVo.getTotalAmount());

        //4.保存订单信息
        Long orderId = orderInfoFeignClient.saveOrderInfo(orderInfoForm).getData();

        //5.添加并执行任务调度，每分钟执行一次，搜索附近司机
        //5.1.封装调度参数对象
        NewOrderTaskVo newOrderDispatchVo = new NewOrderTaskVo();
        newOrderDispatchVo.setOrderId(orderId);
        newOrderDispatchVo.setStartLocation(orderInfoForm.getStartLocation());
        newOrderDispatchVo.setStartPointLongitude(orderInfoForm.getStartPointLongitude());
        newOrderDispatchVo.setStartPointLatitude(orderInfoForm.getStartPointLatitude());
        newOrderDispatchVo.setEndLocation(orderInfoForm.getEndLocation());
        newOrderDispatchVo.setEndPointLongitude(orderInfoForm.getEndPointLongitude());
        newOrderDispatchVo.setEndPointLatitude(orderInfoForm.getEndPointLatitude());
        newOrderDispatchVo.setExpectAmount(orderInfoForm.getExpectAmount());
        newOrderDispatchVo.setExpectDistance(orderInfoForm.getExpectDistance());
        newOrderDispatchVo.setExpectTime(drivingLineVo.getDuration());
        newOrderDispatchVo.setFavourFee(orderInfoForm.getFavourFee());
        newOrderDispatchVo.setCreateTime(new Date());
        //5.2.添加并执行任务调度
        Long jobId = newOrderFeignClient.addAndStartTask(newOrderDispatchVo).getData();
        log.info("订单id为： {}，绑定任务id为：{}", orderId, jobId);
        return orderId;
    }



    @Autowired
    OrderInfoFeignClient orderInfoFeignClient;
    @Autowired
    DriverInfoFeignClient driverInfoFeignClient;
    @Autowired
    LocationFeignClient locationFeignClient;

    @Override
    public CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId) {
        return orderInfoFeignClient.searchCustomerCurrentOrder(customerId).getData();
    }
    @Override
    public Integer getOrderStatus(Long orderId) {
        return orderInfoFeignClient.getOrderStatus(orderId).getData();
    }

    @Override
    public DriverInfoVo getDriverInfo(Long orderId, Long customerId) {
        OrderInfo orderInfo = orderInfoFeignClient.getOrderInfo(orderId).getData();
        if (orderInfo.getCustomerId().longValue() != customerId.longValue()) {
            throw new AnanException(ResultCodeEnum.ILLEGAL_REQUEST);
        }
        return driverInfoFeignClient.getDriverInfo(orderInfo.getDriverId()).getData();
    }

    @Override
    public OrderLocationVo getCacheOrderLocation(Long orderId) {
        return locationFeignClient.getCacheOrderLocation(orderId).getData();
    }

    @Override
    public DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm) {
        return mapFeignClient.calculateDrivingLine(calculateDrivingLineForm).getData();
    }


    @Override
    public OrderInfoVo getOrderInfo(Long orderId, Long customerId) {
        //订单信息
        OrderInfo orderInfo = orderInfoFeignClient.getOrderInfo(orderId).getData();
        if (orderInfo.getCustomerId().longValue() != customerId.longValue()) {
            throw new AnanException(ResultCodeEnum.ILLEGAL_REQUEST);
        }

        //获取司机信息
        DriverInfoVo driverInfoVo = null;
        if(null != orderInfo.getDriverId()) {
            driverInfoVo = driverInfoFeignClient.getDriverInfo(orderInfo.getDriverId()).getData();
        }

        //账单信息
        OrderBillVo orderBillVo = null;
        if (orderInfo.getStatus().intValue() >= OrderStatus.UNPAID.getStatus().intValue()) {
            orderBillVo = orderInfoFeignClient.getOrderBillInfo(orderId).getData();
        }

        //封装订单信息
        OrderInfoVo orderInfoVo = new OrderInfoVo();
        orderInfoVo.setOrderId(orderId);
        orderInfoVo.setDriverInfoVo(driverInfoVo);
        orderInfoVo.setOrderBillVo(orderBillVo);
        BeanUtils.copyProperties(orderInfo, orderInfoVo);
        //orderInfoVo.setDriverInfoVo(driverInfoVo);
        return orderInfoVo;
    }


    @Override
    public OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId) {
        return locationFeignClient.getOrderServiceLastLocation(orderId).getData();
    }


    @Autowired
    private CustomerInfoFeignClient customerInfoFeignClient;

    @Autowired
    private WxPayFeignClient wxPayFeignClient;

/*
    @Override
    public WxPrepayVo createWxPayment(CreateWxPaymentForm createWxPaymentForm) {
        //1.获取订单支付相关信息
        OrderPayVo orderPayVo = orderInfoFeignClient.getOrderPayVo(createWxPaymentForm.getOrderNo(), createWxPaymentForm.getCustomerId()).getData();
        //判断是否在未支付状态
        if (orderPayVo.getStatus().intValue() != OrderStatus.UNPAID.getStatus().intValue()) {
            throw new AnanException(ResultCodeEnum.ILLEGAL_REQUEST);
        }

        //2.获取乘客微信openId
        String customerOpenId = customerInfoFeignClient.getCustomerOpenId(orderPayVo.getCustomerId()).getData();

        //3.获取司机微信openId
        String driverOpenId = driverInfoFeignClient.getDriverOpenId(orderPayVo.getDriverId()).getData();

        //4.封装微信下单对象，微信支付只关注以下订单属性
        PaymentInfoForm paymentInfoForm = new PaymentInfoForm();
        paymentInfoForm.setCustomerOpenId(customerOpenId);
        paymentInfoForm.setDriverOpenId(driverOpenId);
        paymentInfoForm.setOrderNo(orderPayVo.getOrderNo());
        paymentInfoForm.setAmount(orderPayVo.getPayAmount());
        paymentInfoForm.setContent(orderPayVo.getContent());
        paymentInfoForm.setPayWay(1);
        WxPrepayVo wxPrepayVo = wxPayFeignClient.createWxPayment(paymentInfoForm).getData();
        return wxPrepayVo;
    }
*/

    @Autowired
    private CouponFeignClient couponFeignClient;

        @Override
        public WxPrepayVo createWxPayment(CreateWxPaymentForm createWxPaymentForm) {
            //1.获取订单支付相关信息
            OrderPayVo orderPayVo = orderInfoFeignClient.getOrderPayVo(createWxPaymentForm.getOrderNo(), createWxPaymentForm.getCustomerId()).getData();
            //判断是否在未支付状态
             if (orderPayVo.getStatus().intValue() != OrderStatus.UNPAID.getStatus().intValue()) {
            throw new AnanException(ResultCodeEnum.ILLEGAL_REQUEST);
            }

             //2.获取乘客微信openId
                String customerOpenId = customerInfoFeignClient.getCustomerOpenId(orderPayVo.getCustomerId()).getData();

            //3.获取司机微信openId
                 String driverOpenId = driverInfoFeignClient.getDriverOpenId(orderPayVo.getDriverId()).getData();

             //4.处理优惠券
                    BigDecimal couponAmount = null;
             //支付时选择过一次优惠券，如果支付失败或未支付，下次支付时不能再次选择，只能使用第一次选中的优惠券（前端已控制，后端再次校验）
             if (null == orderPayVo.getCouponAmount() && null != createWxPaymentForm.getCustomerCouponId() && createWxPaymentForm.getCustomerCouponId() != 0) {
                 UseCouponForm useCouponForm = new UseCouponForm();
                 useCouponForm.setOrderId(orderPayVo.getOrderId());
                 useCouponForm.setCustomerCouponId(createWxPaymentForm.getCustomerCouponId());
                 useCouponForm.setOrderAmount(orderPayVo.getPayAmount());
                 useCouponForm.setCustomerId(createWxPaymentForm.getCustomerId());
                 couponAmount = couponFeignClient.useCoupon(useCouponForm).getData();
                    }

                    //5.更新账单优惠券金额
                     //支付金额
            BigDecimal payAmount = orderPayVo.getPayAmount();
                 if (null != couponAmount) {
                     Boolean isUpdate = orderInfoFeignClient.updateCouponAmount(orderPayVo.getOrderId(), couponAmount).getData();
                         if (!isUpdate) {
                               throw new AnanException(ResultCodeEnum.DATA_ERROR);
                        }
                            //当前支付金额 = 支付金额 - 优惠券金额
                        payAmount = payAmount.subtract(couponAmount);
                }

                 //6.封装微信下单对象，微信支付只关注以下订单属性
                 PaymentInfoForm paymentInfoForm = new PaymentInfoForm();
                 paymentInfoForm.setCustomerOpenId(customerOpenId);
                 paymentInfoForm.setDriverOpenId(driverOpenId);
                 paymentInfoForm.setOrderNo(orderPayVo.getOrderNo());
                 paymentInfoForm.setAmount(payAmount);
                 paymentInfoForm.setContent(orderPayVo.getContent());
                 paymentInfoForm.setPayWay(1);
                 WxPrepayVo wxPrepayVo = wxPayFeignClient.createWxPayment(paymentInfoForm).getData();
                    return wxPrepayVo;
}


    @Override
    public Boolean queryPayStatus(String orderNo) {
        return wxPayFeignClient.queryPayStatus(orderNo).getData();
    }
}
