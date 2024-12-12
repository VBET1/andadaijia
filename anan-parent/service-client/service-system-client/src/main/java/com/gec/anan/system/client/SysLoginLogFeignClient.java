package com.gec.anan.system.client;

import com.gec.anan.common.result.Result;
import com.gec.anan.model.entity.system.SysLoginLog;
import com.gec.anan.model.query.system.SysLoginLogQuery;
import com.gec.anan.model.vo.base.PageVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 产品列表API接口
 * </p>
 *
 * @author qy
 */
@FeignClient(value = "service-system")
public interface SysLoginLogFeignClient {

    @PostMapping("/sysLoginLog/findPage/{page}/{limit}")
    Result<PageVo<SysLoginLog>> findPage(
            @PathVariable("page") Long page,
            @PathVariable("limit") Long limit,
            @RequestBody SysLoginLogQuery sysLoginLogQuery);

    @GetMapping("/sysLoginLog/getById/{id}")
    Result<SysLoginLog> getById(@PathVariable Long id);

    /**
     * 记录登录日志
     *
     * @param sysLoginLog
     * @return
     */
    @PostMapping("/sysLoginLog/recordLoginLog")
    Result<Boolean> recordLoginLog(@RequestBody SysLoginLog sysLoginLog);
}