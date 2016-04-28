package com.gongdian.qmcb.utils;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import com.ab.global.AbAppConfig;
import com.ab.util.AbStrUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.gongdian.qmcb.model.Location;
import com.gongdian.qmcb.model.Menu;
import com.gongdian.qmcb.model.QmcbUsers;
import com.gongdian.qmcb.model.Users;
import com.pgyersdk.crash.PgyCrashManager;

import java.util.List;
import java.util.Map;

public class MyApplication extends Application {

    // 登录用户
    public String token;
    public boolean isLogin = false;
    public QmcbUsers user=null;
    public List<Menu>  menuList= null;
    public static Map<String, Long> map;
    public boolean userPasswordRemember = false;
    public boolean ad = false;
    public boolean isFirstStart = true;
    public SharedPreferences mSharedPreferences = null;
    public String sms;
    public  String phoneNumber;
    public String version;
    //任意位置获得context
    private static MyApplication instance;
    //百度map部分
    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;
    public TextView mLocationResult,logMsg;
    public TextView trigger,exit;
    public Vibrator mVibrator;
    public Location mLocation;
    public Boolean isAddProject=false;
    /**fragmentMenu 和 fragmentProfile 需要更新*/
    public Boolean userChanged1=false;
    public Boolean userChanged2=false;
    public Boolean userChanged3=false;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PgyCrashManager.register(this);
        SDKInitializer.initialize(this);
        instance = this;
        mSharedPreferences = getSharedPreferences(AbAppConfig.SHARED_PATH,
                Context.MODE_PRIVATE);
        //map
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        mLocation = new Location();


        initLoginParams();
    }

    /**
     * 上次登录参数
     */
    private void initLoginParams() {
        SharedPreferences preferences = getSharedPreferences(
                AbAppConfig.SHARED_PATH, Context.MODE_PRIVATE);
    }

    public void updateLoginParams(Users users) {
    }

    /**
     * 清空上次登录参数
     */
    public void clearLoginParams() {

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public QmcbUsers getUser() {
        return user;
    }

    public void setUser(QmcbUsers user) {
        this.user = user;
    }


    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public Boolean getIsAddProject() {
        return isAddProject;
    }

    public void setIsAddProject(Boolean isAddProject) {
        this.isAddProject = isAddProject;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
    }

    public Boolean getUserChanged1() {
        return userChanged1;
    }

    public void setUserChanged1(Boolean userChanged1) {
        this.userChanged1 = userChanged1;
    }

    public Boolean getUserChanged2() {
        return userChanged2;
    }

    public void setUserChanged2(Boolean userChanged2) {
        this.userChanged2 = userChanged2;
    }

    public Boolean getUserChanged3() {
        return userChanged3;
    }

    public void setUserChanged3(Boolean userChanged3) {
        this.userChanged3 = userChanged3;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * map实现实时位置回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("时间 : ");
            sb.append(location.getTime());
//            sb.append("\nerror code : ");
//            sb.append(location.getLocType());
//            sb.append("\nlatitude : ");
//            sb.append(location.getLatitude());
//            sb.append("\nlontitude : ");
//            sb.append(location.getLongitude());
//            sb.append("\nradius : ");
//            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
//                sb.append("\nspeed : ");
//                sb.append(location.getSpeed());// 单位：公里每小时
//                sb.append("\nsatellite : ");
//                sb.append(location.getSatelliteNumber());
//                sb.append("\nheight : ");
//                sb.append(location.getAltitude());// 单位：米
//                sb.append("\ndirection : ");
//                sb.append(location.getDirection());
                sb.append("\n描述 : ");
                sb.append("GPS定位成功");
//                sb.append("\n位置 : ");
//                sb.append(location.getAddrStr());
                mLocation.setDescribe("gps定位成功");
                mLocation.setSucess(true);

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                sb.append("\n描述 : ");
                sb.append("网络定位成功");
//                sb.append("\n位置 : ");
//                sb.append(location.getAddrStr());
                //运营商信息
//                sb.append("\noperationers : ");
//                sb.append(location.getOperators());
                mLocation.setDescribe("请稍后...");
                mLocation.setSucess(true);
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\n描述 : ");
                sb.append("离线定位成功");
                mLocation.setDescribe("离线定位成功");
                mLocation.setSucess(true);
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\n描述 : ");
                sb.append("服务端网络定位失败");
                mLocation.setDescribe("服务端网络定位失败");
                mLocation.setSucess(false);
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\n描述 : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
                mLocation.setDescribe("网络不同导致定位失败，请检查网络是否通畅");
                mLocation.setSucess(false);
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\n描述 : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                mLocation.setDescribe("无法获取有效定位依据导致定位失败");
                mLocation.setSucess(false);
            }
            sb.append("\n位置 : ");
            if (AbStrUtil.isEmpty(location.getAddrStr())) {
                sb.append("请稍后...");
            }else {
                sb.append(location.getAddrStr());
            }

            sb.append("\n提示 : ");// 位置语义化信息
            if (AbStrUtil.isEmpty(location.getLocationDescribe())) {
                sb.append("请稍后...");
            }else {
                sb.append(location.getLocationDescribe());
            }

//            List<Poi> list = location.getPoiList();// POI信息-兴趣点
//            if (list != null) {
//                sb.append("\npoilist size = : ");
//                sb.append(list.size());
//                for (Poi p : list) {
//                    sb.append("\npoi= : ");
//                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
//                }
//            }
            logMsg(sb.toString());
            Log.i("BaiduLocationApiDem", sb.toString());

            mLocation.setTime(location.getTime());
            mLocation.setError(location.getLocType());
            mLocation.setLatitude(location.getLatitude());
            mLocation.setLontitude(location.getLongitude());
            mLocation.setAddress(location.getAddrStr());
            mLocation.setLocationdescribe(location.getLocationDescribe());
        }
    }


    /**
     * map显示请求字符串
     * @param str
     */
    public void logMsg(String str) {
        try {
            if (mLocationResult != null)
                mLocationResult.setText(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}