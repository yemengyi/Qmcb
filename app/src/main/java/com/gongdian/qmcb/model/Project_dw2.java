package com.gongdian.qmcb.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qian-pc on 1/18/16.
 */
public class Project_dw2 implements Serializable{

    public String id;
    public String pid;
    public String dz;
    public String fzr;
    public String fzrxm;
    public String pname;
    public String flag;
    public String latitude;
    public String lontitude;

    private List<Project_jd2> project_jd;

    public String getTitle(){
        return pname + "(" + fzrxm + ")"+ " 于 " + dz + " 施工";

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDz() {
        return dz;
    }

    public void setDz(String dz) {
        this.dz = dz;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
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

    public List<Project_jd2> getProject_jd() {
        return project_jd;
    }

    public void setProject_jd(List<Project_jd2> project_jd) {
        this.project_jd = project_jd;
    }

    public String getFzr() {
        return fzr;
    }

    public void setFzr(String fzr) {
        this.fzr = fzr;
    }

    public String getFzrxm() {
        return fzrxm;
    }

    public void setFzrxm(String fzrxm) {
        this.fzrxm = fzrxm;
    }
}
