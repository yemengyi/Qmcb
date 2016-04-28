package com.gongdian.qmcb.model;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

import java.io.Serializable;

/**
 * Created by qian-pc on 1/18/16.
 */
@Table(name = "project_dw")
public class Project_dw implements Serializable{
    @Id
    @Column(name = "_id")
    public int _id;
    @Column(name = "pid")
    public String pid;
    @Column(name = "pro_id")
    public String id;
    @Column(name = "dz")
    public String dz;
    @Column(name = "fzr")
    public String fzr;
    @Column(name = "fzrxm")
    public String fzrxm;
    @Column(name = "pname")
    public String pname;
    @Column(name = "flag")
    public String flag;
    public String latitude;
    public String lontitude;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
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
