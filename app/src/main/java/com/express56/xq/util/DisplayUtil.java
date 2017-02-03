package com.express56.xq.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtil {

    public static final String TAG = DisplayUtil.class.getSimpleName();

    public static int screenWidth;
    public static int screenHeight;
    public static float density;

    public static int getPxByDP(int i ,Activity activity) {
        if (density == 0 ){
            ini(activity);
        }
        return (int)(i*density);
    }

    public static int getPxByDP(int i ,Context activity) {
        return getPxByDP(i,(Activity)activity);
    }
    public static void ini(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(
                displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight =  displayMetrics.heightPixels;
        density = displayMetrics.density;
        LogUtil.d(TAG, "density" + density);
    }

}
