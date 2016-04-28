package com.gongdian.qmcb.model;

import com.ab.util.AbStrUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qian-pc on 1/23/16.
 */
public class Project_jd2 implements Serializable{

    public String id;
    private String pro_id;
    private String menu_id;
    private String nr;
    private String user_id;
    private String createtime;
    private String yxbz;
    private String pid;

    private String uname;
    private String menu;
    private String bm;
    private String sfgs;

    private String pname;


    private List<Project_photo> project_photo;

    public String getTitle(){
        return menu + "  " + createtime + "\n" +  pname + "-" + uname +  "\n" + "描述:"+ nr;
    }
    public String getAllAddress(){
        String s = " 拍摄位置:" + "\n";
        if (project_photo!=null) {
            for (int i=0;i<project_photo.size();i++) {
                String address;
                if (AbStrUtil.isEmpty(project_photo.get(i).getLocationdescribe())) {
                     address = project_photo.get(i).getAddress() ;
                }else {
                     address = project_photo.get(i).getAddress() + "(" + project_photo.get(i).getLocationdescribe() + ")";
                }

                if (i == project_photo.size() - 1) {
                    s = s + String.valueOf(i+1) + " - " + address;
                } else {
                    s = s + String.valueOf(i+1) + " - " + address + "\n";
                }
            }
        }

        return s;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<Project_photo> getProject_photo() {
        return project_photo;
    }

    public void setProject_photo(List<Project_photo> project_photo) {
        this.project_photo = project_photo;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getBm() {
        return bm;
    }

    public void setBm(String bm) {
        this.bm = bm;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getSfgs() {
        return sfgs;
    }

    public void setSfgs(String sfgs) {
        this.sfgs = sfgs;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }
}
