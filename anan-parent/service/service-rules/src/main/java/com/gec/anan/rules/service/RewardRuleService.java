package com.gec.anan.rules.service;

import com.gec.anan.model.form.rules.RewardRuleRequestForm;
import com.gec.anan.model.vo.rules.RewardRuleResponseVo;

public interface RewardRuleService {


    RewardRuleResponseVo calculateOrderRewardFee(RewardRuleRequestForm rewardRuleRequestForm);
}
