package com.gongdian.qmcb.others;

import com.ab.util.AbLogUtil;

/**
 * Created by qian-pc on 4/6/16.
 */
public class IMSInfo {
    public String chipName;
    public String imsi_1;
    public String imei_1;
    public String imsi_2;
    public String imei_2;

    public String getChipName() {
        return chipName;
    }

    public void tolog(){
        AbLogUtil.e("xxxxx","chipName" + chipName);
        AbLogUtil.e("xxxxx","imsi_1" + imsi_1);
        AbLogUtil.e("xxxxx","imei_1" + imei_1);
        AbLogUtil.e("xxxxx","imsi_2" + imsi_2);
        AbLogUtil.e("xxxxx","imei_2" + imei_2);
    }

    public void setChipName(String chipName) {
        this.chipName = chipName;
    }

    public String getImsi_1() {
        return imsi_1;
    }

    public void setImsi_1(String imsi_1) {
        this.imsi_1 = imsi_1;
    }

    public String getImei_1() {
        return imei_1;
    }

    public void setImei_1(String imei_1) {
        this.imei_1 = imei_1;
    }

    public String getImsi_2() {
        return imsi_2;
    }

    public void setImsi_2(String imsi_2) {
        this.imsi_2 = imsi_2;
    }

    public String getImei_2() {
        return imei_2;
    }

    public void setImei_2(String imei_2) {
        this.imei_2 = imei_2;
    }
}
