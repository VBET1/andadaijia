package com.gec.anan.coupon.mapper;

import com.gec.anan.model.entity.coupon.CustomerCoupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

@Mapper
public interface CustomerCouponMapper extends BaseMapper<CustomerCoupon> {
    //保存用户领取的优惠券
    int saveCustomerCoupon(Long customerId, Long couponId, Date receiveTime);
}
