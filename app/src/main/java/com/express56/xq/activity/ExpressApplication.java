package com.express56.xq.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import com.express56.xq.util.LogUtil;
import com.tencent.android.tpush.XGNotifaction;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushNotifactionCallback;

import java.util.List;

/**
 * Created by qzy on 2016/6/8.
 */
public class ExpressApplication extends Application {

    private static final String TAG = ExpressApplication.class.getSimpleName();
    private Context mContext;

    /**
     * 当前Acitity个数
     */
    private int activityAount = 0;

    public static boolean isForeground = false;


    @Override
    public void onCreate() {
        super.onCreate();
        InvokeStaticMethod.makeFiles();
//        //未捕获异常处理
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(this); // 传入参数必须为Activity，否则AlertDialog将不显示。
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        push();
    }

    private void push() {
        // 在主进程设置信鸽相关的内容
        if (isMainProcess()) {
            // 为保证弹出通知前一定调用本方法，需要在application的onCreate注册
            // 收到通知时，会调用本回调函数。
            // 相当于这个回调会拦截在信鸽的弹出通知之前被截取
            // 一般上针对需要获取通知内容、标题，设置通知点击的跳转逻辑等等
            XGPushManager
                    .setNotifactionCallback(new XGPushNotifactionCallback() {

                        @Override
                        public void handleNotify(XGNotifaction xGNotifaction) {
                            Log.i("test", "处理信鸽通知：" + xGNotifaction);
                            // 获取标签、内容、自定义内容
                            String title = xGNotifaction.getTitle();
                            String content = xGNotifaction.getContent();
                            String customContent = xGNotifaction
                                    .getCustomContent();
                            // 其它的处理
                            // 如果还要弹出通知，可直接调用以下代码或自己创建Notifaction，否则，本通知将不会弹出在通知栏中。
                            xGNotifaction.doNotify();
                        }
                    });
        }
    }

    private boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Activity 生命周期监听，用于监控app前后台状态切换
     */
    ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
//            if (activityAount == 0) {
//                //app回到前台
//                isForeground = true;
//            }
            activityAount++;
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityAount--;
            if (activityAount == 0) {
                PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
                boolean isScreenOn = pm.isScreenOn();
                if (isScreenOn) {
                    isForeground = false;
                }
                LogUtil.d(TAG, "background");
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    };

}
