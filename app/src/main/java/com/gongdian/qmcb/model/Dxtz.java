package com.gongdian.qmcb.model;

/**
 * Created by qian-pc on 3/14/16.
 */
public class Dxtz {

    /**
     * id : 10012
     * user_id : 100
     * hm : 13951499090
     * menu_id : 203
     * dxnr : 钱锋您好，工作(10012)刚刚个，于2016-03-12 22:54至2016-03-16 00:23，由安全监察质量部(钱锋)在刚刚进行施工，请您安排做好许可开(完)工工作。
     * pid : 1001
     * pname : 安全监察质量部
     */

    private String id;
    private String user_id;
    private String hm;
    private String menu_id;
    private String dxnr;
    private String pid;
    private String pname;
    private String xm;
    private boolean choose = true;

    public void setId(String id) {
        this.id = id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setHm(String hm) {
        this.hm = hm;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    public void setDxnr(String dxnr) {
        this.dxnr = dxnr;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getHm() {
        return hm;
    }

    public String getMenu_id() {
        return menu_id;
    }

    public String getDxnr() {
        return dxnr;
    }

    public String getPid() {
        return pid;
    }

    public String getPname() {
        return pname;
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }
}
