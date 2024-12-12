package com.gec.anan.driver.service.impl;



import com.gec.anan.common.constant.RedisConstant;
import com.gec.anan.driver.service.LocationService;
import com.gec.anan.map.client.LocationFeignClient;
import com.gec.anan.model.form.map.OrderServiceLocationForm;
import com.gec.anan.model.form.map.UpdateDriverLocationForm;
import com.gec.anan.model.form.map.UpdateOrderLocationForm;
import com.gec.anan.model.vo.map.OrderLocationVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {
    @Autowired
    LocationFeignClient locationFeignClient;
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public Boolean saveOrderServiceLocation(List<OrderServiceLocationForm> orderLocationServiceFormList) {
        return locationFeignClient.saveOrderServiceLocation(orderLocationServiceFormList).getData();
    }

    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        return locationFeignClient.updateDriverLocation(updateDriverLocationForm).getData();
    }

    @Override
    public Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm) {
        return locationFeignClient.updateOrderLocationToCache(updateOrderLocationForm).getData();
    }
}
