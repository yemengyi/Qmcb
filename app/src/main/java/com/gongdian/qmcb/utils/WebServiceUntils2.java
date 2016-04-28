package com.gongdian.qmcb.utils;

import android.content.Context;
import android.content.DialogInterface;

import com.ab.fragment.AbDialogFragment;
import com.ab.fragment.AbLoadDialogFragment;
import com.ab.fragment.AbRefreshDialogFragment;
import com.ab.soap.AbSoapListener;
import com.ab.soap.AbSoapParams;
import com.ab.soap.AbSoapUtil;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbStrUtil;
import com.ab.util.AbToastUtil;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.gongdian.qmcb.R;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;


/**
 * Created by qian-pc on 12/20/15.
 */
public class WebServiceUntils2 {
    private Context context;
    private String methodName;
    private AbSoapParams params;
    private int count = 0;

    public WebServiceUntils2(Context context, String methodName, AbSoapParams params) {
        this.context = context;
        this.methodName = methodName;
        this.params = params;
        this.params.put("token",MyApplication.getInstance().getToken());
    }
    public WebServiceUntils2(Context context, String methodName) {
        this.context = context;
        this.methodName = methodName;
    }

    public static WebServiceUntils2 newInstance(Context context, String methodName, AbSoapParams params){
        WebServiceUntils2 webServiceUntils2 = new WebServiceUntils2(context,methodName,params);
       return webServiceUntils2;
    }

    /**
     * 显示加载弹出框无背景层
     */
    public void start(final webServiceCallBack webServiceCallBack) {
        final AbLoadDialogFragment mDialogFragment = AbDialogUtil
                .showLoadDialog(context, R.drawable.list_load, "请稍候...", AbDialogUtil.ThemeLightPanel);

        mDialogFragment.setAbDialogOnLoadListener(new AbDialogFragment.AbDialogOnLoadListener() {
            @Override
            public void onLoad() {
                call(context, methodName, params, mDialogFragment, webServiceCallBack);
            }

        });
        // 取消的监听
        mDialogFragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
    }


    /**
     * 显示刷新弹出框无背景层
     */
    private  void showRefreshPanel(final webServiceCallBack webServiceCallBack) {
        // 显示重新刷新的框
        final AbRefreshDialogFragment mDialogFragment = AbDialogUtil
                .showRefreshDialog(context, R.drawable.ic_action_reload, "点击重试...", AbDialogUtil.ThemeLightPanel);
        mDialogFragment.setAbDialogOnLoadListener(new AbDialogFragment.AbDialogOnLoadListener() {
            @Override
            public void onLoad() {
                count++;
                call(context, methodName, params, mDialogFragment, webServiceCallBack);
            }
        });

        mDialogFragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }

        });
    }


    private void call(final Context context, final String methodName, final AbSoapParams params,  final AbDialogFragment mDialogFragment, final webServiceCallBack webServiceCallBack) {
        AbLogUtil.d("tttt-方法-" + methodName, methodName);
        AbLogUtil.d("tttt-参数-"+methodName,params.toString());
        // 一个url地址
        String urlString = Constant.SOAPURL;
        String nameSpace = Constant.SOAPNSP;
        AbSoapUtil abSoapUtil = AbSoapUtil.getInstance(context);
        abSoapUtil.setTimeout(10000);

        abSoapUtil.call(urlString, nameSpace, methodName, params, new AbSoapListener() {

            // 获取数据成功会调用这里
            @Override
            public void onSuccess(int statusCode, SoapObject object) {
                count = 0;
                mDialogFragment.loadFinish();
                String rtn = object.getProperty(0).toString().trim();
                AbLogUtil.d("tttt-返回值-"+methodName,rtn);

                if (AbStrUtil.isEquals(rtn, Constant.VERSION_ERROR)) {
                    new AlertView("提示", "请升级为最新版本", null, new String[]{"确定"}, null, context, AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                        }
                    }).show();
                    webServiceCallBack.callback(false, rtn);
                } else if (AbStrUtil.isEquals(rtn, Constant.IMEI_ERROR)) {
                    new AlertView("提示", "登录信息失效,请重新登录!", null, new String[]{"确定"}, null, context, AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                        }
                    }).show();
                    webServiceCallBack.callback(false, rtn);
                } else {
                    webServiceCallBack.callback(true, rtn);
                }
            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, String content,
                                  Throwable error) {
                webServiceCallBack.callback(false, "");
                mDialogFragment.loadFinish();
                if (count < 3) {
                    showRefreshPanel(webServiceCallBack);
                    AbToastUtil.showToast(context, error.getMessage());
                } else {
                    AbDialogUtil.showAlertDialog(context, R.drawable.ic_action_cancel, "错误提示", "服务故障,请检查网络");
                }
            }

            @Override
            public void onFailure(int statusCode, SoapFault fault) {
                webServiceCallBack.callback(false, "");
                mDialogFragment.loadFinish();
                if (count < 3) {
                    showRefreshPanel(webServiceCallBack);
                    AbToastUtil.showToast(context, fault.faultstring);
                } else {
                    AbDialogUtil.showAlertDialog(context, R.drawable.ic_action_cancel, "错误提示", "服务故障,请检查网络");

                }
            }

            // 开始执行前
            @Override
            public void onStart() {

            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
            }

        });

    }

    public interface webServiceCallBack {
        void callback(Boolean aBoolean, String result);
    }


    public void setParams(AbSoapParams params) {
        this.params = params;
        this.params.put("token", MyApplication.getInstance().getToken());
    }
}
