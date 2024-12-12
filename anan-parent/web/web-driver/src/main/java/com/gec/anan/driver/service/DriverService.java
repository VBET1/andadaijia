package com.gec.anan.driver.service;

import com.gec.anan.model.form.driver.DriverFaceModelForm;
import com.gec.anan.model.form.driver.UpdateDriverAuthInfoForm;
import com.gec.anan.model.vo.driver.DriverAuthInfoVo;
import com.gec.anan.model.vo.driver.DriverLoginVo;

public interface DriverService {

    String login(String code);
    //获取司机登录信息
    DriverLoginVo getDriverLoginInfo(Long driverId);
    //获取司机认证信息
    DriverAuthInfoVo getDriverAuthInfo(Long driverId);
    //更新司机认证信息
    Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm);
    //创建司机人脸模型
    Boolean creatDriverFaceModel(DriverFaceModelForm driverFaceModelForm);
    //判断司机日常人脸打卡
    Boolean isFaceRecognition(Long driverId);
    //判断司机是否打卡
    Boolean verifyDriverFace(DriverFaceModelForm driverFaceModelForm);
    //接单
    Boolean startService(Long driverId);
    //结束服务
    Boolean stopService(Long driverId);
}
