package com.express56.xq.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.model.Bike;
import com.express56.xq.model.User;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SharedPreUtils {
	private static final String TAG = SharedPreUtils.class.getSimpleName();
	private SharedPreferences sp;

	public SharedPreUtils(Context paramContext) {
		this.sp = paramContext.getSharedPreferences(ExpressConstant.LOGIN_SET,
				Context.MODE_PRIVATE);

	}

	public String getString(String key) {
		return this.sp.getString(key, "");
	}

	public void setString(String key, String value) {
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public int getInteger(String key) {
		return this.sp.getInt(key, -1);
	}

	public void setInteger(String key, int value) {
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public boolean getBoolean(String key, boolean bool) {
		return this.sp.getBoolean(key, bool);
	}

	public void setBoolean(String key, Boolean value) {
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public Set<String> getStringSet(String key) {
		return this.sp.getStringSet(key, new HashSet<String>());
	}

	public void setStringSet(String key, Set<String> value) {
		Editor editor = sp.edit();
		editor.putStringSet(key, value);
		editor.commit();
	}

	public void remove(String key) {
		Editor editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}

	public long getLong(String key) {
		return this.sp.getLong(key, -1);
	}

	public void setLong(String key, long value) {
		Editor editor = sp.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public boolean setArray(ArrayList<String> list, String keyStr) {
		if (list == null) {
			return false;
		}
		Editor editor = sp.edit();
		editor.putInt(keyStr, list.size());
		for(int i = 0; i < list.size(); i++) {
			editor.remove(keyStr + "_" + i);
			editor.putString(keyStr + "_" + i, list.get(i));
		}
		return editor.commit();
	}

	public ArrayList<String> getArray(String keyStr) {
		ArrayList<String> list = new ArrayList<String>();
		int size = sp.getInt(keyStr, 0);
		for(int i = 0; i < size; i++) {
			list.add(sp.getString(keyStr + "_" + i, null));
		}
		return list;
	}

    /**
     * 保存用户从IM loginVerify接口返回的数据构造的UserInfo对象类
     * @param userInfo
     */
    public void saveUserInfo(User userInfo) {
        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(userInfo);
            // 将字节流编码成base64的字符窜
            String oAuth_Base64 = new String(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
            Editor editor = sp.edit();
            editor.putString("userInfo", oAuth_Base64);

            editor.commit();
        } catch (IOException e) {
            LogUtil.i(TAG, "save userInfo fail");
        }
        LogUtil.i(TAG, "save userInfo success");
    }

    /**
     * 获取用户信息
     * @return
     */
    public User getUserInfo() {
		User userInfo = new User();
        String productBase64 = sp.getString("userInfo", "");
//        LogUtils.v(TAG, "getUserInfo productBase64= " + productBase64);
        if (productBase64 == null || productBase64.isEmpty()) {
			return null;
		}
        //读取字节
        byte[] base64 = Base64.decode(productBase64.getBytes(), Base64.DEFAULT);

        //封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            //再次封装
            ObjectInputStream bis = new ObjectInputStream(bais);
            try {
                //读取对象
                userInfo = (User) bis.readObject();
//                LogUtil.v(TAG, "getUserInfo success!");
            } catch (ClassNotFoundException e) {
				LogUtil.v(TAG, "getUserInfo fail!");
                e.printStackTrace();
            }
        } catch (StreamCorruptedException e) {
			LogUtil.v(TAG, "getUserInfo fail!");
            e.printStackTrace();
        } catch (IOException e) {
			LogUtil.v(TAG, "getUserInfo fail!");
            e.printStackTrace();
        }
//		LogUtil.v(TAG, "getUserInfo userInfo= " + userInfo);
        return userInfo;
    }

	/**
	 * 保存用户从IM loginVerify接口返回的数据构造的UserInfo对象类
	 * @param bike
	 */
	public void saveBike(Bike bike) {
		// 创建字节输出流
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			// 创建对象输出流，并封装字节流
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			// 将对象写入字节流
			oos.writeObject(bike);
			// 将字节流编码成base64的字符窜
			String oAuth_Base64 = new String(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
			Editor editor = sp.edit();
			editor.putString("bike", oAuth_Base64);
			editor.commit();
		} catch (IOException e) {
			LogUtil.i(TAG, "save bike fail");
		}
		LogUtil.i(TAG, "save bike success");
	}

	/**
	 * 获取用户信息
	 * @return
	 */
	public Bike getBike() {
		Bike bike = new Bike();
		String productBase64 = sp.getString("bike", "");
//        LogUtils.v(TAG, "getUserInfo productBase64= " + productBase64);
		if (productBase64 == null || productBase64.isEmpty()) {
			return null;
		}
		//读取字节
		byte[] base64 = Base64.decode(productBase64.getBytes(), Base64.DEFAULT);

		//封装到字节流
		ByteArrayInputStream bais = new ByteArrayInputStream(base64);
		try {
			//再次封装
			ObjectInputStream bis = new ObjectInputStream(bais);
			try {
				//读取对象
				bike = (Bike) bis.readObject();
				LogUtil.v(TAG, "get bike success!");
			} catch (ClassNotFoundException e) {
				LogUtil.v(TAG, "get bike fail!");
				e.printStackTrace();
			}
		} catch (StreamCorruptedException e) {
			LogUtil.v(TAG, "get bike fail!");
			e.printStackTrace();
		} catch (IOException e) {
			LogUtil.v(TAG, "get bike fail!");
			e.printStackTrace();
		}
		LogUtil.v(TAG, "get bike = " + bike);
		return bike;
	}

}