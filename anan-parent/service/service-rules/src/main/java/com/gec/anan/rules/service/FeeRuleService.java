package com.gec.anan.rules.service;

import com.gec.anan.model.form.rules.FeeRuleRequestForm;
import com.gec.anan.model.vo.rules.FeeRuleResponseVo;

public interface FeeRuleService {
    FeeRuleResponseVo calculateOrderFee(FeeRuleRequestForm calculateOrderFeeForm);
}
