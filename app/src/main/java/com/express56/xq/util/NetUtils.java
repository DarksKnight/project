package com.express56.xq.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * NetUtils
 * 网络相关工具类
 *
 */
public class NetUtils {
	private static final int PROXY_PORT = 80;
	private static String currentAPN;
	private static final String CMWAP = "cmwap"; // 移动wap
	private static final String CM_AND_UNI_WAP_HOST = "10.0.0.172";
	private static final String UNIWAP = "uniwap"; // 联通2g wap 联通的接入点 和 移动一样是172
	private static final String L3GWAP = "3gwap"; // 联通3g wap
	private static final String CTWAP = "ctwap"; // 电信wap
	private static final String CTWAP_HOST = "10.0.0.200";
	private static int timeout = 30000;//set default timeout 30 seconds.
	private static final boolean DEBUG = false ;

	 /**
     * make true current connect service is wifi
     * @param mContext
     * @return
     */
	public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }


	private static boolean isWapType(String wapType, String apnName) {
		if (apnName == null) {
			return false;
		} else {
			return apnName.contains(wapType);
		}
	}

}
