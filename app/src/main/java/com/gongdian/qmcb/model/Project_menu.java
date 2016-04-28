package com.gongdian.qmcb.model;

import java.io.Serializable;

/**
 * Created by qian-pc on 2/18/16.
 */
public class Project_menu implements Serializable {

    /**
     * pro_id : 10003
     * menu_id : 204
     * menu : 到岗到位
     * menu_bm : 到岗
     */

    private String pro_id;
    private String menu_id;
    private String menu;
    private String menu_bm;

    public void setPro_id(String pro_id) {
        this.pro_id = pro_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public void setMenu_bm(String menu_bm) {
        this.menu_bm = menu_bm;
    }

    public String getPro_id() {
        return pro_id;
    }

    public String getMenu_id() {
        return menu_id;
    }

    public String getMenu() {
        return menu;
    }

    public String getMenu_bm() {
        return menu_bm;
    }
}
