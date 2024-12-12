package com.gec.anan.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.anan.model.form.map.CalculateDrivingLineForm;
import com.gec.anan.model.vo.map.DrivingLineVo;

public interface MapService  {
    DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm);
}
