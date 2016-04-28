package com.gongdian.qmcb.model;

import java.io.Serializable;

/**
 * Created by qian-pc on 4/23/16.
 */
public class Yxc implements Serializable{
    private String zcbm;
    private String dwmc;
    private Boolean choose;
    private String latitude;
    private String lontitude;
    private String address;
    private String locationdescribe;
    private String url;
    private String qdsj;

    public String getZcbm() {
        return zcbm;
    }

    public void setZcbm(String zcbm) {
        this.zcbm = zcbm;
    }

    public String getDwmc() {
        return dwmc;
    }

    public void setDwmc(String dwmc) {
        this.dwmc = dwmc;
    }

    public Boolean getChoose() {
        return choose;
    }

    public void setChoose(Boolean choose) {
        this.choose = choose;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLontitude() {
        return lontitude;
    }

    public void setLontitude(String lontitude) {
        this.lontitude = lontitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocationdescribe() {
        return locationdescribe;
    }

    public void setLocationdescribe(String locationdescribe) {
        this.locationdescribe = locationdescribe;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQdsj() {
        return qdsj;
    }

    public void setQdsj(String qdsj) {
        this.qdsj = qdsj;
    }
}
