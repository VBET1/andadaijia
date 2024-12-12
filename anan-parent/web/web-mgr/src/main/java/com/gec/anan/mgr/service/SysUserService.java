package com.gec.anan.mgr.service;


import com.gec.anan.model.entity.system.SysUser;
import com.gec.anan.model.query.system.SysUserQuery;
import com.gec.anan.model.vo.base.PageVo;

public interface SysUserService {

    SysUser getById(Long id);

    void save(SysUser sysUser);

    void update(SysUser sysUser);

    void remove(Long id);

    PageVo<SysUser> findPage(Long page, Long limit, SysUserQuery sysUserQuery);

    void updateStatus(Long id, Integer status);


}
