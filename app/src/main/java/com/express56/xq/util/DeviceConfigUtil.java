package com.express56.xq.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;


public class DeviceConfigUtil {
	public static final String TAG = DeviceConfigUtil.class.getSimpleName();
    /**
     * 判断通知显示权限是否打开
     * @param context
     * @return
     */
	public static boolean isSystemNotificationOpAllowed(Context context) {
		final int version = Build.VERSION.SDK_INT;
		LogUtil.i(TAG, "version = " + version);
		if (version >= 19) {
			int op = 11;  // 为什么是11?看AppOpsManager.OP_POST_NOTIFICATION (数值)
			try {
				Class<?> clazz = AppOpsManager.class;
				Field field = clazz.getDeclaredField("OP_POST_NOTIFICATION");
				op = field.getInt(null);
				LogUtil.i(TAG, "op = " + op);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			return checkOp(context, op);
		} else {
			LogUtil.i(TAG, "permission = " + context.getApplicationInfo().permission);
			if ((context.getApplicationInfo().flags & (1 << 27)) == 1) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 判断某一权限是否打开
	 * @param context
	 * @param op       权限ID
	 * @return
	 */
	public static boolean checkOp(Context context, int op) {
		final int version = Build.VERSION.SDK_INT;
		if (version >= 19) {
			AppOpsManager manager = (AppOpsManager) context
					.getSystemService(Context.APP_OPS_SERVICE);
			try {
				Class<?> clazz = AppOpsManager.class;
				Method method = clazz.getMethod("checkOp", int.class, int.class, String.class);
				int permisson = (Integer) method.invoke(manager, op,
						Binder.getCallingUid(), context.getPackageName());
				LogUtil.i(TAG, "permission = " + permisson);
				if (AppOpsManager.MODE_ALLOWED == permisson) { // 这儿反射就自己写吧
					LogUtil.i(TAG, "allowed");
					return true;
				} else {
					LogUtil.i(TAG, "ignored");
				}
			} catch (Exception e) {
				LogUtil.w(TAG, e.getMessage());
			}
		} else {
			LogUtil.w(TAG, "Below API 19 cannot invoke!");
		}
		return false;
	}

	/**
	 * 根据包名打开应用
	 * @param context
	 * @param packageName
	 */
	public static void openApp(Context context, String packageName) {
		PackageInfo pi = null;
		try {
			pi = context.getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return;
		}

		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(pi.packageName);

		List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);

		ResolveInfo ri = apps.iterator().next();
		if (ri != null ) {
			String pkName = ri.activityInfo.packageName;
			String className = ri.activityInfo.name;

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			ComponentName cn = new ComponentName(pkName, className);

			intent.setComponent(cn);
			context.startActivity(intent);
		}
	}

	/**
	 * 根据包名找到启动页activity
	 * @param packageName
	 * @param context
	 * @return
	 */
	public static String launcherActivityName(String packageName, Context context) {
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageName);
		List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
		ResolveInfo ri = apps.iterator().next();
		String className = null;
		if (ri != null ) {
			className  = ri.activityInfo.name;
		}
		return className;
	}

    /**
     * 返回app运行状态
     * 1:程序在前台运行
     * 2:程序在后台运行
     * 3:程序未启动
     * 注意：需要配置权限<uses-permission android:name="android.permission.GET_TASKS" />
     */
    public static int getAppSatus(Context context, String pageName) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(20);

        //判断程序是否在栈顶
        if (list.get(0).topActivity.getPackageName().equals(pageName)) {
            return 1;
        } else {
            //判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(pageName)) {
                    return 2;
                }
            }
            return 3;//栈里找不到，返回3
        }
//    还可以通过下面代码判断程序的某个页面是否在运行
//    info.topActivity.getClassName().equals(activityName)
    }

    /**
     * app 是否处于运行状态
     * @param context
     * @param pageName
     * @return
     */
    public static boolean isAppRunning(Context context, String pageName) {
    	if (getAppSatus(context, pageName) == 1 || getAppSatus(context, pageName) == 2) {
			return true;
		} else {
			return false;
		}
    }

    /**
     * APP前台运行
     * @param context
     * @return
     */
    public static boolean isAppInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                return appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            }
        }
        return false;
    }

    /**
     *APP后台运行
     * @param context
     * @return
     */
    public static boolean isAppInBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                return appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
            }
        }
        return false;
    }

    /**
     * 判断service是否运行
     * @param mContext
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
        mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);

        if (serviceList.size() == 0) {
            return false;
        }

        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 解锁
     */
    public static void unlockScreen(Context context) {
        KeyguardManager km = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {//如果锁屏就解锁
            KeyguardManager.KeyguardLock keyguardLock = km.newKeyguardLock("");
            keyguardLock.disableKeyguard();
        }
    }

}
