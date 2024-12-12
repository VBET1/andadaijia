package com.gec.anan.driver.service;

import com.gec.anan.model.form.map.CalculateDrivingLineForm;
import com.gec.anan.model.form.order.OrderFeeForm;
import com.gec.anan.model.form.order.StartDriveForm;
import com.gec.anan.model.form.order.UpdateOrderCartForm;
import com.gec.anan.model.vo.base.PageVo;
import com.gec.anan.model.vo.map.DrivingLineVo;
import com.gec.anan.model.vo.map.OrderServiceLastLocationVo;
import com.gec.anan.model.vo.order.CurrentOrderInfoVo;
import com.gec.anan.model.vo.order.NewOrderDataVo;
import com.gec.anan.model.vo.order.OrderInfoVo;

import java.util.List;

public interface OrderService {
    Integer getOrderStatus(Long orderId);
    //抢新订单
    Boolean robNewOrder(Long driverId, Long orderId);
    List<NewOrderDataVo> findNewOrderQueueData(Long driverId);

    DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm);

    Boolean driverArriveStartLocation(Long orderId, Long driverId);

    Boolean updateOrderCart(UpdateOrderCartForm updateOrderCartForm);

    CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId);

    CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId);

    OrderInfoVo getOrderInfo(Long orderId, Long customerId);

    Boolean startDrive(StartDriveForm startDriveForm);

    OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId);
    Boolean endDrive(OrderFeeForm orderFeeForm);

    PageVo findDriverOrderPage(Long driverId, Long page, Long limit);

    Boolean sendOrderBillInfo(Long orderId, Long driverId);

}
