package com.gongdian.qmcb.permission;
/**
 * Created by qian-pc on 12/28/15.
 */

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by qian-pc on 12/26/15.
 * android 6.0 权限检查及开启权限工具类
 */
public class CheckPermission {
    private Context context = null;

    public CheckPermission(Context context) {
        this.context = context;
    }

    public static CheckPermission newInstance(Context context) {
        CheckPermission permissionUtil = new CheckPermission(context);
        return permissionUtil;
    }

    public Boolean check(String permission) {
        int checkCode = ContextCompat.checkSelfPermission(context, permission);
        if (checkCode != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }
}
