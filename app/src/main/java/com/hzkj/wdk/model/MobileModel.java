package com.hzkj.wdk.model;

/**
 * Created by howie on 16/6/22.
 */
public class MobileModel {
    private String rowid;
    private String id;
    private String MobileNumber;
    private String MobileArea;
    private String MobileType;
    private String AreaCode;
    private String PostCode;

    public String getRowid() {
        return rowid;
    }

    public void setRowid(String rowid) {
        this.rowid = rowid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getMobileArea() {
        return MobileArea;
    }

    public void setMobileArea(String mobileArea) {
        MobileArea = mobileArea;
    }

    public String getMobileType() {
        return MobileType;
    }

    public void setMobileType(String mobileType) {
        MobileType = mobileType;
    }

    public String getAreaCode() {
        return AreaCode;
    }

    public void setAreaCode(String areaCode) {
        AreaCode = areaCode;
    }

    public String getPostCode() {
        return PostCode;
    }

    public void setPostCode(String postCode) {
        PostCode = postCode;
    }
}
