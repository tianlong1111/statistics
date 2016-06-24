/*
*
*
*
* */
package com.jiyoutang.statistics;

import android.content.Context;
import android.content.pm.PackageManager;

class AppInfo {
    private static Context context;

    static void init(Context context) {
        AppInfo.context = context;
    }


    /**
     * 获取app的版本号 例如：versionCode = 130
     */
    public static int getAppVersion() {
        int result = 1;
        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return result;
    }

}
