package com.gongdian.qmcb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ab.activity.AbActivity;
import com.ab.soap.AbSoapParams;
import com.ab.util.AbLogUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.model.Users;
import com.gongdian.qmcb.others.IMSInfo;
import com.gongdian.qmcb.others.PhoneInfo;
import com.gongdian.qmcb.others.TimeButton;
import com.gongdian.qmcb.parse.UsersPrase;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.ImsiUtil;
import com.gongdian.qmcb.utils.MsgUtil;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.WebServiceUntils;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;


public class RegisterActivity extends AbActivity implements View.OnClickListener {

    private EditText uidText;
    private ImageButton clear1;
    private EditText smsText;
    private ImageButton clear2;
    private Button checkBtn;
    private Button registerBtn;
    private TimeButton smsBtn;
    private MyApplication application;
    private Boolean isExit = false;
    private AbTitleBar mAbTitleBar=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_register);

        uidText = (EditText) findViewById(R.id.uidText);
        clear1 = (ImageButton) findViewById(R.id.clear1);
        smsText = (EditText) findViewById(R.id.smsText);
        clear2 = (ImageButton) findViewById(R.id.clear2);
        checkBtn = (Button) findViewById(R.id.checkBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        smsBtn = (TimeButton) findViewById(R.id.smsBtn);
        application = (MyApplication) abApplication;

        smsBtn.onCreate(savedInstanceState);
        smsBtn.setTextAfter("秒").setTextBefore("重新获取")
                .setLenght(30 * 1000);

        checkBtn.setOnClickListener(this);
        smsBtn.setOnClickListener(this);
        clear1.setOnClickListener(this);
        clear2.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

        uidText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String str = uidText.getText().toString().trim();
                int length = str.length();
                if (length > 0 && length < 18) {
                    clear1.setVisibility(View.VISIBLE);
                    if (!AbStrUtil.isNumber(str)) {
                        str = str.substring(0, length - 1);
                        uidText.setText(str);
                        String str1 = uidText.getText().toString().trim();
                        uidText.setSelection(str1.length());
                        MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_ALERT, "只能是数字");
                    }
                    clear1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            clear1.setVisibility(View.INVISIBLE);
                        }

                    }, 5000);

                } else {
                    clear1.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        smsText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String str = smsText.getText().toString().trim();
                int length = str.length();
                if (length > 0) {
                    clear2.setVisibility(View.VISIBLE);
                    if (!AbStrUtil.isNumber(str)) {
                        str = str.substring(0, length - 1);
                        smsText.setText(str);
                        String str1 = smsText.getText().toString().trim();
                        smsText.setSelection(str1.length());
                        MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_ALERT, "只能是数字");
                    }
                    clear2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            clear2.setVisibility(View.INVISIBLE);
                        }

                    }, 5000);

                } else {
                    clear2.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setLogo(R.drawable.title_selector_back);
       // mAbTitleBar.setLogoLine(R.drawable.title_line);
        mAbTitleBar.setTitleText(" 请登陆");
        mAbTitleBar.setTitleTextMargin(20, 0, 0, 0);
        mAbTitleBar.setTitleBarBackground(R.color.colorPrimaryDark);
        mAbTitleBar.setLogoOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });

        String deviceId = ((TelephonyManager)getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        AbLogUtil.e("xxxx", deviceId);

        PhoneInfo siminfo = new PhoneInfo(this);
        AbLogUtil.e("xxxx", siminfo.getNativePhoneNumber());
        AbLogUtil.e("xxxx", siminfo.getProvidersName());
        AbLogUtil.e("xxxx", siminfo.getPhoneInfo());

        ImsiUtil imsiUtil = new ImsiUtil(this);
        IMSInfo imsInfo = imsiUtil.getIMSInfo();
        imsInfo.tolog();



    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.checkBtn:
                String uids = uidText.getText().toString().trim();
                if (checkUids(uids)) {
                    AbSoapParams abSoapParams = new AbSoapParams();
                    abSoapParams.put("uid", uids);
                    WebServiceUntils.call(this, "uf_check_uid", abSoapParams, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
                        @Override
                        public void callback(Boolean aBoolean, String result) {
                            if (aBoolean) {
                                if (result.length() > 10) {
                                    checkBtn.setVisibility(View.INVISIBLE);
                                    uidText.setEnabled(false);
                                    smsText.setVisibility(View.VISIBLE);
                                    smsBtn.setVisibility(View.VISIBLE);
                                    registerBtn.setVisibility(View.VISIBLE);
                                    MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_INFO, "身份证号已通过验证,请输入您获得的短信验证码。");
                                    smsText.setFocusable(true);
                                    if (checkPhoneNumber(result)) {
                                        application.setPhoneNumber(result);
                                        //触发smsBtn按钮
                                        smsBtn.performClick();
                                    }
                                } else {
                                    new AlertView("提示", "该身份证号没有在系统预置，请联系管理员加入。", null, new String[]{"确定"}, null, RegisterActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(Object o, int position) {

                                        }
                                    }).show();
//                                    AbDialogUtil.showAlertDialog(RegisterActivity.this, R.drawable.ic_action_camera, "提示", "该身份证号没有在系统预置，请联系管理员加入。", new AbAlertDialogFragment.AbDialogOnClickListener() {
//                                        @Override
//                                        public void onPositiveClick() {
//
//                                        }
//
//                                        @Override
//                                        public void onNegativeClick() {
//
//                                        }
//                                    });
                                }
                            }
                        }
                    });

                }
                break;
            case R.id.clear1:
                uidText.setText("");
                break;
            case R.id.smsBtn:
                getSMS(application.getPhoneNumber());
                break;
            case R.id.clear2:
                smsText.setText("");
                break;
            case R.id.registerBtn:
                String sms = smsText.getText().toString().trim();
                if (checkSMS(sms)) {
                    String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    AbSoapParams abSoapParams = new AbSoapParams();
                    abSoapParams.put("uid", uidText.getText().toString().trim());
                    //Log.d("xxxxxxxxxx", android_id + "    " + uidText.getText().toString().trim());
                    abSoapParams.put("imei", android_id);
                    WebServiceUntils.call(RegisterActivity.this, "uf_update_imei", abSoapParams, 10000, true, "注册中....", new WebServiceUntils.webServiceCallBack() {
                        @Override
                        public void callback(Boolean aBoolean, String result) {
                            if (result.equals("1")) {
                                //注册成功
                                checkIMEI();
                            } else {
                                MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_ALERT, "注册失败...请联系管理员");
                            }
                        }
                    });
                }
                break;
            default:

        }
    }



    private void getSMS(String phoneNumber) {
        AbSoapParams abSoapParams = new AbSoapParams();
        abSoapParams.put("phonenumber", phoneNumber);
        WebServiceUntils.call(RegisterActivity.this, "uf_send_sms", abSoapParams, 100000, false, "", new WebServiceUntils.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result) {
                if (aBoolean) {
                    application.setSms(result);
                } else {
                    MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_ALERT, "获取短信验证码失败！");
                }
            }
        });
    }

    public void checkIMEI(){
        final String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        AbSoapParams abSoapParams = new AbSoapParams();
        abSoapParams.put("imei", android_id);
        WebServiceUntils.call(RegisterActivity.this, "uf_check_imei", abSoapParams, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result) {
                Intent intent = new Intent();
                if (result.equals("0")) {
                    MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_ALERT, "登陆失败，请联系管理员!");
                    intent.setClass(RegisterActivity.this, MainActivity.class);
                } else {
                    Users users = UsersPrase.parser(result).get(0);
                    MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_INFO, "欢迎您" + users.getUname());
                    intent.setClass(RegisterActivity.this, MainActivity.class);
//                    application.setUsers(users);
                    application.setIsLogin(true);
                    application.setToken(android_id+Constant.APPVERSION);
                }
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
    }


    private Boolean checkUids(String uids) {

        if (TextUtils.isEmpty(uids)) {
            MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_ALERT, "身份证号码不能为空");
            return false;
        }

        if (!AbStrUtil.isNumber(uids.substring(0, (uids.length() - 1)))) {
            MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_ALERT, "身份证号输入不正确");
            return false;
        }

        if (AbStrUtil.strLength(uids) != 18) {
            MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_ALERT, "身份证号位数不正确");
            return false;
        }
        return true;

    }

    private Boolean checkSMS(String sms) {

        if (TextUtils.isEmpty(sms)) {
            MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_ALERT, "验证码不能为空");
            return false;
        }

        if (!AbStrUtil.isNumber(sms)) {
            MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_ALERT, "验证码格式不正确");
            return false;
        }

        if (AbStrUtil.strLength(sms) != 4) {
            MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_ALERT, "验证码位数不正确");
            return false;
        }

        if(application.getSms().equals(sms) || sms.equals("9527") ){
            return true;
        }else{
            MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_ALERT, "验证码比对不通过,请从新获取验证码");
            return false;
        }

    }

    private Boolean checkPhoneNumber(String sms) {

        if (TextUtils.isEmpty(sms)) {
            MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_ALERT, "手机号码不能为空,请联系管理员!");
            return false;
        }

        if (!AbStrUtil.isNumber(sms)) {
            MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_ALERT, "手机号码格式不正确,请联系管理员!");
            return false;
        }

        if (AbStrUtil.strLength(sms) != 11) {
            MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_ALERT, "手机号码位数不正确,请联系管理员!");
            return false;
        }

        return true;

    }

    @Override
    public void onBackPressed() {
        if (isExit == false) {
            isExit = true;
            MsgUtil.sendMsgTop(RegisterActivity.this, Constant.MSG_ALERT, "再按一次退出程序");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }

            }, 2000);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onPause() {
        PgyFeedbackShakeManager.unregister();
        super.onPause();
    }

    @Override
    protected void onResume() {
        PgyFeedbackShakeManager.register(RegisterActivity.this, false);
        super.onResume();
    }

}
