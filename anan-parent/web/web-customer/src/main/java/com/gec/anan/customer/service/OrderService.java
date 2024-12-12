package com.gec.anan.customer.service;

import com.gec.anan.model.form.customer.ExpectOrderForm;
import com.gec.anan.model.form.customer.SubmitOrderForm;
import com.gec.anan.model.form.map.CalculateDrivingLineForm;
import com.gec.anan.model.form.payment.CreateWxPaymentForm;
import com.gec.anan.model.vo.base.PageVo;
import com.gec.anan.model.vo.customer.ExpectOrderVo;
import com.gec.anan.model.vo.driver.DriverInfoVo;
import com.gec.anan.model.vo.map.DrivingLineVo;
import com.gec.anan.model.vo.map.OrderLocationVo;
import com.gec.anan.model.vo.map.OrderServiceLastLocationVo;
import com.gec.anan.model.vo.order.CurrentOrderInfoVo;
import com.gec.anan.model.vo.order.OrderInfoVo;
import com.gec.anan.model.vo.payment.WxPrepayVo;

public interface OrderService {
    ExpectOrderVo expectOrder(ExpectOrderForm expectOrderForm);
   Long submitOrder(SubmitOrderForm submitOrderForm);
    Integer getOrderStatus(Long orderId);
    DriverInfoVo getDriverInfo(Long orderId, Long customerId);
    OrderLocationVo getCacheOrderLocation(Long orderId);
    DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm);
    OrderInfoVo getOrderInfo(Long orderId, Long customerId);
    CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId);
    OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId);

    PageVo findCustomerOrderPage(Long customerId, Long page, Long limit);

    WxPrepayVo createWxPayment(CreateWxPaymentForm createWxPaymentForm);

    Boolean queryPayStatus(String orderNo);
}
