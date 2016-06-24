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
 * @filesource
 * @since Version 0.1
 */
package com.jiyoutang.statistics;

import android.content.Context;
import android.text.TextUtils;

import com.jiyoutang.statistics.UmsAgent.SendPolicy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

class EventManager {
    private Context context;
    private String uniqueCode;
    private boolean isCore;
    private String content;
    private final String tag = "EventManager";
    private int retryCount = 3;

    public EventManager(Context context, String uniqueCode, boolean isCore, String content) {
        this.context = context;
        this.uniqueCode = uniqueCode;
        this.isCore = isCore;
        this.content = content;
    }

    private StringBuffer prepareEventData() {
        StringBuffer buffer = new StringBuffer();
        try {
            buffer.append("uniqueCode=" + uniqueCode);
            buffer.append("&sourceApp=" + UmsConstants.sourceApp);
            buffer.append("&source=" + UmsConstants.source);
            buffer.append("&channel=" + UmsConstants.channel);
            buffer.append("&userId=" + UmsConstants.userId);
            buffer.append("&areaCode=" + UmsConstants.areaCode);
            buffer.append("&versionCode=" + AppInfo.getAppVersion());
            buffer.append("&deviceId=" + URLEncoder.encode(DeviceInfo.getDeviceId(), "UTF-8"));
            buffer.append("&sysVersion=" + URLEncoder.encode(DeviceInfo.getOsVersion(), "UTF-8"));
            buffer.append("&deviceName=" + URLEncoder.encode(DeviceInfo.getDeviceName(), "UTF-8"));
            buffer.append("&netType=" + DeviceInfo.getNetworkTypeWIFI2G3G());
            buffer.append("&realTime=" + URLEncoder.encode(DeviceInfo.getDeviceTime(), "UTF-8"));
            buffer.append("&content=" + URLEncoder.encode(content, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return buffer;

    }

    public void postEventInfo() {
        StringBuffer buffer = prepareEventData();
        if (CommonUtil.getReportPolicyMode(context) == SendPolicy.REALTIME
                && CommonUtil.isNetworkAvailable(context)) {

            String jsonStr = NetworkUtil.Get(UmsConstants.urlDomain,
                    buffer.toString());
            if(TextUtils.isEmpty(jsonStr)){
                if(retryCount > 0){
                    retryCount--;
                    postEventInfo();
                }else{
                    if(isCore){
                        saveInfoToFile();
//                        Toast.makeText(context, "解析失败，缓存本地", Toast.LENGTH_SHORT).show();
                    }
                }
                return;
            }
            try {
                JSONObject object = new JSONObject(jsonStr);
                String result = object.getString("result");
                CobubLog.e(tag, "result=" + result);
                if (!"3000".equals(result)) {
                    if (retryCount > 0) {
                        retryCount--;
                        postEventInfo();
                    } else {
                        if(isCore){
                            saveInfoToFile();
//                            Toast.makeText(context, "发送失败，缓存本地", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
//                    Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                if (retryCount > 0) {
                    retryCount--;
                    postEventInfo();
                } else {
                    if(isCore){
                        saveInfoToFile();
//                        Toast.makeText(context, "解析失败，缓存本地", Toast.LENGTH_SHORT).show();
                    }
                }
                e.printStackTrace();
            }

        } else {
            saveInfoToFile();
//            Toast.makeText(context, "无网络，缓存本地", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveInfoToFile() {
        JSONObject localJSONObject = new JSONObject();
        try {
            localJSONObject.put("uniqueCode", uniqueCode);
            localJSONObject.put("realTime", URLEncoder.encode(DeviceInfo.getDeviceTime(), "UTF-8"));
            localJSONObject.put("content", new JSONObject(content));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CommonUtil.saveInfoToFile("params", localJSONObject, context);
    }

}
