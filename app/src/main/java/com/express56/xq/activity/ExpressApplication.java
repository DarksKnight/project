package com.express56.xq.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;

import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ToastUtil;

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
