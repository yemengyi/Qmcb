package com.gongdian.qmcb.permission.listener;

import android.app.Activity;
import android.util.Log;


/**
 *
 * 检查权限的监听器<br/>
 *
 * Created by yang3 on 2015/9/10.
 */
public abstract class AbstractPermissionListener implements PermissionListener {


    @Override
    public void onPermissionsError(Activity act, int requestCode, int[] grantResults, String errorMsg, String... permissions) {
        //
        String msg = "act:" + act + ",permissions:" + permissions
                + ",requestCode:" + requestCode + ",grantResults:" + grantResults + ">>error: " + errorMsg;
        Log.e("PermissionsDispatcher", msg);
    }
}
