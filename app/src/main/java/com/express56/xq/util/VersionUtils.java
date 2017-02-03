package com.express56.xq.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;


public class VersionUtils {

	public static final String TAG = VersionUtils.class.getSimpleName();

	/**
	 * 获得当前版本号
	 */
	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					context.getApplicationInfo().packageName, 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	//版本名
	public static String getVersionName1(Context context) {
		return getPackageInfo(context).versionName;
	}

	//版本号
	public static int getVersionCode1(Context context) {
		LogUtil.d(TAG , "versionCode = " + getPackageInfo(context).versionCode);
		return getPackageInfo(context).versionCode;
	}

	private static PackageInfo getPackageInfo(Context context) {
		PackageInfo pi = null;

		try {
			PackageManager pm = context.getPackageManager();
			pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_CONFIGURATIONS);

			return pi;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pi;
	}

	/**
	 * 获得当前版本号
	 */
	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			versionName = context.getPackageManager().getPackageInfo(
					context.getApplicationInfo().packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	public static int getAPPVersionCodeFromAPP(Context ctx) {
		int currentVersionCode = 0;
		PackageManager manager = ctx.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
			String appVersionName = info.versionName; // 版本名
			currentVersionCode = info.versionCode; // 版本号
			System.out.println(currentVersionCode + " " + appVersionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch blockd
			e.printStackTrace();
		}
		return currentVersionCode;
	}


	/**
	 * 返回当前程序版本名
	 */
	public static String getAppVersionName(Context context) {
	    String versionName = "";
	    try {
	        // ---get the package info---
	        PackageManager pm = context.getPackageManager();
	        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
	        versionName = pi.versionName;
	        if (versionName == null || versionName.length() <= 0) {
	            return "";
	        }
	    } catch (Exception e) {
	        LogUtil.e("VersionInfo", "Exception", e);
	    }
	    return versionName;
	}

}
