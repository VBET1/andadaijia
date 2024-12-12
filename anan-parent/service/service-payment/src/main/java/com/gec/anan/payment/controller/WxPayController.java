package com.gec.anan.payment.controller;

import com.gec.anan.common.result.Result;
import com.gec.anan.model.form.payment.PaymentInfoForm;
import com.gec.anan.model.vo.payment.WxPrepayVo;
import com.gec.anan.payment.service.WxPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Tag(name = "微信支付接口")
@RestController
@RequestMapping("payment/wxPay")
@Slf4j
public class WxPayController {

    @Autowired
    WxPayService wxPayService;

    // 	http://vbet.free.idcfengye.com/payment/wxPay/test  [内网穿透]
    @Operation(summary = "接口测试")
    @GetMapping("/test")
    public String test() {
        return "test--->success!!!!";
    }

    @Operation(summary = "支付状态查询")
    @GetMapping("/queryPayStatus/{orderNo}")
    public Result<Boolean> queryPayStatus(@PathVariable String orderNo) {
        return Result.ok(wxPayService.queryPayStatus(orderNo));
    }


    // http://127.0.0.1:8506/payment/wxPay/notify  [内网穿透]
    @Operation(summary = "微信支付异步通知接口")
    @PostMapping("/notify")
    public Map<String, Object> notify(HttpServletRequest request) {

        try {
            //处理响应结果

            wxPayService.wxnotify(request);

            //返回成功
            Map<String, Object> result = new HashMap<>();
            result.put("code", "SUCCESS");
            result.put("message", "成功");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        //返回失败
        Map<String, Object> result = new HashMap<>();
        result.put("code", "FAIL");
        result.put("message", "失败");
        return result;
    }


    @Operation(summary = "创建微信支付")
    @PostMapping("/createWxPayment")
    public Result<WxPrepayVo> createWxPayment(@RequestBody PaymentInfoForm paymentInfoForm) {
        System.out.println("paymentInfoForm = " + paymentInfoForm);
        return Result.ok(wxPayService.createWxPayment(paymentInfoForm));
    }

}
