package com.gec.anan.customer.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gec.anan.customer.mapper.CustomerInfoMapper;
import com.gec.anan.customer.mapper.CustomerLoginLogMapper;
import com.gec.anan.customer.service.CustomerInfoService;
import com.gec.anan.model.entity.customer.CustomerInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.anan.model.entity.customer.CustomerLoginLog;
import com.gec.anan.model.form.customer.UpdateWxPhoneForm;
import com.gec.anan.model.vo.customer.CustomerLoginVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerInfoServiceImpl extends ServiceImpl<CustomerInfoMapper, CustomerInfo> implements CustomerInfoService {
    @Autowired
    private WxMaService wxMaService;

    @Autowired
    private CustomerLoginLogMapper customerLoginLogMapper;

    /**
     * 条件：
     * 1、前端开发者appid与服务器端appid一致
     * 2、前端开发者必须加入开发者
     *
     * @param code
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Long login(String code) {
        String openId = null;
        try {
            //获取openId
            WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
            openId = sessionInfo.getOpenid();
            log.info("【小程序授权】openId={}", openId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        //2 根据openid查询数据库表，判断是否第一次登录
        //如果openid不存在返回null，如果存在返回一条记录
        CustomerInfo customerInfo = this.getOne(new LambdaQueryWrapper<CustomerInfo>().eq(CustomerInfo::getWxOpenId, openId));
        //3 如果第一次登录，添加信息到用户表
        if (null == customerInfo) {
            customerInfo = new CustomerInfo();
            customerInfo.setNickname(String.valueOf(System.currentTimeMillis()));
            customerInfo.setAvatarUrl("https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
            customerInfo.setWxOpenId(openId);
            this.save(customerInfo);
        }

        //4 记录登录日志信息
        CustomerLoginLog customerLoginLog = new CustomerLoginLog();
        customerLoginLog.setCustomerId(customerInfo.getId());
        customerLoginLog.setMsg("小程序登录");
        customerLoginLogMapper.insert(customerLoginLog);

        //5 返回用户id
        return customerInfo.getId();
    }

    @Override
    public CustomerLoginVo getCustomerLoginInfo(Long customerId) {
        CustomerInfo customerInfo = this.getById(customerId);
        CustomerLoginVo customerInfoVo = new CustomerLoginVo();
        BeanUtils.copyProperties(customerInfo, customerInfoVo);
        //判断是否绑定手机号码，如果未绑定，小程序端发起绑定事件
        Boolean isBindPhone = StringUtils.hasText(customerInfo.getPhone());
        customerInfoVo.setIsBindPhone(isBindPhone);
        return customerInfoVo;
    }

    @SneakyThrows
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm) {
        // 调用微信 API 获取用户的手机号
        WxMaPhoneNumberInfo phoneInfo = wxMaService.getUserService().getPhoneNoInfo(updateWxPhoneForm.getCode());
        String phoneNumber = phoneInfo.getPhoneNumber();
        //String phoneNumber = "13322637136";
        log.info("phoneInfo:{}", JSON.toJSONString(phoneInfo));

        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setId(updateWxPhoneForm.getCustomerId());
        customerInfo.setPhone(phoneNumber);
        return this.updateById(customerInfo);

    }


    @Override
    public String getCustomerOpenId(Long customerId) {
        CustomerInfo customerInfo = this.getOne(new LambdaQueryWrapper<CustomerInfo>().eq(CustomerInfo::getId, customerId).select(CustomerInfo::getWxOpenId));
        return customerInfo.getWxOpenId();
    }
}
