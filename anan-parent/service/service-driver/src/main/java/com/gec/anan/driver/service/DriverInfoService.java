package com.gec.anan.driver.service;

import com.gec.anan.model.entity.driver.DriverInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.anan.model.entity.driver.DriverSet;
import com.gec.anan.model.form.driver.DriverFaceModelForm;
import com.gec.anan.model.form.driver.UpdateDriverAuthInfoForm;
import com.gec.anan.model.vo.driver.DriverAuthInfoVo;
import com.gec.anan.model.vo.driver.DriverInfoVo;
import com.gec.anan.model.vo.driver.DriverLoginVo;

public interface DriverInfoService extends IService<DriverInfo> {
    //根据微信小程序code获取用户信息
    Long login(String code);
    //根据司机id获取司机登录信息
    DriverLoginVo getDriverLoginInfo(Long driverId);
    //根据司机id获取司机认证信息
    DriverAuthInfoVo getDriverAuthInfo(Long driverId);
    //更新司机认证信息
    Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm);
    //创建人脸库
    Boolean creatDriverFaceModel(DriverFaceModelForm driverFaceModelForm);
    //人脸识别
    Boolean isFaceRecognition(Long driverId);
    Boolean verifyDriverFace(DriverFaceModelForm driverFaceModelForm);
    //接单
    Boolean updateServiceStatus(Long driverId, Integer status);

    DriverSet getDriverSet(Long driverId);

    DriverInfoVo getDriverInfo(Long driverId);

    String getDriverOpenId(Long driverId);

}
