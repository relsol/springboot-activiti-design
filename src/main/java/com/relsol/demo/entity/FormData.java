package com.relsol.demo.entity;

import java.io.Serializable;

public class FormData implements Serializable {

    private String id;

    private String userId;

    private String attr1;

    private String attr2;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAttr1() {
        return attr1;
    }

    public void setAttr1(String attr1) {
        this.attr1 = attr1;
    }

    public String getAttr2() {
        return attr2;
    }

    public void setAttr2(String attr2) {
        this.attr2 = attr2;
    }

    @Override
    public String toString() {
        return "FormData{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", attr1='" + attr1 + '\'' +
                ", attr2='" + attr2 + '\'' +
                '}';
    }
}
