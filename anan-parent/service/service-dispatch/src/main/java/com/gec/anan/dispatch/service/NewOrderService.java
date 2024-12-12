package com.gec.anan.dispatch.service;

import com.gec.anan.model.vo.dispatch.NewOrderTaskVo;
import com.gec.anan.model.vo.order.NewOrderDataVo;

import java.util.List;

public interface NewOrderService {

    List<NewOrderDataVo> findNewOrderQueueData(Long driverId);

    Boolean clearNewOrderQueueData(Long driverId);

    //添加及启动任务[发布了一个job]
    Long addAndStartTask(NewOrderTaskVo newOrderTaskVo);

    //执行任务
    Boolean executeTask(Long jobId);
}
