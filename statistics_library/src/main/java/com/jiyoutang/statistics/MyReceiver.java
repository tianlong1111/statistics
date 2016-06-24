package com.jiyoutang.statistics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MyReceiver extends BroadcastReceiver {
    private static final String tag ="MyReceiver";

    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        if (activeInfo != null) {
            CobubLog.i(tag, "activeInfo.getTypeName()=" + activeInfo.getTypeName());
            DeviceInfo.init(context);
            AppInfo.init(context);
            UmsAgent.postHistoryLog(context);
        }
    }
}
