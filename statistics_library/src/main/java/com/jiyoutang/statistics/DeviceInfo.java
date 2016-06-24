package com.jiyoutang.statistics;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author apple
 */
public class DeviceInfo {

    private static final String tag = "DeviceInfo";
    private static Context context;
    private static Location location;
    private static TelephonyManager telephonyManager;
    private static BluetoothAdapter bluetoothAdapter;
    private static SensorManager sensorManager;

    public static void init(Context context) {
        DeviceInfo.context = context;

        try {
            telephonyManager = (TelephonyManager) (context
                    .getSystemService(Context.TELEPHONY_SERVICE));
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        } catch (Exception e) {
            CobubLog.e(tag, e.toString());
        }
    }

    public static String getLanguage() {
        String language = Locale.getDefault().getLanguage();
        CobubLog.i(tag, "getLanguage()=" + language);
        if (language == null)
            return "";
        return language;
    }

    public static String getResolution() {

        DisplayMetrics displaysMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displaysMetrics);
        CobubLog.i(tag, "getResolution()=" + displaysMetrics.widthPixels + "x"
                + displaysMetrics.heightPixels);
        return displaysMetrics.widthPixels + "x" + displaysMetrics.heightPixels;
    }

    public static String getDeviceProduct() {
        String result = Build.PRODUCT;
        CobubLog.i(tag, "getDeviceProduct()=" + result);
        if (result == null)
            return "";
        return result;
    }

    public static boolean getBluetoothAvailable() {
        if (bluetoothAdapter == null)
            return false;
        else
            return true;
    }

    private static boolean isSimulator() {
        if (getDeviceIMEI().equals("000000000000000"))
            return true;
        else
            return false;
    }

    public static boolean getGravityAvailable() {
        try {
        // This code getSystemService(Context.SENSOR_SERVICE);
        // often hangs out the application when it runs in Android Simulator.
        // so in simulator, this line will not be run.
        if (isSimulator())
            sensorManager = null;
        else
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        CobubLog.i(tag, "getGravityAvailable()");
        return (sensorManager == null) ? false : true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getOsVersion() {
        String result = Build.VERSION.RELEASE;
        CobubLog.i(tag, "getOsVersion()=" + result);
        if (result == null)
            return "";

        return result;
    }

    /**
     * Returns a constant indicating the device phone type. This indicates the
     * type of radio used to transmit voice calls.
     * 
     * @return PHONE_TYPE_NONE //0 PHONE_TYPE_GSM //1 PHONE_TYPE_CDMA //2
     *         PHONE_TYPE_SIP //3
     */
    public static int getPhoneType() {
        if (telephonyManager == null)
            return -1;
        int result = telephonyManager.getPhoneType();
        CobubLog.i(tag, "getPhoneType()=" + result);
        return result;
    }

    /**
     * get IMSI for GSM phone, return "" if it is unavailable.
     * 
     * @return IMSI string
     */
    public static String getIMSI() {
        String result = "";
        try {
            if (!CommonUtil.checkPermissions(context, Manifest.permission.READ_PHONE_STATE)) {
                CobubLog.e(tag,
                        "READ_PHONE_STATE permission should be added into AndroidManifest.xml.");
                return "";
            }
            result = telephonyManager.getSubscriberId();
            CobubLog.i(tag, "getIMSI()=" + result);
            if (result == null)
                return "";
            return result;

        } catch (Exception e) {
            CobubLog.e(tag, e);
        }

        return result;
    }

    public static String getWifiMac() {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wi = wifiManager.getConnectionInfo();
            String result = wi.getMacAddress();
            if (result == null)
                result = "";
            CobubLog.i(tag, "getWifiMac()=" + result);
            return result;
        } catch (Exception e) {
            CobubLog.e(tag,e);
            return "";
        }

    }

    public static String getDeviceTime() {
        try {
            Date date = new Date();
            SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.US);
            String result = localSimpleDateFormat.format(date);
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getDeviceName() {
        String deviceName = (android.os.Build.BRAND + "_" + android.os.Build.MODEL).replace(" ", "");
        try {
            deviceName = URLDecoder.decode(deviceName, "UTF-8");
        } catch (Exception e) {
        }
        return deviceName;
    }

    /**服务器端规定的网络类型，用于上传数据到服务器时使用*/
    public class NetTypeByServerRule{

        public static final int NET_2G = 1;
        public static final int NET_3G = 2;
        public static final int NET_4G = 3;
        public static final int NET_WIFI = 4;
        public static final int NET_OTHRE = 5;

    }

    /**
     * 返回当前网络类型
     *  网络类型(2G=1；3G=2；4G=3；wifi=4;其他=5 ,没有网络=-1)
     */
    public static int getNetworkTypeWIFI2G3G()
    {
        int currNetType = -1 ;

        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
            {
                currNetType =  NetTypeByServerRule.NET_WIFI;
            }
            else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                String _strSubTypeName = networkInfo.getSubtypeName();


                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        currNetType = NetTypeByServerRule.NET_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        currNetType = NetTypeByServerRule.NET_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        currNetType = NetTypeByServerRule.NET_4G;
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000"))
                        {
                            currNetType = NetTypeByServerRule.NET_3G;
                        }
                        else
                        {
                            currNetType = NetTypeByServerRule.NET_OTHRE;
                        }

                        break;
                }

            }
        }

        return currNetType;
    }




    public static boolean getWiFiAvailable() {
        try {
        if (!CommonUtil.checkPermissions(context, Manifest.permission.ACCESS_WIFI_STATE)) {
            CobubLog.e(tag,
                    "ACCESS_WIFI_STATE permission should be added into AndroidManifest.xml.");
            return false;
        }
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI")
                            && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getDeviceIMEI() {
        String result = "";
        try {
            if (!CommonUtil.checkPermissions(context, Manifest.permission.READ_PHONE_STATE)) {
                CobubLog.e(tag,
                        "READ_PHONE_STATE permission should be added into AndroidManifest.xml.");
                return "";
            }
            result = telephonyManager.getDeviceId();
            if (result == null)
                result = "";
        } catch (Exception e) {
            CobubLog.e(tag, e);
        }
        return result;
    }

    private static String getSSN() {
        String result = "";
        try {

            if (!CommonUtil.checkPermissions(context, Manifest.permission.READ_PHONE_STATE)) {
                CobubLog.e(tag,
                        "READ_PHONE_STATE permission should be added into AndroidManifest.xml.");
                return "";
            }
            result = telephonyManager.getSimSerialNumber();
            if (result == null)
                result = "";
        } catch (Exception e) {
            CobubLog.e(tag, e);
        }
        return result;
    }


    private static String sDeviceId = null;
    private final static String DEVICE_ID_FILENAME_NEW = "DEV";

    public static String getDeviceId() {
        if (sDeviceId == null) {
            File newFile = new File(context.getFilesDir(), DEVICE_ID_FILENAME_NEW);
            if (newFile.exists()) {
                CobubLog.d(tag,"getDeviceId(), newFile exist.");
                //新文件存在，直接读取
                sDeviceId = readIdFile(context, newFile, true);
                if (TextUtils.isEmpty(sDeviceId)) {
                    //可能读取失败，则重新生成
                    createDeviceId(context, newFile, true);
                }
            } else {
                CobubLog.d(tag,"getDeviceId(), newFile not exist.");
                createDeviceId(context, newFile, true);
            }
        }
        CobubLog.d(tag,"getDeviceId(), sDeviceId=" + sDeviceId);
        return (sDeviceId == null) ? "" : sDeviceId;
    }

    private static void createDeviceId(Context context, File file, boolean encode) {
        try {
            String deviceId = null;
            // IMEI
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE);
            deviceId = telephonyManager.getDeviceId();
            if (invalidDeviceId(context, deviceId)) {
                // CPU序列号
                deviceId = getCPUSerial();
                if (deviceId != null) {
                    deviceId = deviceId.toLowerCase();
                }
                if (invalidDeviceId(context, deviceId)) {
                    // android.os.Build.SERIAL
                    deviceId = getSerial();
                    if (invalidDeviceId(context, deviceId)) {
                        // ANDROID_ID
                        deviceId = getAndroidId(context);
                        if (invalidDeviceId(context, deviceId)) {
//                            // IMSI
//                            deviceId = getIMSI(context, 0);
                            if (invalidDeviceId(context, deviceId)) {
                                // MAC地址
                                deviceId = getMacAddress(context);
                                if (invalidDeviceId(context, deviceId)) {
                                    // UUID
                                    deviceId = "U" + getUUID();
                                } else {
                                    deviceId = "M" + deviceId;
                                }
                            } else {
                                deviceId = "I" + deviceId;
                            }
                        } else {
                            deviceId = "A" + deviceId;
                        }
                    } else {
                        deviceId = "S" + deviceId;
                    }
                } else {
                    deviceId = "C" + deviceId;
                }
            }
            sDeviceId = deviceId;
            writeIdFile(context, sDeviceId, file, encode);
        } catch (Exception ex) {
        }
    }

    private static void writeIdFile(Context context, String id, File idFile, boolean encode) {
        if (TextUtils.isEmpty(id))
            return;

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(idFile, false);
            if (encode) {
                id = AESUtils.AESEncrypt(id + context.getPackageName());
            }
            out.write(id.getBytes());
        } catch (Exception ex) {
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    /**
     * 获取UUID
     *
     * @return
     */
    private static String getUUID() {
        String id = null;
        try {
            id = UUID.randomUUID().toString();
            id = id.replaceAll("-", "").replace(":", "").toLowerCase();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return id;
    }

    /**
     * 获取网卡的MAC地址
     *
     * @param context
     * @return
     */
    private static String getMacAddress(Context context) {
        String macAddress = null;
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            if (info != null) {
                macAddress = info.getMacAddress();
                if (macAddress != null) {
                    macAddress = macAddress.replaceAll("-", "").replaceAll(":", "").toLowerCase();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return macAddress;
    }

    /**
     * 获取ANDROID_ID号，Android2.2版本以上系统有效
     *
     * @return
     */
    private static String getAndroidId(Context context) {
        String android_id = null;
        try {
            if (Build.VERSION.SDK_INT >= 8) {
                android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                if (android_id != null) {
                    android_id = android_id.toLowerCase();
                }
            }
        } catch (Throwable ex) {
        }
        return android_id;
    }

    /*
     * 获取机器Serial号，Android2.3版本以上有效
     */
    private static String getSerial() {
        String serial = null;
        try {
            if (Build.VERSION.SDK_INT >= 9) {
                Class<Build> clazz = Build.class;
                Field field = clazz.getField("SERIAL");
                serial = (String) field.get(null);
                if (serial != null) {
                    serial = serial.toLowerCase();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return serial;
    }

    /**
     * 获取CPU序列号
     *
     * @return CPU序列号(16位) 读取失败为null
     */
    private static String getCPUSerial() {
        String line = "";
        String cpuAddress = null;
        try {
            // 读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            // 查找CPU序列号
            for (int i = 1; i < 100; i++) {
                line = input.readLine();
                if (line != null) {
                    // 查找到序列号所在行
                    line = line.toLowerCase();
                    int p1 = line.indexOf("serial");
                    int p2 = line.indexOf(":");
                    if (p1 > -1 && p2 > 0) {
                        // 提取序列号
                        cpuAddress = line.substring(p2 + 1);
                        // 去空格
                        cpuAddress = cpuAddress.trim();
                        break;
                    }
                } else {
                    // 文件结尾
                    break;
                }
            }
            if (ir != null) {
                ir.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return cpuAddress;
    }

    private static String readIdFile(Context context, File idFile, boolean decode) {
        RandomAccessFile f = null;
        String deviceId = null;
        try {
            f = new RandomAccessFile(idFile, "r");
            byte[] bytes = new byte[(int) f.length()];
            f.readFully(bytes);
            if (decode) {
                deviceId = AESUtils.AESDecrypt(new String(bytes) + context.getPackageName());
            } else {
                deviceId = new String(bytes);
            }
        } catch (Exception ex) {
        } finally {
            if (f != null) {
                try {
                    f.close();
                } catch (Exception ex) {
                }
            }
        }
        return deviceId;
    }

    private final static String INVALID_IMEI_FILENAME = "invalid-imei.idx";
    private final static String ANDROID_ID_FILENAME = "ANDROID_ID"; //保存手机的android_id，用来校验DEVICE_ID是否需要重新获取
    private static boolean invalidDeviceId(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }

        InputStream is = null;
        try {
            is = context.getAssets().open(INVALID_IMEI_FILENAME);
            if (is != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String regexp = null;
                while ((regexp = br.readLine()) != null) {
                    try {
                        Pattern pattern = Pattern.compile(regexp);
                        Matcher match = pattern.matcher(str);
                        if (match.matches()) {
                            return true;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (br != null)
                    br.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ex) {
                }
            }
        }
        return false;
    }
    
    private static Map<String,String> getDeviceInfo(){
        Map<String,String> map = new HashMap<>();
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null){
            CobubLog.e(tag, "No IMEI.");
        }
        
        String deviceIdStr = null;
        try {
            if (CommonUtil.checkPermissions(context, Manifest.permission.READ_PHONE_STATE)){
                deviceIdStr = telephonyManager.getDeviceId();
            }
        }catch (Exception e){
            CobubLog.e(tag, "No IMEI.");
        }
        
        String macStr = getMacStr();
        String android_id_str = Settings.Secure.getString(context.getContentResolver(),"android_id");
        if (!TextUtils.isEmpty(macStr)){
            map.put("mac",macStr);
        }
        if (!TextUtils.isEmpty(deviceIdStr)){
            map.put("imei",deviceIdStr);
        }
        if (!TextUtils.isEmpty(android_id_str)){
            map.put("android_id",android_id_str);
        }
        return map;
    }

    private static String getMacStr() {
        try {
            WifiManager var1 = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            if(CommonUtil.checkPermissions(context, Manifest.permission.ACCESS_WIFI_STATE)) {
                WifiInfo var2 = var1.getConnectionInfo();
                return var2.getMacAddress();
            }

            CobubLog.e("MobclickAgent", "Could not get mac address.[no permission android.permission.ACCESS_WIFI_STATE");
        } catch (Exception var3) {
            CobubLog.e("MobclickAgent", "Could not get mac address." + var3.toString());
        }

        return "";

    }


    public static String getLatitude() {
        if (location == null)
            return "";
        return String.valueOf(location.getLatitude());
    }

    public static String getLongitude() {
        if (location == null)
            return "";
        return String.valueOf(location.getLongitude());

    }



    public static String getMCCMNC() {
        String result = "";
        try {

            String operator = telephonyManager.getNetworkOperator();
            if (operator == null)
                result = "";
            else
                result = operator;
        } catch (Exception e) {
            result = "";
            CobubLog.e(tag, e.toString());
        }
        return result;
    }
}
