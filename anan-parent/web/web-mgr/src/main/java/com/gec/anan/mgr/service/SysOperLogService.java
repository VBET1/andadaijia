package com.gec.anan.mgr.service;

import com.gec.anan.model.entity.system.SysOperLog;
import com.gec.anan.model.query.system.SysOperLogQuery;
import com.gec.anan.model.vo.base.PageVo;

public interface SysOperLogService {

    PageVo<SysOperLog> findPage(Long page, Long limit, SysOperLogQuery sysOperLogQuery);

    /**
     * 保存系统日志记录
     */
    void saveSysLog(SysOperLog sysOperLog);

    SysOperLog getById(Long id);
}
