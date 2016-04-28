package com.gongdian.qmcb.model;

import java.io.Serializable;

/**
 * Created by qian-pc on 12/30/15.
 */
public class Menu implements Serializable{

    /**
     * uids : 320721198205050011
     * id : 102
     * menu : 权限管理
     * flag : 1
     */

    private String uids;
    private String id;
    private String menu;
    private String flag;
    private int task;

    public void setUids(String uids) {
        this.uids = uids;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getUids() {
        return uids;
    }

    public String getId() {
        return id;
    }

    public String getMenu() {
        return menu;
    }

    public String getFlag() {
        return flag;
    }

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }
}
