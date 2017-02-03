package com.express56.xq.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Reader
 * 写操作类
 *
 */
public class WriterUtil {

	/**
	 *  写入SDCard
	 *  @param dir sd卡里面的目录，如/sdcard/weibo/temp，注意要带上/sdcard/
	 * @param fileName  想要保存的文件名，如icon.png/hello.txt
	 * @param data
	 */
	public static boolean writeToSDCard(byte[] data, String dir, String fileName) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//			File SDFile = android.os.Environment.getExternalStorageDirectory(); //其中“/sdcard/”相当于
//			String path = SDFile.getAbsolutePath() + File.separator+dir + File.separator+ fileName;
			//检测目录是否存在，不存在则创建
			File file = new File(dir);
			if (!file.exists()) {
				file.mkdirs();
			}
			//要保存的文件是否存在，不存在则创建
			String path = dir + File.separator + fileName;
			file = new File(path);
			FileOutputStream os = null;
			try {
				if (!file.exists()) {
					file.createNewFile();
				}
				os = new FileOutputStream(file);
				os.write(data);
				os.close();
				return true;
			}catch (IOException e) {
				e.printStackTrace();
				return false;
			}finally {  //qiuzy  关闭流
				try {
					if (os != null) {
						os.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			LogUtil.e("Writer", ">>>>>>SDCard不可用, 或者没有读写权限.<<<<<<");
			return false;
		}
	}

	public static boolean writeToSDCard(Bitmap bitmap, String dir, String fileName) {
		if (writeToSDCard(bitmap, dir, fileName, CompressFormat.JPEG, 100)) {
			return true;
		}else {
			return false;
		}
	}

	public static boolean writeGifToSDCard(Bitmap bitmap, String dir, String fileName) {
		if (writeToSDCard(bitmap, dir, fileName, CompressFormat.PNG, 100)) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 *  写入SDCard
	 *  @param dir sd卡里面的目录，如/sdcard/weibo/temp，注意要带上/sdcard/
	 * @param fileName  想要保存的文件名，如icon.png/hello.txt
	 * @param quality 图片的质量
	 */
	public static boolean writeToSDCard(Bitmap bitmap, String dir, String fileName, CompressFormat compressFormat, int quality) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			if (bitmap == null) {
				return false;
			}
			//检测目录是否存在，不存在则创建
			File file = new File(dir);
			if (!file.exists()) {
				file.mkdirs();
			}
			//要保存的文件是否存在，不存在则创建
			String path = dir + File.separator + fileName;
			LogUtil.d("Writer", "file sava path="+path);
			file = new File(path);
			FileOutputStream os = null;
			BufferedOutputStream bos = null;
			try {
				if (!file.exists()) {
					file.createNewFile();
				}
				os = new FileOutputStream(file);
				bos = new BufferedOutputStream(os);
				bitmap.compress(compressFormat, quality, bos);
				bos.flush();
				bos.close();
				os.close();
				return true;
			}catch (IOException e) {
				e.printStackTrace();
				return false;
			}finally {//qiuzy  关闭流
				try {
					if (bos != null) {
						bos.close();
					}
					if (os != null) {
						os.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			LogUtil.e("Writer", ">>>>>>SDCard不可用, 或者没有读写权限.<<<<<<");
			return false;
		}
	}

	public static boolean writeToSDCard(File file1 , String dir, String fileName, CompressFormat compressFormat, int quality) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			//检测目录是否存在，不存在则创建
			File file = new File(dir);
			if (!file.exists()) {
				file.mkdirs();
			}
			//要保存的文件是否存在，不存在则创建
			String path = dir + File.separator + fileName;
			LogUtil.d("Writer", "file sava path="+path);
			file = new File(path);
			FileOutputStream os = null;
			BufferedOutputStream bos = null;
			try {
				if (!file.exists()) {
					file.createNewFile();
				}
				os = new FileOutputStream(file);
				bos = new BufferedOutputStream(os);
				FileInputStream in = new FileInputStream(file1);
				byte[] buf = new byte[1024];
				int length = 0;
				while ((length = in.read(buf)) != -1) {
					bos.write(buf, 0, length);
				}
				in.close();
//				bitmap.compress(compressFormat, quality, bos);
				bos.flush();
				bos.close();
				os.close();
				return true;
			}catch (IOException e) {
				e.printStackTrace();
				return false;
			}finally {//qiuzy  关闭流
				try {
					if (bos != null) {
						bos.close();
					}
					if (os != null) {
						os.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			LogUtil.e("Writer", ">>>>>>SDCard不可用, 或者没有读写权限.<<<<<<");
			return false;
		}
	}
	public static boolean writeToCache(Bitmap bitmap, Context context, String fileName) {
		if(writeToCache(bitmap, context, fileName, CompressFormat.JPEG, 100)){
			return true;
		}else {
			return false;
		}
	}

	public static boolean writeToCache(Bitmap bitmap, Context context, String fileName, CompressFormat compressFormat, int quality) {
//			//检测目录是否存在，不存在则创建
//			String cachePath = context.getCacheDir().toString();
//			File file = new File(cachePath);
//			if (!file.exists()) {
//				file.mkdirs();
//			}
			//要保存的文件是否存在，不存在则创建
			String path = context.getCacheDir() + File.separator + fileName;
			LogUtil.d("Writer", "file sava path="+path);
			File file = new File(path);
			FileOutputStream os = null;
			BufferedOutputStream bos = null;
			try {
				if (file.exists()) {//删除旧的
					file.delete();
				}
				file.createNewFile();
				os = new FileOutputStream(file);
				bos = new BufferedOutputStream(os);
				bitmap.compress(compressFormat, quality, bos);
				bos.flush();
				bos.close();
				os.close();
				return true;
			}catch (IOException e) {
				e.printStackTrace();
				return false;
			}finally {//qiuzy  关闭流
				try {
					if(bos != null) {
						bos.close();
					}
					if (os != null) {
						os.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}

	public static boolean writeToFiles(Bitmap bitmap, Context context, String fileName, CompressFormat compressFormat, int quality) {
		//要保存的文件是否存在，不存在则创建
		String path = context.getFilesDir() + File.separator + fileName;
		LogUtil.d("Writer", "file sava path="+path);
		File file = new File(path);
		FileOutputStream os = null;
		BufferedOutputStream bos = null;
		try {
			if (file.exists()) {//删除旧的
				file.delete();
			}
			file.createNewFile();
			os = new FileOutputStream(file);
			bos = new BufferedOutputStream(os);
			bitmap.compress(compressFormat, quality, bos);
			bos.flush();
			bos.close();
			os.close();
			return true;
		}catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally {//qiuzy  关闭流
			try {
				if(bos != null) {
					bos.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean writeToFiles(File file1, Context context, String fileName, CompressFormat compressFormat, int quality) {
		//要保存的文件是否存在，不存在则创建
		String path = context.getFilesDir() + File.separator + fileName;
		LogUtil.d("Writer", "file sava path="+path);
		File file = new File(path);
		FileOutputStream os = null;
		BufferedOutputStream bos = null;
		try {
			if (file.exists()) {//删除旧的
				file.delete();
			}
			file.createNewFile();
			os = new FileOutputStream(file);
			bos = new BufferedOutputStream(os);
			FileInputStream in = new FileInputStream(file1);
			byte[] buf = new byte[1024];
			int length = 0;
			while ((length = in.read(buf)) != -1) {
				bos.write(buf, 0, length);
			}
			in.close();
			bos.flush();
			bos.close();
			os.close();
			return true;
		}catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally {//qiuzy  关闭流
			try {
				if(bos != null) {
					bos.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 以SharedPreferences存到内存(ROM)
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void writeToSharedPreferences(Context context, String key, String value) {
		SharedPreferences preferences = context.getSharedPreferences(key,0);
		Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 对象序列化(该对象要实现Serializable接口)，保存到内存(ROM)
	 */
	public static boolean writeBySerializable(Context context, String key, Object object) {
		FileOutputStream out = null;
		ObjectOutputStream oos = null;
		try {
			out = context.openFileOutput(key, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(out);
	        oos.writeObject(object);
	        out.close();
	        oos.close();
	        return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {//qiuzy  关闭流
			try {
				if (oos != null) {
					oos.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 效率最高，写到RAM里
	 * { MemoryFile通过将 NAND或SD卡上的文件，分段映射到内存中进行修改处理，
	 * 这样就用高速的RAM代替了ROM或SD卡，性能自然提高不少，对于Android手机
	 * 而言同时还减少了电量消耗。from: http://www.android123.com.cn/androidkaifa/440.html}
	 */
	public static void writeToMemoryFile() {
//		MemoryFile memoryFile = new MemoryFile("name", length);
	}

}
