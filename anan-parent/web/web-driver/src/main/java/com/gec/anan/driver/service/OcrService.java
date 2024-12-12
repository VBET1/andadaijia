package com.gec.anan.driver.service;

import com.gec.anan.model.vo.driver.DriverLicenseOcrVo;
import com.gec.anan.model.vo.driver.IdCardOcrVo;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {

    IdCardOcrVo idCardOcr(MultipartFile file);
    DriverLicenseOcrVo driverLicenseOcr(MultipartFile file);

}
