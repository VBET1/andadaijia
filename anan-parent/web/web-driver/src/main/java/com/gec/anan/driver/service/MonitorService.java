package com.gec.anan.driver.service;

import com.gec.anan.model.form.order.OrderMonitorForm;
import org.springframework.web.multipart.MultipartFile;

public interface MonitorService {


    Boolean upload(MultipartFile file, OrderMonitorForm orderMonitorForm);
}
