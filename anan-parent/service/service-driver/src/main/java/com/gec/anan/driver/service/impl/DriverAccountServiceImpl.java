package com.gec.anan.driver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gec.anan.driver.mapper.DriverAccountDetailMapper;
import com.gec.anan.driver.mapper.DriverAccountMapper;
import com.gec.anan.driver.service.DriverAccountService;
import com.gec.anan.model.entity.driver.DriverAccount;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.anan.model.entity.driver.DriverAccountDetail;
import com.gec.anan.model.form.driver.TransferForm;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverAccountServiceImpl extends ServiceImpl<DriverAccountMapper, DriverAccount> implements DriverAccountService {



    @Autowired
    private DriverAccountMapper driverAccountMapper;

    @Autowired
    private DriverAccountDetailMapper driverAccountDetailMapper;

    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean transfer(TransferForm transferForm) {
        //去重
        long count = driverAccountDetailMapper.selectCount(new LambdaQueryWrapper<DriverAccountDetail>().eq(DriverAccountDetail::getTradeNo, transferForm.getTradeNo()));
        if(count > 0) return true;

        //添加账号金额
        driverAccountMapper.add(transferForm.getDriverId(), transferForm.getAmount());

        //添加账户明细
        DriverAccountDetail driverAccountDetail = new DriverAccountDetail();
        BeanUtils.copyProperties(transferForm, driverAccountDetail);
        driverAccountDetailMapper.insert(driverAccountDetail);
        return true;
    }
}
