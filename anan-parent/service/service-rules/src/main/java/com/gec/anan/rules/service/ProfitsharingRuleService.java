package com.gec.anan.rules.service;

import com.gec.anan.model.form.rules.ProfitsharingRuleRequestForm;
import com.gec.anan.model.vo.rules.ProfitsharingRuleResponseVo;

public interface ProfitsharingRuleService {


    ProfitsharingRuleResponseVo calculateOrderProfitsharingFee(ProfitsharingRuleRequestForm profitsharingRuleRequestForm);
}
