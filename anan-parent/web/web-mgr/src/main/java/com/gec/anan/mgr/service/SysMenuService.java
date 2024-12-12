package com.gec.anan.mgr.service;


import com.gec.anan.model.entity.system.SysMenu;
import com.gec.anan.model.vo.system.AssginMenuVo;

import java.util.List;

public interface SysMenuService {

    void save(SysMenu sysMenu);

    void update(SysMenu sysMenu);

    void remove(Long id);

    /**
     * 菜单树形数据
     * @return
     */
    List<SysMenu> findNodes();


    /**
     * 保存角色权限
     * @param  assginMenuVo
     */
    void doAssign(AssginMenuVo assginMenuVo);


    List<SysMenu> toAssign(Long roleId);
}
