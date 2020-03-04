package com.relsol.demo.entity;

import java.io.Serializable;

public class ProcessManage implements Serializable {

    private int id;

    private String processType;

    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ProcessManage{" +
                "id=" + id +
                ", processType='" + processType + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
