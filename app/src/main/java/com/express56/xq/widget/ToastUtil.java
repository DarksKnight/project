package com.express56.xq.widget;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Toast util class.
 * from internet
 * 目前支持的显示方位： 1.屏幕居中显示  2.屏幕底部显示
 */
public class ToastUtil {
    private static final String TAG = ToastUtil.class.getSimpleName();

    private static Handler handler = new Handler(Looper.getMainLooper());

    private static Toast toast = null;

    private static Object synObj = new Object();

    public static void showMessageLong(final Context act, final String msg) {
        showMessage(act, msg, Toast.LENGTH_LONG, false, 40);
    }

    public static void showMessage(final Context act, final String msg) {
        showMessage(act, msg, Toast.LENGTH_SHORT, false, 40);
    }

    public static void showMessage(final Context act, final int msg) {
        showMessage(act, msg, Toast.LENGTH_SHORT, false, 0);
    }

    public static void showMessage(final Context act, final String msg, boolean showInCenter) {
        showMessage(act, msg, Toast.LENGTH_SHORT, showInCenter, 40);
    }

    public static void showMessage(final Context act, final int msg, boolean showInCenter) {
        showMessage(act, msg, Toast.LENGTH_SHORT, showInCenter, 0);
    }

    public static void showMessage(final Context act, final String msg, boolean showInCenter, int offsetY_ShowBottom) {
        showMessage(act, msg, Toast.LENGTH_SHORT, showInCenter, offsetY_ShowBottom);
    }

    public static void showMessage(final Context act, final int msg, boolean showInCenter, int offsetY_ShowBottom) {
        showMessage(act, msg, Toast.LENGTH_SHORT, showInCenter, offsetY_ShowBottom);
    }

    public static void showMessageTop(final Context act, final String msg,
                                      final int len, final int offsetY) {
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
//                                toast.cancel();

                                LinearLayout linearLayout = (LinearLayout) toast.getView();
                                TextView messageTextView = (TextView) linearLayout.getChildAt(0);
                                messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

                                toast.setText(msg);
                                toast.setDuration(len);
                                toast.setGravity(Gravity.TOP, 0, offsetY);
                            } else {
                                toast = Toast.makeText(act, msg, len);

                                LinearLayout linearLayout = (LinearLayout) toast.getView();
                                TextView messageTextView = (TextView) linearLayout.getChildAt(0);
                                messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

                                toast.setGravity(Gravity.TOP, 0, offsetY);
                            }
                            toast.show();

                        }
                    }
                });
            }
        }).start();
    }

    private static void showMessage(final Context act, final String msg,
                                    final int len, final boolean showInCenter, final int offsetY) {
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
//                                toast.cancel();
                                toast.setText(msg);
                                toast.setDuration(len);
                                if (showInCenter) {
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                } else {
                                    toast.setGravity(Gravity.BOTTOM, 0, offsetY);
                                }
                            } else {
                                toast = Toast.makeText(act, msg, len);
                                if (showInCenter) {
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                } else {
                                    toast.setGravity(Gravity.BOTTOM, 0, offsetY);
                                }
                            }
                            toast.show();

                        }
                    }
                });
            }
        }).start();
    }


    private static void showMessage(final Context act, final int msg,
                                    final int len, final boolean showInCenter, final int offsetY) {
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
                                toast.cancel();
                                toast.setText(msg);
                                toast.setDuration(len);
                                if (showInCenter) {
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                } else {
                                    toast.setGravity(Gravity.BOTTOM, 0, offsetY);
                                }
                            } else {
                                toast = Toast.makeText(act, msg, len);
                            }
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }

}
