package com.gec.anan.customer.client;

import com.gec.anan.common.result.Result;
import com.gec.anan.model.form.customer.UpdateWxPhoneForm;
import com.gec.anan.model.vo.customer.CustomerLoginVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-customer")
public interface CustomerInfoFeignClient {

    /**
     * 小程序授权登录
     * @param code
     * @return
     */
    @GetMapping("/customer/info/login/{code}")
    Result<Long> login(@PathVariable String code);

    /**
     * 获取客户登录信息
     * @param customerId
     * @return
     */
    @GetMapping("/customer/info/getCustomerLoginInfo/{customerId}")
    Result<CustomerLoginVo> getCustomerLoginInfo(@PathVariable("customerId") Long customerId);

    /**
     * 更新客户微信手机号码
     * @param updateWxPhoneForm
     * @return
     */
    @PostMapping("/customer/info/updateWxPhoneNumber")
    Result<Boolean> updateWxPhoneNumber(@RequestBody UpdateWxPhoneForm updateWxPhoneForm);

    /**
     * 获取客户OpenId
     * @param customerId
     * @return
     */
    @GetMapping("/customer/info/getCustomerOpenId/{customerId}")
    Result<String> getCustomerOpenId(@PathVariable("customerId") Long customerId);

}