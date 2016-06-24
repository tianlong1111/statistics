package com.jiyoutang.statistics;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

public class UmsAgent {

    private static Handler handler;
    static boolean isPostFile = true;
    private static final String tag = "UMSAgent";

    private static boolean isInited = false;

    public enum LogLevel {
        Info, // equals Log.INFO, for less important info
        Debug, // equals Log.DEBUG, for some debug information
        Warn, // equals Log.WARN, for some warning info
        Error, // equals Log.ERROR, for the exceptions errors
        Verbose // equals Log.VERBOSE, for the verbose info
    }

    public enum SendPolicy {
        BATCH,
        REALTIME
    }

    static {
        HandlerThread localHandlerThread = new HandlerThread("UmsAgent");
        localHandlerThread.start();
        handler = new Handler(localHandlerThread.getLooper());
    }

    /**
     * @param context 上下文
     * @param urlDomain 数据传输接口
     * @param urlDomainForAll 数据批量传输接口
     * @param productId 产品号
     * @param channel 渠道号
     */
    public static void init(final Context context,
                            final String urlDomain,
                            final String urlDomainForAll,
                            final String productId,
                            final String channel) {
        if(!isInited){
            UmsConstants.channel = channel;
            UmsConstants.urlDomain = urlDomain;
            UmsConstants.urlDomainForAll = urlDomainForAll;
            UmsConstants.productId = productId;
            DeviceInfo.init(context);
            AppInfo.init(context);
            UmsAgent.postHistoryLog(context);
            isInited = true;
        }
    }


    /**
     * 用来统计用户情况
     * @param userId 登陆用户id。登出后，清空
     */
    public static void setUserId(String userId) {
        UmsConstants.userId = userId;
    }

    /**
     * 用来情况用户id
     * */
    public static void clearUserId() {
        UmsConstants.userId = "";
    }

    /**
     * 用来设置区域码
     * @param areaCode 区域码
     */
    public static void setAreaCode(String areaCode) {
        UmsConstants.areaCode = areaCode;
    }

    /**
     * Information is saved to a file by type
     *
     * @param uniqueCode
     * @param context
     */
    public static void onEvent(final Context context, final String uniqueCode) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                CobubLog.i(tag, "Call onEvent(context,uniqueCode)");
                EventManager em = new EventManager(context, uniqueCode, false, "");
                em.postEventInfo();
            }
        });
        handler.post(thread);
    }


    /**
     * @param context
     * @param uniqueCode
     * @param isCore
     */
    public static void onEvent(final Context context, final String uniqueCode, final boolean isCore) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                CobubLog.i(tag, "Call onEvent(uniqueCode,isCore)");
                EventManager em = new EventManager(context, uniqueCode, isCore, "");
                em.postEventInfo();
            }
        });
        handler.post(thread);
    }

    /**
     * @param uniqueCode
     * @param isCore
     * @param content
     */
    public static void onEvent(final Context context, final String uniqueCode, final boolean isCore, final String content) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                CobubLog.i(tag, "Call onEvent(uniqueCode,isCore,content) "+ uniqueCode);
                EventManager em = new EventManager(context, uniqueCode, isCore, content);
                em.postEventInfo();
            }
        });
        handler.post(thread);
    }

    /**
     * @param context
     */
    static void postHistoryLog(final Context context) {
        CobubLog.i(tag, "postHistoryLog");
        if (CommonUtil.isNetworkAvailable(context)) {
            if (UmsAgent.isPostFile) {
                Thread thread = new UploadHistoryLog(context);
                handler.post(thread);
                UmsAgent.isPostFile = false;
            }

        }
    }


    /**
     * @param isEnableDebug
     */
    public static void setDebugEnabled(boolean isEnableDebug) {
        UmsConstants.DebugEnabled = isEnableDebug;
    }

    /**
     * @param level
     */
    public static void setDebugLevel(LogLevel level) {
        UmsConstants.DebugLevel = level;
    }


    /**
     * @param isUpdateonlyWifi
     */
    public static void setUpdateOnlyWifi(boolean isUpdateonlyWifi) {
        UmsConstants.mUpdateOnlyWifi = isUpdateonlyWifi;
        CobubLog.i(tag, "setUpdateOnlyWifi = " + String.valueOf(isUpdateonlyWifi));
    }

    /**
     * Setting data transmission mode
     *
     * @param context
     * @param sendPolicy
     */
    public static void setDefaultReportPolicy(Context context, SendPolicy sendPolicy) {
        UmsConstants.mReportPolicy = sendPolicy;
        CobubLog.i(tag, "setDefaultReportPolicy = " + String.valueOf(sendPolicy));
    }


    /**
     * Default settings for continue Session duration. If user quit the app and
     * then re-entry the app in [interval] seconds, it will be seemed as the same session.
     * @param interval
     */
    public static void setSessionContinueMillis(long interval) {
        CobubLog.i(tag, "setSessionContinueMillis = " + String.valueOf(interval));
        if (interval > 0) {
            UmsConstants.kContinueSessionMillis = interval;
        }
    }

    public static void onAppStart(Context context){
        onEvent(context, UmsConstants.newAppStartUniqueCode, true);
        /**重置启动判断辅助器*/
        CommonUtil.savePageName(context,"");
        CommonUtil.saveSessionTime(context);
    }



}
