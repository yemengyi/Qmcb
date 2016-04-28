package com.gongdian.qmcb.model;

import java.io.Serializable;

/**
 * Created by qian-pc on 4/19/16.
 */
public class QmcbUsers implements Serializable{

    /**
     * username : 13951499090
     * password : 123456
     * pwd : gycsi
     * role : 5
     * lognum : 0
     * lastlog : null
     * sjh : 13951499090
     * yxbz : 1
     * yh : null
     * wd : null
     * mc : 局信息中心
     * owner : null
     * fzr : 钱锋
     * imei : 867686020812564
     * imsi : null
     */

    private String username;
    private String password;
    private String pwd;
    private int role;
    private int lognum;
    private String sjh;
    private int yxbz;
    private String yh;
    private String wd;
    private String mc;
    private String owner;
    private String fzr;
    private String imei;
    private String imsi;
    private String headurl;
    private String xb;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getLognum() {
        return lognum;
    }

    public void setLognum(int lognum) {
        this.lognum = lognum;
    }

    public String getSjh() {
        return sjh;
    }

    public void setSjh(String sjh) {
        this.sjh = sjh;
    }

    public int getYxbz() {
        return yxbz;
    }

    public void setYxbz(int yxbz) {
        this.yxbz = yxbz;
    }

    public String getYh() {
        return yh;
    }

    public void setYh(String yh) {
        this.yh = yh;
    }

    public String getWd() {
        return wd;
    }

    public void setWd(String wd) {
        this.wd = wd;
    }

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFzr() {
        return fzr;
    }

    public void setFzr(String fzr) {
        this.fzr = fzr;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }

    public String getXb() {
        return xb;
    }

    public void setXb(String xb) {
        this.xb = xb;
    }
}
