package com.gec.anan.customer.controller;

import com.gec.anan.common.constant.RedisConstant;
import com.gec.anan.common.login.AnanLogin;
import com.gec.anan.common.result.Result;
import com.gec.anan.common.util.AuthContextHolder;
import com.gec.anan.customer.service.CustomerService;
import com.gec.anan.model.form.customer.UpdateWxPhoneForm;
import com.gec.anan.model.vo.customer.CustomerLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
    @Tag(name = "客户API接口管理")
    @RestController
    @RequestMapping("/customer")
/**
 * @SuppressWarnings({"unchecked", "rawtypes"}) 是一个 Java 注解，用于抑制编译器在特定代码段中显示特定类型的警告。在这个例子中，{"unchecked", "rawtypes"} 表示抑制两种类型的警告：
 *
 * unchecked: 抑制未经检查的类型转换警告。这种警告通常出现在涉及泛型的代码中，当编译器无法验证类型安全性时会发出警告。例如，从一个原始类型（raw type）转换到一个泛型类型时。
 *
 * rawtypes: 抑制原始类型警告。这种警告发生在使用泛型类或接口的原始类型时。例如，使用 List 而不是 List<String> 或 List<Integer> 之类的具体类型时。
 */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public class CustomerController {

        @Autowired
        private CustomerService customerInfoService;

        @Operation(summary = "小程序授权登录")
        @GetMapping("/login/{code}")
        public Result<String> wxLogin(@PathVariable String code) {
            return Result.ok(customerInfoService.login(code));
        }

        @Autowired
        private RedisTemplate redisTemplate;
    @Operation(summary = "获取客户登录信息")
    @AnanLogin
    @GetMapping("/getCustomerLoginInfo")
    public Result<CustomerLoginVo> getCustomerLoginInfo() {

        //1 从ThreadLocal获取用户id
        Long customerId = AuthContextHolder.getUserId();

        //调用service
        CustomerLoginVo customerLoginVo = customerInfoService.getCustomerLoginInfo(customerId);

        return Result.ok(customerLoginVo);
    }


    @Operation(summary = "更新用户微信手机号")
    @AnanLogin
    @PostMapping("/updateWxPhone")
    public Result updateWxPhone(@RequestBody UpdateWxPhoneForm updateWxPhoneForm) {
        updateWxPhoneForm.setCustomerId(AuthContextHolder.getUserId());
        //customerInfoService.updateWxPhoneNumber(updateWxPhoneForm);
        return Result.ok(true);
    }
    }



