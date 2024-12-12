package com.gec.anan.payment.service;

import com.gec.anan.model.form.payment.PaymentInfoForm;
import com.gec.anan.model.vo.payment.WxPrepayVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface WxPayService {


    WxPrepayVo createWxPayment(PaymentInfoForm paymentInfoForm);

    Map<String, Object> wxnotify(HttpServletRequest request);

    Boolean queryPayStatus(String orderNo);

    void handleOrder(String orderNo);
}
