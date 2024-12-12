package com.gec.anan.dispatch.xxl.job;

import com.alibaba.nacos.common.utils.ExceptionUtil;
import com.gec.anan.dispatch.mapper.XxlJobLogMapper;
import com.gec.anan.dispatch.service.NewOrderService;
import com.gec.anan.model.entity.dispatch.XxlJobLog;
import com.gec.anan.model.vo.dispatch.NewOrderTaskVo;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//作业【任务：新订单发起-定时查询司机列表】
@Slf4j
@Component
public class JobHandler {

    @Autowired
    NewOrderService newOrderService;

    @Autowired
    XxlJobLogMapper xxlJobLogMapper;

    @XxlJob("firstJobHandler")
    public void firstJobHandler() {
        log.info("xxl-job项目集成测试");
    }

    //定义任务
    @XxlJob("newOrderTaskHandler")
    public void newOrderTaskHandler() {
        log.info("新订单发起-{}", XxlJobHelper.getJobId());
        //1、记录任务-日志-cj日志对象
        XxlJobLog xxlJobLog = new XxlJobLog();
        xxlJobLog.setJobId(XxlJobHelper.getJobId());
        //开始的时间
        long startTime = System.currentTimeMillis();
        //2、执行任务
        try {
            //执行任务
            newOrderService.executeTask(XxlJobHelper.getJobId());
        } catch (Exception e) {
            xxlJobLog.setStatus(0);
            xxlJobLog.setError(ExceptionUtil.getAllExceptionMsg(e));//异常错误信息
            e.printStackTrace();
        } finally {
            xxlJobLog.setTimes((int) (startTime - System.currentTimeMillis()));
            xxlJobLogMapper.insert(xxlJobLog);
        }

    }


}
