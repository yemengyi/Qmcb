package com.gongdian.qmcb.model;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

import java.io.Serializable;
/**
 * Created by qian-pc on 1/23/16.
 */
@Table(name = "project_photo")
public class Project_photo implements Serializable{
    @Id
    @Column(name = "_id")
    public int _id;
    @Column(name = "id")
    public String id;
    @Column(name = "pro_id")
    public String pro_id;
    @Column(name = "jd_id")
    public String jd_id;
    @Column(name = "url")
    public String url;
    @Column(name = "createuser")
    public String createuser;
    @Column(name = "createtime")
    public String createtime;
    @Column(name = "flag")
    public String flag;
    @Column(name = "latitude")
    public String latitude;
    @Column(name = "lontitude")
    public String lontitude;
    @Column(name = "address")
    public String address;
    @Column(name = "locationdescribe")
    public String locationdescribe;

    public String menu;
    public String uname;

    public String getInfo2(){
        return menu + "\n" + uname + "  " + createtime ;
    }

    public String getInfo(){
        return  "时间: " + createtime + "\n" + "地点: "+ address + "("+locationdescribe+")" ;
    }

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

    public String getPro_id() {
        return pro_id;
    }

    public void setPro_id(String pro_id) {
        this.pro_id = pro_id;
    }

    public String getJd_id() {
        return jd_id;
    }

    public void setJd_id(String jd_id) {
        this.jd_id = jd_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreateuser() {
        return createuser;
    }

    public void setCreateuser(String createuser) {
        this.createuser = createuser;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
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

    public String getLocationdescribe() {
        return locationdescribe;
    }

    public void setLocationdescribe(String locationdescribe) {
        this.locationdescribe = locationdescribe;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }
}
