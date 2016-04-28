package com.gongdian.qmcb.model;

/**
 * Created by qian-pc on 3/6/16.
 */
public class Cf2 {

    /**
     * fzr : 100
     * fzrxm : 钱锋
     * ryf : 10
     * ryq : 10
     */

    private String fzr;
    private String fzrxm;
    private String pname;
    private int ryf;
    private int ryq;

    public void setFzr(String fzr) {
        this.fzr = fzr;
    }

    public void setFzrxm(String fzrxm) {
        this.fzrxm = fzrxm;
    }

    public void setRyf(int ryf) {
        this.ryf = ryf;
    }

    public String getFzr() {
        return fzr;
    }

    public void setRyq(int ryq) {
        this.ryq = ryq;
    }

    public String getFzrxm() {
        return fzrxm;
    }

    public int getRyf() {
        return ryf;
    }

    public int getRyq() {
        return ryq;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getTitle() {
        return pname + " "+fzrxm;
    }
    public String getQian() {
        return String.valueOf(ryq) + "分";
    }
    public String getFen() {
        return String.valueOf(ryf) + "元";
    }
}
