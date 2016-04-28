package com.gongdian.qmcb.model;

import java.io.Serializable;

/**
 * Created by qian-pc on 12/19/15.
 */
public class Users implements Serializable{
    private String id;
    private String uids;
    private String uname;
    private String urole;
    private String pid;
    private String pcode;
    private String imei;
    private String pname;
    private String version;
    private String headurl;
    private boolean choose = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUids(String uids) {
        this.uids = uids;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public void setUrole(String urole) {
        this.urole = urole;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUids() {
        return uids;
    }

    public String getUname() {
        return uname;
    }

    public String getUrole() {
        return urole;
    }

    public String getPid() {
        return pid;
    }

    public String getPcode() {
        return pcode;
    }

    public String getImei() {
        return imei;
    }

    public String getPname() {
        return pname;
    }

    public String getVersion() {
        return version;
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }
}
