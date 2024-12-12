package com.gec.anan.customer.service.impl;

import com.gec.anan.coupon.client.CouponFeignClient;
import com.gec.anan.customer.service.CouponService;
import com.gec.anan.model.vo.base.PageVo;
import com.gec.anan.model.vo.coupon.AvailableCouponVo;
import com.gec.anan.model.vo.coupon.NoReceiveCouponVo;
import com.gec.anan.model.vo.coupon.NoUseCouponVo;
import com.gec.anan.model.vo.coupon.UsedCouponVo;
import com.gec.anan.model.vo.order.OrderBillVo;
import com.gec.anan.order.client.OrderInfoFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponFeignClient couponFeignClient;

    @Override
    public PageVo<NoReceiveCouponVo> findNoReceivePage(Long customerId, Long page, Long limit) {
        return couponFeignClient.findNoReceivePage(customerId, page, limit).getData();
    }

    @Override
    public PageVo<NoUseCouponVo> findNoUsePage(Long customerId, Long page, Long limit) {
        return couponFeignClient.findNoUsePage(customerId, page, limit).getData();
    }

    @Override
    public PageVo<UsedCouponVo> findUsedPage(Long customerId, Long page, Long limit) {
        return couponFeignClient.findUsedPage(customerId, page, limit).getData();
    }


    @Override
    public Boolean receive(Long customerId, Long couponId) {
        return couponFeignClient.receive(customerId, couponId).getData();
    }

    @Autowired
    private OrderInfoFeignClient orderInfoFeignClient;

    @Override
    public List<AvailableCouponVo> findAvailableCoupon(Long customerId, Long orderId) {
        OrderBillVo orderBillVo = orderInfoFeignClient.getOrderBillInfo(orderId).getData();
        return couponFeignClient.findAvailableCoupon(customerId, orderBillVo.getPayAmount()).getData();
    }


}
