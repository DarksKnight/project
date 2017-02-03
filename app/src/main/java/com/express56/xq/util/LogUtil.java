package com.express56.xq.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志类
 */
public class LogUtil {

    private static final boolean Debug = true;

    private static final int INFO = 1;
    private static final int DEBUG = 2;
    private static final int WARNING = 3;
    private static final int VERBOSE = 4;
    private static final int ERROR = 5;

    public static void i(String tag, String msg) {
        if (Debug && INFO > 0 && notFilter(tag)) {
            Log.i(tag, msg);
        }
    }

    private static boolean notFilter(String tag) {
//        if ("NoticeService".equals(tag)){
//            return false;
//        }
        return true;
    }

    public static void d(String tag, String msg) {
        if (Debug && DEBUG > 0 && notFilter(tag)) {
            Log.d(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (Debug && VERBOSE > 0) {
            Log.v(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (Debug && ERROR > 0) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        if (Debug && ERROR > 0) {
            Log.e(tag, msg, t);
        }
    }

    public static void w(String tag, String msg) {
        if (Debug && WARNING > 0) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, Throwable t) {
        if (Debug && WARNING > 0) {
            Log.w(tag, t);
        }
    }

    public static void saveLogToSDCard(String tag, String msg) {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            String date = sDateFormat.format(new java.util.Date());
            String file = Environment.getExternalStorageDirectory().getPath() + File.separator + "express" + date + ".txt";
            java.io.File SDFile = new java.io.File(file);
            if (!SDFile.exists()) {
                try {
                    SDFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                FileOutputStream outputStream = new FileOutputStream(file, true);
                outputStream.write((new Date().toLocaleString() + "[" + tag + "]" + ": " + msg + "\r\n").getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
