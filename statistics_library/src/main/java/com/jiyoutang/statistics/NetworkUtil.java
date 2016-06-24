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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class NetworkUtil {
    private static final String TAG = "NetworkUtil";
    private static int CONNECT_TIMEOUT_IN_MILLISECONDS = 1500;
    private static int READ_TIMEOUT_IN_MILLISECONDS = 2000;

    public static String Post(String urlStr, String data) {
        CobubLog.d(TAG, "urlStr=" + urlStr);
        CobubLog.d(TAG, "data=" + data);
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT_IN_MILLISECONDS);
            conn.setReadTimeout(READ_TIMEOUT_IN_MILLISECONDS);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream os = conn.getOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            CobubLog.d(TAG, "responseCode=" + responseCode);
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = br.readLine();
            br.close();
            CobubLog.d(TAG, "line=" + line);
            return line;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return null;
    }


    public static String Get(String urlStr, String data) {
        HttpURLConnection conn = null;
        try {
            CobubLog.d(TAG, "urlStr+data=" + urlStr + data);
            URL url = new URL(urlStr + data);
            CobubLog.d(TAG, "urlStr=" + urlStr);
            CobubLog.d(TAG, "url=" + url.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT_IN_MILLISECONDS);
            conn.setReadTimeout(READ_TIMEOUT_IN_MILLISECONDS);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.setDoOutput(false);

            conn.setDoInput(true);
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = br.readLine();
            br.close();
            CobubLog.d(TAG, "line=" + line);
            return line;
        } catch (MalformedURLException e) {
            CobubLog.d(TAG, "e.getMessage()=" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            CobubLog.d(TAG, "e.getMessage()=" + e.getMessage());
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return null;
    }


}
