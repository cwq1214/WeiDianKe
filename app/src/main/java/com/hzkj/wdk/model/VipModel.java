package com.hzkj.wdk.model;

import java.io.Serializable;

/**
 * Created by howie on 16/8/29.
 */
public class VipModel implements Serializable {
    private String value;
    private String value_des;
    private String function_des;
    private String id;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue_des() {
        return value_des;
    }

    public void setValue_des(String value_des) {
        this.value_des = value_des;
    }

    public String getFunction_des() {
        return function_des;
    }

    public void setFunction_des(String function_des) {
        this.function_des = function_des;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
