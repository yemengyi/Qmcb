package com.gongdian.qmcb.model;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

import java.io.Serializable;

/**
 * Created by qian-pc on 1/23/16.
 */
@Table(name = "project_jd")
public class Project_jd implements Serializable{
    @Id
    @Column(name = "_id")
    public int _id;
    @Column(name = "pro_id")
    private String pro_id;
    @Column(name = "menu_id")
    private String menu_id;
    @Column(name = "nr")
    private String nr;
    @Column(name = "user_id")
    private String user_id;
    @Column(name = "createtime")
    private String createtime;
    @Column(name = "yxbz")
    private String yxbz;
    @Column(name = "pid")
    private String pid;
    @Column(name = "lb")
    private String lb;
    @Column(name = "dwf")
    private String dwf;
    @Column(name = "dwq")
    private String dwq;
    @Column(name = "ryf")
    private String ryf;
    @Column(name = "ryq")
    private String ryq;
    private String sfgs;


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getPro_id() {
        return pro_id;
    }

    public void setPro_id(String pro_id) {
        this.pro_id = pro_id;
    }

    public String getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getYxbz() {
        return yxbz;
    }

    public void setYxbz(String yxbz) {
        this.yxbz = yxbz;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getLb() {
        return lb;
    }

    public void setLb(String lb) {
        this.lb = lb;
    }

    public String getDwf() {
        return dwf;
    }

    public void setDwf(String dwf) {
        this.dwf = dwf;
    }

    public String getDwq() {
        return dwq;
    }

    public void setDwq(String dwq) {
        this.dwq = dwq;
    }

    public String getRyf() {
        return ryf;
    }

    public void setRyf(String ryf) {
        this.ryf = ryf;
    }

    public String getRyq() {
        return ryq;
    }

    public void setRyq(String ryq) {
        this.ryq = ryq;
    }

    public String getSfgs() {
        return sfgs;
    }

    public void setSfgs(String sfgs) {
        this.sfgs = sfgs;
    }
}
