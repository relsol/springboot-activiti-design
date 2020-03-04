package com.relsol.demo.service;

import com.relsol.demo.dao.TblActivitiMapper;
import com.relsol.demo.entity.ActivitiVariable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ActivitiVariableService {

    @Resource
    private TblActivitiMapper tblActivitiMapper;

    public ActivitiVariable select(ActivitiVariable activitiVariable) {
        return tblActivitiMapper.select(activitiVariable);
    }

    public List<ActivitiVariable> selectList(ActivitiVariable activitiVariable) {
        return tblActivitiMapper.selectList(activitiVariable);
    }

    public int insert(ActivitiVariable activitiVariable) {
        return tblActivitiMapper.insertData(activitiVariable);
    }

    public int update(ActivitiVariable activitiVariable) {
        return tblActivitiMapper.update(activitiVariable);
    }

    public int delete(Integer id) {
        return tblActivitiMapper.delete(id);
    }
}
