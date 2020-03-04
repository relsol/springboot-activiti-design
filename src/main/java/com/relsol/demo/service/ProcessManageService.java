package com.relsol.demo.service;

import com.relsol.demo.dao.ProcessManageMapper;
import com.relsol.demo.entity.ProcessManage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ProcessManageService {

    @Resource
    private ProcessManageMapper processManageMapper;

    public List<ProcessManage> selectList(ProcessManage processManage) {
        return processManageMapper.selectList(processManage);
    }

    public int insert(ProcessManage processManage) {
        return processManageMapper.insert(processManage);
    }

    public int delete(int id) {
        return processManageMapper.delete(id);
    }

    public int update(ProcessManage processManage) {
        return processManageMapper.update(processManage);
    }

}
