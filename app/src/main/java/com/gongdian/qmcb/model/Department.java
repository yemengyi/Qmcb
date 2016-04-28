package com.gongdian.qmcb.model;

import java.io.Serializable;

/**
 * Created by qian-pc on 12/30/15.
 */
public class Department implements Serializable {

    /**
     * pid : 1000
     * pname : 测试单位1
     */

    private String pid;
    private String pname;
    private String dz;
    private boolean choose = false;
    private String fzr;
    private String fzrxm;

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPid() {
        return pid;
    }

    public String getPname() {
        return pname;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }

    public boolean isChoose() {
        return choose;
    }

    public String getDz() {
        return dz;
    }

    public void setDz(String dz) {
        this.dz = dz;
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
