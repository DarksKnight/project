package com.express56.xq.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * 设备相关的工具类
 *
 * @author Reed.Qiu
 */
public class DeviceUtil {

    private static final String TAG = DeviceUtil.class.getSimpleName();

    /**
     * 在一般情况下 deviceId 不会没有内容， 否则就自己生成一个deviceId
     *
     * @param context
     * @param deviceId
     * @return
     */
    public static String getTheDeviceID(Activity context, String deviceId) {
        SharedPreUtils spUtil = new SharedPreUtils(context);
        //add by Reed  保证能够有deviceId
        if (deviceId == null || deviceId.isEmpty() || deviceId.length() < 5) {
//            String localDeviceId = spUtil.getString("deviceId");
            String localDeviceId = FileUtil.readDeviceNo(context);
            if (localDeviceId == null || localDeviceId.isEmpty()) {
                deviceId = java.util.UUID.randomUUID().toString().replaceAll("-", "");
//                spUtil.setString("deviceId", deviceId);
                FileUtil.saveDeviceNo(context, deviceId);
            } else {
                deviceId = localDeviceId;
            }
        }
        return deviceId;
    }

    public static void getLocalIpAndMac(Context context) {
        LogUtil.i(TAG, "IP: " + getLocalIpAddress() + ", MAC: " + getLocalMacAddress(context));
    }

    /**
     * 获取Android本机IP地址
     *
     * @return
     */
    private static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            LogUtil.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    /**
     * 获取Android本机MAC
     *
     * @return
     */
    private static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

}
