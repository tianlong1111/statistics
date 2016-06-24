/**
 * Cobub Razor
 *
 * An open source analytics android sdk for mobile applications
 *
 * @package		Cobub Razor
 * @author		WBTECH Dev Team
 * @copyright	Copyright (c) 2011 - 2015, NanJing Western Bridge Co.,Ltd.
 * @license		http://www.cobub.com/products/cobub-razor/license
 * @link		http://www.cobub.com/products/cobub-razor/
 * @since		Version 0.1
 * @filesource
 */

package com.jiyoutang.statistics;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

class UploadHistoryLog extends Thread {
    public Context context;

    private final String tag = "UploadHistoryLog";

    private int retryCount = 3;
    
    public UploadHistoryLog(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void run() {
        postData();
        UmsAgent.isPostFile = true;
    }

    private void postData(){
        String cacheFile = context.getCacheDir()+"/cobub.cache";
        CobubLog.i(tag,"Get cache file "+cacheFile);
        File file1;
        FileInputStream in;
        try {
            file1 = new File(cacheFile);
            if (!file1.exists()) {
                CobubLog.d(tag, "No history log file found!");
                return;
            }
            in = new FileInputStream(file1);
            StringBuffer sb = new StringBuffer();

            int i = 0;
            byte[] s = new byte[1024 * 4];

            while ((i = in.read(s)) != -1) {
                sb.append(new String(s, 0, i));
            }
            JSONObject jsonObject = prepareJsonData(sb.toString());
            String result = NetworkUtil.Post(
                    UmsConstants.urlDomainForAll, ("jsonParams="+jsonObject.toString()).replace("\n",""));
            CobubLog.e(tag, jsonObject.toString());
            if ("3000".equals(result)) {
                File file = new File(cacheFile);
                file.delete();
            }else{
                if(retryCount > 0){
                    retryCount -- ;
                    postData();
                }
            }
        } catch (Exception e) {
            CobubLog.e(tag,e);
            if(retryCount > 0){
                retryCount -- ;
                postData();
            }
        }
    }

    private JSONObject prepareJsonData(String jsonStr){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonStr);
            jsonObject.put("sourceApp",UmsConstants.sourceApp);
            jsonObject.put("source",UmsConstants.source);
            jsonObject.put("channel",UmsConstants.channel);
            jsonObject.put("userId", UmsConstants.userId);
            jsonObject.put("areaCode", UmsConstants.areaCode);
            jsonObject.put("versionCode",AppInfo.getAppVersion());
            jsonObject.put("deviceId",URLEncoder.encode(DeviceInfo.getDeviceId(), "UTF-8"));
            jsonObject.put("sysVersion",URLEncoder.encode(DeviceInfo.getOsVersion(), "UTF-8"));
            jsonObject.put("deviceName",URLEncoder.encode(DeviceInfo.getDeviceName(), "UTF-8"));
            jsonObject.put("netType",DeviceInfo.getNetworkTypeWIFI2G3G());
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


}
