package com.gec.anan.order.service;

import com.gec.anan.model.entity.order.OrderMonitor;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.anan.model.entity.order.OrderMonitorRecord;

public interface OrderMonitorService extends IService<OrderMonitor> {


    Boolean saveOrderMonitorRecord(OrderMonitorRecord orderMonitorRecord);

    Long saveOrderMonitor(OrderMonitor orderMonitor);

    OrderMonitor getOrderMonitor(Long orderId);

    Boolean updateOrderMonitor(OrderMonitor orderMonitor);
}
