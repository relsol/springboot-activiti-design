package com.relsol.demo.dao;

import com.relsol.demo.entity.ProcessManage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProcessManageMapper {

    public List<ProcessManage> selectList(ProcessManage processManage);

    public int insert(ProcessManage processManage);

    public int delete(int id);

    public int update(ProcessManage processManage);
}
