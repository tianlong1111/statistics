/**
 * Cobub Razor
 * <p/>
 * An open source analytics android sdk for mobile applications
 *
 * @package Cobub Razor
 * @author WBTECH Dev Team
 * @copyright Copyright (c) 2011 - 2015, NanJing Western Bridge Co.,Ltd.
 * @license http://www.cobub.com/products/cobub-razor/license
 * @link http://www.cobub.com/products/cobub-razor/
 * @since Version 0.1
 * @filesource
 */

package com.jiyoutang.statistics;

import com.jiyoutang.statistics.UmsAgent.LogLevel;
import com.jiyoutang.statistics.UmsAgent.SendPolicy;

class UmsConstants {
    // Set the SDK Logs output. If DebugEnabled == true, the log will be
    // output depends on DebugLevel. If DebugEnabled == false, there is 
    // no any outputs.
    public static boolean DebugEnabled = true;

    // Default Log Level is Debug, no log information will be output in Logcat
    public static LogLevel DebugLevel = LogLevel.Debug;

    // Default settings for continue Session duration. If user quit the app and 
    // then re-entry the app in 30 seconds, it will be seemed as the same session.
    public static long kContinueSessionMillis = 30000L; // Default is 30s.

    public static boolean mProvideGPSData = false; // Default is false, not use GPS data. 

    public static boolean mUpdateOnlyWifi = true; // Default is true, only wifi update

    // Report policy: 1 means sent the data to server immediately
    // 0 means the data will be cached and sent to server when next app's start up.
    public static SendPolicy mReportPolicy = SendPolicy.REALTIME; //Default is 1, real-time


    //数据统计上传的单个接口域名
    public static String urlDomain = "http://172.16.32.7:8081/actionDetail/collectBatch.do?";
    //数据统计上传的批量接口域名
    public static String urlDomainForAll = "http://172.16.32.7:8081/actionDetail/collect.do?";

    // 平台号（Android/iOS）
    public static String source = "20";

    // 渠道号（channel）
    public static String channel = "xiaomi";

    // 登陆用户的用户id
    public static String userId = "";

    // 产品号
    public static String sourceApp = "6";

    //区域码
    public static String areaCode = "00001";

    public static String newAppStartUniqueCode="tj9020";

    public static String newActivityStartUniqueCode="tj9021";

}


