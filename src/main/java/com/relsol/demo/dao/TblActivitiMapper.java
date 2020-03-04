package com.relsol.demo.dao;

import com.relsol.demo.entity.ActivitiVariable;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TblActivitiMapper {

    public ActivitiVariable select(ActivitiVariable activitiVariable);

    public List<ActivitiVariable> selectList(ActivitiVariable activitiVariable);

    public int insertData(ActivitiVariable activitiVariable);

    public int update(ActivitiVariable activitiVariable);

    public int delete(Integer id);
}
