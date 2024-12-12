package com.gec.anan.driver.service;

import com.gec.anan.model.vo.order.TextAuditingVo;

public interface CiService {

    Boolean imageAuditing(String path);
    TextAuditingVo textAuditing(String content);
}
