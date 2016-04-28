package com.gongdian.qmcb.utils;

import android.content.Context;

import com.ab.soap.AbSoapListener;
import com.ab.soap.AbSoapParams;
import com.ab.soap.AbSoapUtil;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbStrUtil;
import com.ab.util.AbToastUtil;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;


/**
 * Created by qian-pc on 12/20/15.
 */
public class WebServiceUntils {


    public static void call(final Context context, final String methodName, final AbSoapParams params, int timeout,
                          final Boolean isShowProgress, final String progressText, final webServiceCallBack webServiceCallBack) {

        params.put("token", MyApplication.getInstance().getToken());
        AbLogUtil.d("tttt-方法-" + methodName,methodName);
        AbLogUtil.d("tttt-参数-"+methodName,params.toString());


        // 一个url地址
        String urlString = Constant.SOAPURL;
        String nameSpace = Constant.SOAPNSP;
        AbSoapUtil abSoapUtil = AbSoapUtil.getInstance(context);
        abSoapUtil.setTimeout(timeout);

        abSoapUtil.call(urlString, nameSpace, methodName, params, new AbSoapListener() {

            // 获取数据成功会调用这里
            @Override
            public void onSuccess(int statusCode, SoapObject object) {
                AbDialogUtil.removeDialog(context);
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
                AbToastUtil.showToast(context, error.getMessage());
            }

            @Override
            public void onFailure(int statusCode, SoapFault fault) {
                webServiceCallBack.callback(false, "");
                AbToastUtil.showToast(context, fault.faultstring);
            }

            // 开始执行前
            @Override
            public void onStart() {
                if (isShowProgress) {
                    AbDialogUtil.showProgressDialog(context, 0, progressText);
                }
            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
                // 移除进度框
                AbDialogUtil.removeDialog(context);
            }

        });

    }

    public interface webServiceCallBack {
        void callback(Boolean aBoolean, String result);
    }


}
