package com.gec.anan.driver.mapper;

import com.gec.anan.model.entity.driver.DriverAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface DriverAccountMapper extends BaseMapper<DriverAccount> {
    Integer add(@Param("driverId") Long userId, @Param("amount") BigDecimal amount);
}
