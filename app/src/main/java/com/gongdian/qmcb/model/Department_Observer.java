package com.gongdian.qmcb.model;

import java.io.Serializable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by qian-pc on 12/30/15.
 */
public class Department_Observer extends Observable implements Observer,Serializable {

    /**
     * pid : 1000
     * pname : 测试单位1
     */

    private String pid;
    private String pname;
    private String dz;
    private boolean choose = false;
    private List<Users_Observer> users ;
    private boolean isChecked;
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

    public List<Users_Observer> getUsers() {
        return users;
    }

    public void setUsers(List<Users_Observer> users) {
        this.users = users;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public void changeChecked(){
        isChecked = !isChecked;
        setChanged();
        notifyObservers(isChecked);
    }

    @Override
    public void update(Observable observable, Object data) {
        boolean flag = true;
        for (Users_Observer user : users) {
            if (user.isChecked() == false) {
                flag = false;
            }
        }
        this.isChecked = flag;
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
