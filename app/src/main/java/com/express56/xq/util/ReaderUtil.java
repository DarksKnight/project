package com.express56.xq.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Reader
 * 读操作类
 *
 */
public class ReaderUtil {

	/**
	 * 从SD卡读取文件
	 * @param path
	 * @param name
	 * @return
	 */
	public static File readFromSDCard(String path, String name) {
		File file = new File(path + File.separator + name);
		if (file.exists()) {
			return file;
		}else {
			LogUtil.i("Reader", "readFromSDCard File not found!");
			return null;
		}
	}

	/**
	 * 从SD卡读取Bitmap
	 * @param path
	 * @param name
	 * @return
	 */
	public static Bitmap readBitmapFromSDCard(String path, String name) {
		String filePath = path + File.separator + name;
		File file = new File(filePath);
		if (file.exists()) {
			Bitmap b = null;
			try {
				b = BitmapFactory.decodeFile(filePath);
				LogUtil.i("Reader", "reading Bitmap FromSDCard ");
			} catch (OutOfMemoryError e) {
				LogUtil.w("Reader", "---OutOfMemoryError---");
				System.gc();
				try {Thread.sleep(2000);} catch (InterruptedException e1) {}
				try {
					b = BitmapFactory.decodeFile(filePath);
				} catch (OutOfMemoryError e1) {}
			}
			return b;
		}else {
			LogUtil.i("Reader", "readBitmapFromSDCard File not found!");
			return null;
		}
	}

	/**
	 * 从ROM cache读取文件
	 * @param context
	 * @param name
	 * @return
	 */
	public static File readFromCache(Context context, String name) {
		File file = new File(context.getCacheDir() + File.separator + name);
		if (file.exists()) {
			return file;
		}else {
			LogUtil.i("Reader", " readFromCache File not found!");
			return null;
		}
	}

	/**
	 * 从ROM files 读取文件
	 * @param context
	 * @param name
	 * @return
	 */
	public static File readFromFiles(Context context, String name) {
		File file = new File(context.getFilesDir() + File.separator + name);
		if (file.exists()) {
			return file;
		}else {
			LogUtil.i("Reader", " readFromFiles File not found!");
			return null;
		}
	}

	/**
	 * 从 ROM cache卡读取文件返回bitmap
	 * @param context
	 * @param name
	 * @return
	 */
	public static Bitmap readBitmapFromCache(Context context, String name) {
		String filePath = context.getCacheDir() + File.separator + name;
		File file = new File(filePath);
		if (file.exists()) {
			LogUtil.i("Reader", "reading Bitmap from cache ");
			Bitmap b = null;
			try {
				b = BitmapFactory.decodeFile(filePath);
				LogUtil.i("Reader", "reading Bitmap FromSDCard ");
			} catch (OutOfMemoryError e) {
				LogUtil.w("Reader", "---OutOfMemoryError---");
				System.gc();
				try {Thread.sleep(2000);} catch (InterruptedException e1) {}
				try {
					b = BitmapFactory.decodeFile(filePath);
				} catch (OutOfMemoryError e1) {}
			}
			return b;
		}else {
			LogUtil.i("Reader", "File not found in cache!");
			return null;
		}
	}

	/**
	 * 从SharedPreferences读取
	 * @param context
	 * @param key
	 * @return
	 */
	public static String readFromSharedPreferences(Context context, final String key) {
		SharedPreferences preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
		return preferences.getString(key, "");
	}

	public static String readAppVersionSharedPreferences(Context context, final String key) {
		SharedPreferences preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
		return preferences.getString(key, "");
	}

	public static int readRegCountSharedPreferences(Context context, String key){
		SharedPreferences preferences = context.getSharedPreferences("registCount", 0);
		 String count = preferences.getString(key, "0");
		 return Integer.parseInt(count);
	}



	/**
	 * 读取内存中序列化的对象
	 * @param context
	 * @param key
	 * @return
	 */
	public static Object readFromSerializable(Context context, final String key) {
		FileInputStream in = null;
		ObjectInputStream inputStream = null;
        try {
            in = context.openFileInput(key);
            if (in != null) {
                inputStream = new ObjectInputStream(in);
                return inputStream.readObject();
			}
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally{
    		try {
    			if (in != null) {
    				in.close();
    			}
    			if (inputStream != null) {
    				inputStream.close();
    			}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
}
