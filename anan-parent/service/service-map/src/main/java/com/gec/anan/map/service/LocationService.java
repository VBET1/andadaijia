package com.gec.anan.map.service;

import com.gec.anan.model.form.map.OrderServiceLocationForm;
import com.gec.anan.model.form.map.SearchNearByDriverForm;
import com.gec.anan.model.form.map.UpdateDriverLocationForm;
import com.gec.anan.model.form.map.UpdateOrderLocationForm;
import com.gec.anan.model.vo.map.NearByDriverVo;
import com.gec.anan.model.vo.map.OrderLocationVo;
import com.gec.anan.model.vo.map.OrderServiceLastLocationVo;

import java.math.BigDecimal;
import java.util.List;

public interface LocationService {
    Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm);

    Boolean removeDriverLocation(Long driverId);

    List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm);
    Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm);
    OrderLocationVo getCacheOrderLocation(Long orderId);
    Boolean saveOrderServiceLocation(List<OrderServiceLocationForm> orderLocationServiceFormList);
    OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId);
    BigDecimal calculateOrderRealDistance(Long orderId);

}
