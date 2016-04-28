package com.gongdian.qmcb.model;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

import java.io.Serializable;

/**
 * Created by qian-pc on 1/18/16.
 */
@Table(name = "project_ry")
public class Project_ry implements Serializable{
    @Id
    @Column(name = "_id")
    public int _id;
    @Column(name = "uid")
    public String user_id;
    @Column(name = "pro_id")
    public String id;
    @Column(name = "username")
    public String username;
    public String bz;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }
}
