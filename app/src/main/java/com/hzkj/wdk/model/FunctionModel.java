package com.hzkj.wdk.model;

import com.hzkj.wdk.fra.BaseFragment;

import java.io.Serializable;

/**
 * Created by howie on 16/8/29.
 */
public class FunctionModel implements Serializable {
    private String function_name;
    private String function_url;
    private String function_img;
    private boolean web=false;

    public String getFunction_name() {
        return function_name;
    }

    public void setFunction_name(String function_name) {
        this.function_name = function_name;
    }

    public String getFunction_url() {
        return function_url;
    }

    public void setFunction_url(String function_url) {
        this.function_url = function_url;
    }

    public String getFunction_img() {
        return function_img;
    }

    public void setFunction_img(String function_img) {
        this.function_img = function_img;
    }

    public boolean isWeb() {
        return web;
    }

    public void setWeb(boolean web) {
        this.web = web;
    }

}
