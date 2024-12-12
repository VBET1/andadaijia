package com.gec.anan.driver.service;

import com.gec.anan.model.entity.driver.DriverAccount;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.anan.model.form.driver.TransferForm;

public interface DriverAccountService extends IService<DriverAccount> {
    Boolean transfer(TransferForm transferForm);


}
