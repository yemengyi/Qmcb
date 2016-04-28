package com.gongdian.qmcb.model;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Relations;
import com.ab.db.orm.annotation.Table;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qian-pc on 1/17/16.
 */
@Table(name = "project")
public class Project implements Serializable {

    /**
     * id : 1001
     * yf : 201601
     * nr : 线路抢修冲
     * kssj : 2016-01-18
     * jssj : 2016-01-19
     * dz : 城西部分
     * flag : 1
     */
    @Id
    @Column(name = "pro_id")
    private String id;
    @Column(name = "yf")
    private String yf;
    @Column(name = "mc")
    private String mc;
    @Column(name = "nr")
    private String nr;
    @Column(name = "kssj")
    private String kssj;
    @Column(name = "jssj")
    private String jssj;
    @Column(name = "dz")
    private String dz;
    @Column(name = "flag")
    private String flag;
    @Column(name = "xk_pid")
    private String xk_pid;

    private String createtime;
    private String createuser;
    private String createusername;
    private String flag_jd;
    private String dw;
    private String ry;
    private String pid;
    private String latitude;
    private String lontitude;
    private String address;
    private String fzr;
    private String fzrxm;
    private String xkdw;


    @Relations(name = "project_dw", type = "one2many", foreignKey = "pro_id", action = "query_insert_delete")
    private List<Project_dw> project_dw;
    @Relations(name = "project_ry", type = "one2many", foreignKey = "pro_id", action = "query_insert_delete")
    private List<Project_ry> project_ry;
    @Relations(name = "project_jd", type = "one2many", foreignKey = "pro_id", action = "query_insert_delete")
    private List<Project_jd> project_jd;
    @Relations(name = "project_photo", type = "one2many", foreignKey = "pro_id", action = "query_insert_delete")
    private List<Project_photo> project_photos;
    private List<Project_menu> project_menu;

    public int getMinMenu_id() {
        int t = 300;
        if (project_menu != null && project_menu.size() > 0) {
            for (int i=0;i<project_menu.size();i++) {
                int n = Integer.parseInt(project_menu.get(i).getMenu_id());
                if (n<t) {
                    t = n;
                }
            }
        }
        return t;
    }

    public String getFzrs() {
        String t = "";

        if (project_dw != null) {
            for (int i = 0; i < project_dw.size(); i++) {
                if (i == project_dw.size() - 1) {
                    t += project_dw.get(i).getFzrxm();
                } else {
                    t += project_dw.get(i).getFzrxm() + "、";
                }
            }
        }
        return t;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setYf(String yf) {
        this.yf = yf;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public void setKssj(String kssj) {
        this.kssj = kssj;
    }

    public void setJssj(String jssj) {
        this.jssj = jssj;
    }

    public void setDz(String dz) {
        this.dz = dz;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public String getYf() {
        return yf;
    }

    public String getNr() {
        return nr;
    }

    public String getKssj() {
        return kssj;
    }

    public String getJssj() {
        return jssj;
    }

    public String getDz() {
        return dz;
    }

    public String getFlag() {
        return flag;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getCreateuser() {
        return createuser;
    }

    public void setCreateuser(String createuser) {
        this.createuser = createuser;
    }

    public List<Project_dw> getProject_dw() {
        return project_dw;
    }

    public void setProject_dw(List<Project_dw> project_dw) {
        this.project_dw = project_dw;
    }

    public List<Project_ry> getProject_ry() {
        return project_ry;
    }

    public void setProject_ry(List<Project_ry> project_ry) {
        this.project_ry = project_ry;
    }

    public List<Project_jd> getProject_jd() {
        return project_jd;
    }

    public void setProject_jd(List<Project_jd> project_jd) {
        this.project_jd = project_jd;
    }

    public String getFlag_jd() {
        return flag_jd;
    }

    public void setFlag_jd(String flag_jd) {
        this.flag_jd = flag_jd;
    }

    public String getDw() {
        return dw;
    }

    public String getCreateusername() {
        return createusername;
    }

    public void setCreateusername(String createusername) {
        this.createusername = createusername;
    }

    public void setDw(String dw) {
        this.dw = dw;
    }

    public String getRy() {
        return ry;
    }

    public void setRy(String ry) {
        this.ry = ry;
    }

    public List<Project_photo> getProject_photos() {
        return project_photos;
    }

    public void setProject_photos(List<Project_photo> project_photos) {
        this.project_photos = project_photos;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public List<Project_menu> getProject_menu() {
        return project_menu;
    }

    public void setProject_menu(List<Project_menu> project_menu) {
        this.project_menu = project_menu;
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

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public String getTitle() {
        String s;
        s = id + " " + mc + "\n" + kssj + " - " + jssj;
        return s;
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

    public String getXk_pid() {
        return xk_pid;
    }

    public void setXk_pid(String xk_pid) {
        this.xk_pid = xk_pid;
    }

    public String getXkdw() {
        return xkdw;
    }

    public void setXkdw(String xkdw) {
        this.xkdw = xkdw;
    }
}
