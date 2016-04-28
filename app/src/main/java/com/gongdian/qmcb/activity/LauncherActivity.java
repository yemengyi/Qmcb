package com.gongdian.qmcb.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.model.AbResult;
import com.ab.soap.AbSoapParams;
import com.ab.soap.AbSoapUtil;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbStrUtil;
import com.ab.util.AbToastUtil;
import com.ab.util.AbViewUtil;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.model.QmcbUsers;
import com.gongdian.qmcb.model.QmcbUsersListResult;
import com.gongdian.qmcb.others.IMSInfo;
import com.gongdian.qmcb.utils.AppUtil;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.ImsiUtil;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.WebServiceUntils;

import java.util.List;

public class LauncherActivity extends AbActivity {
    private LinearLayout launcherView;
    private Animation mFadeIn;
    private Animation mFadeInScale;
    private AbSoapUtil mAbSoapUtil = null;
    private MyApplication application;
    private String imei;
    private String imsi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        launcherView = (LinearLayout) this.findViewById(R.id.launcherView);
        AbViewUtil.scaleContentView(launcherView);
        application = (MyApplication)abApplication;
        TextView version = (TextView) findViewById(R.id.version);

//        PackageInfo packageInfo = AbAppUtil.getPackageInfo(this);
        version.setText("V 1.0");
        application.setVersion("1.0");

        mAbSoapUtil = AbSoapUtil.getInstance(this);
        mAbSoapUtil.setTimeout(15000);


        ImsiUtil imsiUtil = new ImsiUtil(this);
        IMSInfo imsInfo = imsiUtil.getIMSInfo();
        imei = imsInfo.getImei_1();

        imsi = imsInfo.getImsi_1();

//        imei = "861141032670648";

        imsInfo.tolog();

        String deviceId = ((TelephonyManager)getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        AbLogUtil.e("xxxx",deviceId);
//        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_ID = tm.getDeviceId();

        AbLogUtil.e("xxxx", DEVICE_ID);
        init();
        setListener();


    }

    private void setListener() {

        mFadeIn.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                launcherView.startAnimation(mFadeInScale);
            }
        });

        mFadeInScale.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (!AppUtil.isConn(getApplicationContext())) {
                    AppUtil.setNetworkMethod(LauncherActivity.this);
                } else {
                    gotoMain();
                }

            }

        });

    }

    private void init() {
        initAnim();
        launcherView.startAnimation(mFadeIn);
    }

    private void initAnim() {
        mFadeIn = AnimationUtils.loadAnimation(LauncherActivity.this,
                R.anim.welcome_fade_in);
        mFadeIn.setDuration(800);
        mFadeIn.setFillAfter(true);

        mFadeInScale = AnimationUtils.loadAnimation(LauncherActivity.this,
                R.anim.welcome_fade_in_scale);
        mFadeInScale.setDuration(2000);
        mFadeInScale.setFillAfter(true);
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        gotoMain();
//    }

    public void gotoMain() {


        if (!AppUtil.isConn(getApplicationContext())) {
            AbToastUtil.showToast(LauncherActivity.this, "检测网络不通,程序已关闭！");
            finish();
        } else {
            if (Constant.DEBUG) {
            }else {
                CheckImei();
            }

        }
    }

    public void CheckImei() {
        AbSoapParams params = new AbSoapParams();
        params.put("imei", imei);
        params.put("imsi", imsi);

        WebServiceUntils.call(LauncherActivity.this, Constant.CheckIMEI, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result1) {

                if (aBoolean && !AbStrUtil.isEquals(result1, "0")) {
                    AbResult result = new AbResult(result1);
                    if (result.getResultCode() > 0) {
                        //成功
                        QmcbUsersListResult UsersListResult = AbJsonUtil.fromJson(result1, QmcbUsersListResult.class);
                        List<QmcbUsers> UserssList = UsersListResult.getItems();
                        if (UserssList != null && UserssList.size() > 0) {
                            QmcbUsers users = UserssList.get(0);
                            AbToastUtil.showToast(LauncherActivity.this, "欢迎您" + users.getFzr());
                            application.setUser(users);
                            application.setIsLogin(true);
                            application.setToken(imei + Constant.APPVERSION);
                            AppUtil.start_Activity(LauncherActivity.this, MainActivity.class);
                            finish();
                        } else {
                            new AlertView("提示", "该手机或手机号码未通过验证!", null, new String[]{"确定"}, null, LauncherActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                                @Override
                                public void onItemClick(Object o, int position) {
                                    finish();
                                }
                            }).show();
                        }
                    }
                } else {
                    new AlertView("提示", "该手机或手机号码未通过验证!", null, new String[]{"确定"}, null, LauncherActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            finish();
                        }
                    }).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            if (!AppUtil.isConn(getApplicationContext())) {
                AppUtil.setNetworkMethod(LauncherActivity.this);
            } else {
                gotoMain();
            }
        }
    }


}
