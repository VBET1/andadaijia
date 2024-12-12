package com.gec.anan.coupon.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.anan.model.entity.coupon.CouponInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.anan.model.vo.coupon.AvailableCouponVo;
import com.gec.anan.model.vo.coupon.NoReceiveCouponVo;
import com.gec.anan.model.vo.coupon.NoUseCouponVo;
import com.gec.anan.model.vo.coupon.UsedCouponVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {


    IPage<NoReceiveCouponVo> findNoReceivePage(Page<CouponInfo> pageParam, @Param("customerId") Long customerId);

    IPage<NoUseCouponVo> findNoUsePage(Page<CouponInfo> pageParam, @Param("customerId") Long customerId);

    IPage<UsedCouponVo> findUsedPage(Page<CouponInfo> pageParam, @Param("customerId") Long customerId);

    int updateReceiveCount(@Param("id") Long id);


    int updateReceiveCountByLimit(@Param("id") Long id);


    List<NoUseCouponVo> findNoUseList(@Param("customerId") Long customerId);

    int updateUseCount(@Param("id") Long id);
}
