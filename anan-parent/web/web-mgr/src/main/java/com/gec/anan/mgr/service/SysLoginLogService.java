package com.gec.anan.mgr.service;

import com.gec.anan.model.entity.system.SysLoginLog;
import com.gec.anan.model.query.system.SysLoginLogQuery;
import com.gec.anan.model.vo.base.PageVo;

public interface SysLoginLogService {

    PageVo<SysLoginLog> findPage(Long page, Long limit, SysLoginLogQuery sysLoginLogQuery);

    /**
     * 记录登录信息
     *
     * @param sysLoginLog
     * @return
     */
    void recordLoginLog(SysLoginLog sysLoginLog);

    SysLoginLog getById(Long id);
}
