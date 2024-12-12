package com.gec.anan.mgr.service;

import com.gec.anan.model.entity.system.SysDept;

import java.util.List;

public interface SysDeptService {

    List<SysDept> findNodes();

    List<SysDept> findUserNodes();

    void updateStatus(Long id, Integer status);

    SysDept getById(Long id);

    void save(SysDept sysDept);

    void update(SysDept sysDept);

    void remove(Long id);
}
