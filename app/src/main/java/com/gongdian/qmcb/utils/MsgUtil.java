package com.gongdian.qmcb.utils;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.devspark.appmsg.AppMsg;
import com.gongdian.qmcb.R;

import static android.view.Gravity.BOTTOM;
import static com.devspark.appmsg.AppMsg.LENGTH_SHORT;
import static com.devspark.appmsg.AppMsg.LENGTH_STICKY;

/**
 * Created by qian-pc on 1/26/16.
 */
public  class MsgUtil {
    static AppMsg.Style style;
    static boolean customAnimations = true;
    static AppMsg provided = null;
    static int priority1 = AppMsg.PRIORITY_HIGH;
    static int priority2 = AppMsg.PRIORITY_LOW;
    static int priority3 = AppMsg.PRIORITY_NORMAL;

    public  static void sendMsgTop(Activity activity,String style,String msg){


        sendMsg(activity,style,msg,BOTTOM,null);

    }
    public  static void sendMsgButton(Activity activity,String style,String msg){

        sendMsg(activity,style,msg,BOTTOM,null);

    }
    public  static void sendMsgParent(Activity activity,String style,String msg,ViewGroup viewGroup){

        sendMsg(activity, style, msg, BOTTOM, viewGroup);
    }

    public static void cancelAll(Activity activity){
        AppMsg.cancelAll(activity);
    }


    public static void sendMsg(Activity activity,String styleSelected,String msg,int position,ViewGroup viewGroup){
        AppMsg.cancelAll();
        switch (styleSelected) {
            case Constant.MSG_ALERT:
                style = AppMsg.STYLE_ALERT;
                break;
            case Constant.MSG_CONFIRM:
                style = AppMsg.STYLE_CONFIRM;
                break;
            case Constant.MSG_CUSTOM:
                style = new AppMsg.Style(LENGTH_SHORT, R.color.skybule);
                customAnimations = true;
                break;
            case Constant.MSG_STICKY:
                style = new AppMsg.Style(LENGTH_STICKY, R.color.sticky);
                provided = AppMsg.makeText(activity, msg, style, R.layout.sticky);
                provided.getView()
                        .findViewById(R.id.remove_btn)
                        .setOnClickListener(new CancelAppMsg(provided));
                break;
            case Constant.MSG_INFO:
                style = AppMsg.STYLE_INFO;
                break;
            default:
                style = AppMsg.STYLE_INFO;
                break;
        }
        AppMsg appMsg = provided != null ? provided : AppMsg.makeText(activity, msg, style);
        appMsg.setPriority(priority1);
        appMsg.setLayoutGravity(position);
        if (viewGroup!=null) {
            ViewGroup animatedRoot = viewGroup;
            animatedRoot.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
            appMsg.setParent(viewGroup);
        }

        if (customAnimations) {
            appMsg.setAnimation(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        appMsg.show();

    }

    static class CancelAppMsg implements View.OnClickListener {
        private final AppMsg mAppMsg;

        CancelAppMsg(AppMsg appMsg) {
            mAppMsg = appMsg;
        }

        @Override
        public void onClick(View v) {
            mAppMsg.cancel();
        }
    }
}
