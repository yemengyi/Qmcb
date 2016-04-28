package com.gongdian.qmcb.model;

import com.ab.util.AbStrUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qian-pc on 1/17/16.
 */
public class Project2 implements Serializable{

    /**
     * id : 1001
     * yf : 201601
     * nr : 线路抢修冲
     * kssj : 2016-01-18
     * jssj : 2016-01-19
     * dz : 城西部分
     * flag : 1
     */
    private String id;
    private String yf;
    private String mc;
    private String nr;
    private String kssj;
    private String jssj;
    private String dz;
    private String flag;
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
    private List<Project_dw2> project_dw;
    private List<Project_ry> project_ry;
    private List<Project_jd2> project_jd;
    private String xk_pid;
    private String xkdw;



    public String getSj(){
        String s = kssj.substring(kssj.indexOf("-") + 1) + " ~ " + jssj.substring(jssj.indexOf("-") + 1);
        return s;
    }
    public String getTitle(){
        String s;
        s = id + " " + mc  + "\n" +  getSj() + "\n    " + getNr();
        return s;
    }

    public int getSPnumber() {
        int rtn=0;
        if (project_jd!=null) {
            for (int i = 0;i<project_jd.size();i++) {
                if (AbStrUtil.isEquals(project_jd.get(i).getYxbz(),"0")) {
                    rtn++;
                }
            }
        }
        return rtn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYf() {
        return yf;
    }

    public void setYf(String yf) {
        this.yf = yf;
    }

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public String getKssj() {
        return kssj;
    }

    public void setKssj(String kssj) {
        this.kssj = kssj;
    }

    public String getJssj() {
        return jssj;
    }

    public void setJssj(String jssj) {
        this.jssj = jssj;
    }

    public String getDz() {
        return dz;
    }

    public void setDz(String dz) {
        this.dz = dz;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
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

    public String getCreateusername() {
        return createusername;
    }

    public void setCreateusername(String createusername) {
        this.createusername = createusername;
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

    public void setDw(String dw) {
        this.dw = dw;
    }

    public String getRy() {
        return ry;
    }

    public void setRy(String ry) {
        this.ry = ry;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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

    public List<Project_dw2> getProject_dw() {
        return project_dw;
    }

    public void setProject_dw(List<Project_dw2> project_dw) {
        this.project_dw = project_dw;
    }

    public List<Project_ry> getProject_ry() {
        return project_ry;
    }

    public void setProject_ry(List<Project_ry> project_ry) {
        this.project_ry = project_ry;
    }

    public List<Project_jd2> getProject_jd() {
        return project_jd;
    }

    public void setProject_jd(List<Project_jd2> project_jd) {
        this.project_jd = project_jd;
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
