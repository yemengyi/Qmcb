package com.gongdian.qmcb.model;

/**
 * Created by qian-pc on 3/6/16.
 */
public class Cf1 {

    /**
     * pid : 1001
     * dwf : 20
     * dwq : 10
     * pname : 安全监察质量部
     */

    private String pid;
    private int dwf;
    private int dwq;
    private String pname;

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setDwf(int dwf) {
        this.dwf = dwf;
    }

    public void setDwq(int dwq) {
        this.dwq = dwq;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPid() {
        return pid;
    }

    public int getDwf() {
        return dwf;
    }

    public int getDwq() {
        return dwq;
    }

    public String getPname() {
        return pname;
    }
    public String getTitle() {
        return pname;
    }
    public String getFen() {
        return String.valueOf(dwf) + "分";
    }
    public String getQian() {
        return String.valueOf(dwq) + "元";
    }
}
