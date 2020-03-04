package com.relsol.demo.entity;

import java.io.Serializable;

public class ActivitiVariable implements Serializable {

    private int id;

    private String applyStr;

    private String approveStr;

    @Override
    public String toString() {
        return "ActivitiVariable{" +
                "id=" + id +
                ", applyStr='" + applyStr + '\'' +
                ", approveStr='" + approveStr + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApplyStr() {
        return applyStr;
    }

    public void setApplyStr(String applyStr) {
        this.applyStr = applyStr;
    }

    public String getApproveStr() {
        return approveStr;
    }

    public void setApproveStr(String approveStr) {
        this.approveStr = approveStr;
    }
}
