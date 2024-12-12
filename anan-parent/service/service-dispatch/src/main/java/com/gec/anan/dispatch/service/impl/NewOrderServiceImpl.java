package com.gec.anan.dispatch.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gec.anan.common.constant.RedisConstant;
import com.gec.anan.dispatch.mapper.OrderJobMapper;
import com.gec.anan.dispatch.service.NewOrderService;
import com.gec.anan.dispatch.xxl.client.XxlJobClient;
import com.gec.anan.map.client.LocationFeignClient;
import com.gec.anan.model.entity.dispatch.OrderJob;
import com.gec.anan.model.enums.OrderStatus;
import com.gec.anan.model.form.map.SearchNearByDriverForm;
import com.gec.anan.model.vo.dispatch.NewOrderTaskVo;
import com.gec.anan.model.vo.map.NearByDriverVo;
import com.gec.anan.model.vo.order.NewOrderDataVo;
import com.gec.anan.order.client.OrderInfoFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class NewOrderServiceImpl implements NewOrderService {

    //判断任务有没有创建、是否有记录信息
    @Autowired
    OrderJobMapper orderJobMapper;
    @Autowired
    XxlJobClient xxlJobClient;

    @Autowired
    OrderInfoFeignClient orderInfoFeignClient;
    @Autowired
    LocationFeignClient locationFeignClient;
    @Autowired
    RedisTemplate redisTemplate;


    //执行任务[任务的执行逻辑【搜索司机】和控制]
    @Override
    public Boolean executeTask(Long jobId) {
        //获取任务参数
        OrderJob orderJob = orderJobMapper.selectOne(new LambdaQueryWrapper<OrderJob>().eq(OrderJob::getJobId, jobId));
        if(null == orderJob) {
            return true;
        }
        NewOrderTaskVo newOrderTaskVo = JSONObject.parseObject(orderJob.getParameter(), NewOrderTaskVo.class);

        //查询订单状态，如果该订单还在接单状态，继续执行；如果不在接单状态，则停止定时调度
        Integer orderStatus = orderInfoFeignClient.getOrderStatus(newOrderTaskVo.getOrderId()).getData();
        if(orderStatus.intValue() != OrderStatus.WAITING_ACCEPT.getStatus().intValue()) {
            xxlJobClient.stopJob(jobId);
            log.info("停止任务调度: {}", JSONObject.toJSONString(newOrderTaskVo));
            return true;
        }

        //搜索附近满足条件的司机
        SearchNearByDriverForm searchNearByDriverForm = new SearchNearByDriverForm();
        searchNearByDriverForm.setLongitude(newOrderTaskVo.getStartPointLongitude());
        searchNearByDriverForm.setLatitude(newOrderTaskVo.getStartPointLatitude());
        searchNearByDriverForm.setMileageDistance(newOrderTaskVo.getExpectDistance());
        List<NearByDriverVo> nearByDriverVoList = locationFeignClient.searchNearByDriver(searchNearByDriverForm).getData();
        //给司机派发订单信息
        nearByDriverVoList.forEach(driver -> {
            //记录司机id，防止重复推送订单信息
            String repeatKey = RedisConstant.DRIVER_ORDER_REPEAT_LIST+newOrderTaskVo.getOrderId();
            boolean isMember = redisTemplate.opsForSet().isMember(repeatKey, driver.getDriverId());
            if(!isMember) {
                //记录该订单已放入司机临时容器
                redisTemplate.opsForSet().add(repeatKey, driver.getDriverId());
                //过期时间：15分钟，新订单15分钟没人接单自动取消
                redisTemplate.expire(repeatKey, RedisConstant.DRIVER_ORDER_REPEAT_LIST_EXPIRES_TIME, TimeUnit.MINUTES);

                NewOrderDataVo newOrderDataVo = new NewOrderDataVo();
                newOrderDataVo.setOrderId(newOrderTaskVo.getOrderId());
                newOrderDataVo.setStartLocation(newOrderTaskVo.getStartLocation());
                newOrderDataVo.setEndLocation(newOrderTaskVo.getEndLocation());
                newOrderDataVo.setExpectAmount(newOrderTaskVo.getExpectAmount());
                newOrderDataVo.setExpectDistance(newOrderTaskVo.getExpectDistance());
                newOrderDataVo.setExpectTime(newOrderTaskVo.getExpectTime());
                newOrderDataVo.setFavourFee(newOrderTaskVo.getFavourFee());
                newOrderDataVo.setDistance(driver.getDistance());
                newOrderDataVo.setCreateTime(newOrderTaskVo.getCreateTime());

                //将消息保存到司机的临时队列里面，司机接单了会定时轮询到他的临时队列获取订单消息
                String key = RedisConstant.DRIVER_ORDER_TEMP_LIST+driver.getDriverId();
                redisTemplate.opsForList().leftPush(key, JSONObject.toJSONString(newOrderDataVo));
                //过期时间：1分钟，1分钟未消费，自动过期
                //注：司机端开启接单，前端每5秒（远小于1分钟）拉取1次“司机临时队列”里面的新订单消息
                redisTemplate.expire(key, RedisConstant.DRIVER_ORDER_TEMP_LIST_EXPIRES_TIME, TimeUnit.MINUTES);
                log.info("该新订单信息已放入司机临时队列: {}", JSONObject.toJSONString(newOrderDataVo));
            }
        });
        return true;
    }

    @Override
    public List<NewOrderDataVo> findNewOrderQueueData(Long driverId) {
        //在redis中获取该司机的临时的订单列表
        String key = RedisConstant.DRIVER_ORDER_TEMP_LIST + driverId;
        Long size = redisTemplate.opsForList().size(key);
        //List<NewOrderDataVo>
        List<NewOrderDataVo> list = new ArrayList<>();
        //判断非空
        if (size > 0) {
            //遍历list
            for (int i = 0; i < size; i++) {
                String content = (String) redisTemplate.opsForList().leftPop(key);
                NewOrderDataVo newOrderDataVo = JSONObject.parseObject(content, NewOrderDataVo.class);
                //把数据封装到NewOrderDataVo、再添加到list中
                list.add(newOrderDataVo);
            }
        }
        return list;
    }

    @Override
    public Boolean clearNewOrderQueueData(Long driverId) {
        //在redis中删除司机的列表信息
        String key = RedisConstant.DRIVER_ORDER_TEMP_LIST + driverId;
        redisTemplate.delete(key);
        return true;
    }


    @Override
    public Long addAndStartTask(NewOrderTaskVo newOrderTaskVo) {
        //查询作业任务是否存在
        OrderJob jobInfo = orderJobMapper.selectOne(
                new LambdaQueryWrapper<OrderJob>()
                        .eq(OrderJob::getOrderId, newOrderTaskVo.getOrderId())

        );
        //判断是否为空、空则创建
        if (jobInfo == null) {
            //提交新的任务\返回jobid
            Long jobId = xxlJobClient.addAndStart("newOrderTaskHandler", "", "0/5 * * * * ?", "新订单任务,订单id：" + newOrderTaskVo.getOrderId());
            //记录数据到业务表中
            jobInfo = new OrderJob();
            jobInfo.setOrderId(newOrderTaskVo.getOrderId());
            jobInfo.setJobId(jobId);
            jobInfo.setParameter(JSONObject.toJSONString(newOrderTaskVo));//把newOrderTaskVo的对象放在parameter中
            orderJobMapper.insert(jobInfo);
        }
        //最终要返回任务id
        return jobInfo.getId();
    }


}
