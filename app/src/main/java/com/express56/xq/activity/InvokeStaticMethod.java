package com.express56.xq.activity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.BikeSite;
import com.express56.xq.model.RentInfo;
import com.express56.xq.receiver.MsgPushReceiver;
import com.express56.xq.service.UploadService;
import com.express56.xq.util.BitmapUtils;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.RGBLuminanceSource;
import com.express56.xq.util.SharedPreUtils;
import com.express56.xq.util.StringUtils;
import com.express56.xq.widget.ToastUtil;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import alibaba.fastjson.JSONArray;
import alibaba.fastjson.JSONObject;

/**
 * Created by SEELE on 2016/6/26.
 */
public class InvokeStaticMethod {

    private static final String TAG = InvokeStaticMethod.class.getSimpleName();


    public static void openHtmlLink(Activity activity, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
//       打开链接
//        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.imiyoo.com"));
//        it.setClassName("com.Android.browser", "com.android.browser.BrowserActivity");
//        activity.startActivity(it);
//        打开本地网页
//        Intent intent=new Intent();
//        intent.setAction("android.intent.action.VIEW");
//        Uri CONTENT_URI_BROWSERS = Uri.parse("content://com.android.htmlfileprovider/sdcard/123.html");
//        intent.setData(CONTENT_URI_BROWSERS);
//        intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
//        startActivity(intent);
    }

    /**
     * 创建应用所需文件和目录
     */
    public static void makeFiles() {
        File dir = new File(ExpressConstant.IMAGE_FILE_LOCATION_FOLDER_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        LogUtil.d(TAG, "dir.exists=" + dir.exists());

        File dir1 = new File(ExpressConstant.ORIGINAL_PHOTO_LOCATION_PATH);
        if (!dir1.exists()) {
            dir1.mkdirs();
        }
        LogUtil.d(TAG, "dir1.exists=" + dir1.exists());

        File dir2 = new File(ExpressConstant.COMPRESS_PHOTO_FILE_PATH);
        if (!dir2.exists()) {
            dir2.mkdirs();
        }
        LogUtil.d(TAG, "dir2.exists=" + dir2.exists());

    }

    /**
     * @param view
     * @param width
     */
    public static void setLayoutWidth(View view, int width) {
      /* MarginLayoutParams margin=new MarginLayoutParams(view.getLayoutParams());
       margin.setMargins(margin.leftMargin,y, margin.rightMargin, y+margin.height);
       //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
       //view.setLayoutParams(layoutParams);
       ViewGroup.MarginLayoutParams  layoutParams =newLayParms(view, margin);
       //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
       view.setLayoutParams(layoutParams);
       view.requestLayout();*/
        if (view.getParent() instanceof FrameLayout) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
            lp.width = width;
            view.setLayoutParams(lp);
            view.requestLayout();
        } else if (view.getParent() instanceof RelativeLayout) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
            lp.width = width;
            view.setLayoutParams(lp);
            view.requestLayout();
        } else if (view.getParent() instanceof LinearLayout) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
            lp.width = width;
            view.setLayoutParams(lp);
            view.requestLayout();
        }
    }

    /**
     * 信鸽绑定账号
     */
    public static void registerXGPush(Context context, String userID) {
        try {
            if (StringUtils.notEmpty(userID)) {
                // 开启logcat输出，方便debug，发布时请关闭
                // XGPushConfig.enableDebug(this, true);
                // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
                // 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
                // 具体可参考详细的开发指南
                // 传递的参数为ApplicationContext
//        XGPushManager.registerPush(context);
//            XGPushManager.registerPush(context, userID);
//                IntentFilter intentFilter = new IntentFilter();
//                intentFilter.addAction("com.express56.xq.UPDATE_LISTVIEW");
//                MsgPushReceiver receiver = new MsgPushReceiver();
//                context.registerReceiver(receiver, intentFilter);
                XGPushManager.registerPush(context, userID, new XGIOperateCallback() {
                    @Override
                    public void onSuccess(Object data, int flag) {
                        LogUtil.d("TPush", "注册成功，设备token为：" + data);
                    }

                    @Override
                    public void onFail(Object data, int errCode, String msg) {
                        LogUtil.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);

                    }
                });
//            try {
//                TelephonyManager tm = (TelephonyManager) getBaseContext()
//                        .getSystemService(Context.TELEPHONY_SERVICE);
//                String deviceId = tm.getDeviceId();
//                XGPushManager.registerPush(getApplicationContext(), phoneNumber);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 其它常用的API：
        // 绑定账号（别名）注册：registerPush(context,account)或registerPush(context,account, XGIOperateCallback)，
        // 其中account为APP账号，可以为任意字符串（qq、openid或任意第三方），业务方一定要注意终端与后台保持一致。
        // 取消绑定账号（别名）：registerPush(context,"*")，即account="*"为取消绑定，解绑后，该针对该账号的推送将失效
        // 反注册（不再接收消息）：unregisterPush(context)
        // 设置标签：setTag(context, tagName)
        // 删除标签：deleteTag(context, tagName)
    }

    /**
     * 文件拷贝
     *
     * @param fromFile
     * @param toFile
     * @return
     */
    public static boolean copySDcardFile(String fromFile, String toFile) {

        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 解除推送
     *
     * @param context
     */
    public static void unregisterXGPush(Context context) {
        XGPushManager.unregisterPush(context);
    }


    /**
     * 判断srcStr是否是json格式字符串
     *
     * @param srcStr
     * @return
     */
    public static boolean isNotJSONstring(Context context, String srcStr) {
        String start = srcStr.substring(0, 1);
        String end = srcStr.substring(srcStr.length() - 1);
//        LoisNotJSONstringgUtil.d(TAG, "start=" + start);
//        LogUtil.d(TAG, "end=" + end);
        if (srcStr.length() < 2) {
            return true;
        }
        if (!(start.equals("{") && end.equals("}"))
                && !(start.equals("[") && end.equals("]"))) {
            ToastUtil.showMessage(context, "返回数据格式错误");
            LogUtil.saveLogToSDCard(context.getClass().getSimpleName(), srcStr);
            return true;
        }
        return false;
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isGPSOpened(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        boolean passive = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 强制帮用户打开GPS
     *
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public static void openGPSSettings(Activity context, LocationListener listener) {
        LocationManager alm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "GPS模块正常", Toast.LENGTH_SHORT).show();
            getLocation(context, listener);
            return;
        }
        Toast.makeText(context, "请开启GPS！", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                Toast.makeText(context, "请打开设置页面", Toast.LENGTH_SHORT).show();
            }
        }
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        context.startActivityForResult(intent, 0); //此为设置完成后返回到获取界面
    }

    private static Location getLocation(Activity context, LocationListener listener) {
        // 获取位置管理服务
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) context.getSystemService(serviceName);
        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
        String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
        // 设置监听*器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米


        locationManager.requestLocationUpdates(provider, 3 * 1000, 1, listener);
        return location;
    }

    /**
     * 检测网络是否连接
     *
     * @return
     */
    public static boolean isWifiOpen(Context context) {
        boolean flag = false;
        //得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        if (flag) { //有网络连接
            NetworkInfo.State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if (gprs == NetworkInfo.State.CONNECTED || gprs == NetworkInfo.State.CONNECTING) {
                return false;
            }
            //判断为wifi状态
            if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                return true;
            }
        }
        return flag;
    }

    /**
     * 检测网络是否连接
     *
     * @return
     */
    public static boolean isNetWorkOpen(Context context) {
        boolean flag = false;
        //得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;
    }


//    /**
//     * 网络未连接时，调用设置方法
//     */
//    private void setNetwork(){
//        Toast.makeText(this, "wifi is closed!", Toast.LENGTH_SHORT).show();
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setIcon(R.drawable.ic_launcher);
//        builder.setTitle("网络提示信息");
//        builder.setMessage("网络不可用，如果继续，请先设置网络！");
//        builder.setPositiveButton("设置", new OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = null;
//                /**
//                 * 判断手机系统的版本！如果API大于10 就是3.0+
//                 * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
//                 */
//                if (android.os.Build.VERSION.SDK_INT > 10) {
//                    intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
//                } else {
//                    intent = new Intent();
//                    ComponentName component = new ComponentName(
//                            "com.android.settings",
//                            "com.android.settings.WirelessSettings");
//                    intent.setComponent(component);
//                    intent.setAction("android.intent.action.VIEW");
//                }
//                startActivity(intent);
//            }
//        });
//
//        builder.setNegativeButton("取消", new OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        builder.create();
//        builder.show();
//    }


    /**
     * 新版本APK文件下载路径
     *
     * @param path
     */
    public static void installApk(String path, Activity activity) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        activity.startActivityForResult(intent, ExpressConstant.REQUEST_INSTALL_APK_ID);
    }

    /**
     * 获取 view 的 尺寸
     *
     * @param view
     * @return
     */
    public static int[] getViewSize(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int height = view.getMeasuredHeight();
        int width = view.getMeasuredWidth();
        LogUtil.d(TAG, " " + view + " width = " + width + " height = " + height);
        return new int[]{width, height};
    }

//    /**
//     * Upload the picture file.
//     */
//    public void uploadFile() {
//        uploadSuccess = false;
//        String end = "\r\n";
//        String twoHyphens = "--";
//        String boundary = "*****";
//        try {
//            String uploadUrl = "CommConstants.BASE_URL"
//                    + "/FileUpload.ashx?userId=" + currentUserId
//                    + "&associateId=0&userName=";
//            if ("1".equals(this.uploadAvtar)) {
//                uploadUrl = uploadUrl + "&Avtar=1";
//            }
//            if ("0".equals(this.uploadIsCrop)) {
//                uploadUrl = uploadUrl + "&Pins=1";
//            }
//            URL url = new URL(uploadUrl);
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setDoInput(true);
//            con.setDoOutput(true);
//            con.setUseCaches(false);
//            con.setRequestMethod("POST");
//            con.setRequestProperty("Connection", "Keep-Alive");
//            con.setRequestProperty("Charset", "UTF-8");
//            con.setRequestProperty("Content-Type",
//                    "multipart/form-data;boundary=" + boundary);
//            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
//            ds.writeBytes(twoHyphens + boundary + end);
//            String fileName = picturePath.substring(
//                    picturePath.lastIndexOf("/") + 1, picturePath.length());
//            ds.writeBytes("Content-Disposition: form-data; "
//                    + "name=\"Filedata\";filename=\"" + fileName + "\"" + end);
//            ds.writeBytes(end);
//
//            FileInputStream fStream = new FileInputStream(finalPicturePath);
//
//            int bufferSize = 9024;
//            byte[] buffer = new byte[bufferSize];
//            int length = -1;
//            while ((length = fStream.read(buffer)) != -1) {
//                ds.write(buffer, 0, length);
//            }
//            ds.writeBytes(end);
//            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
//            fStream.close();
//            ds.flush();
//            int responseCode = con.getResponseCode();
//            InputStream is = con.getInputStream();
//            byte[] data = readInputStream(is);
//            final String uploadResult = new String(data);
//            if (responseCode == 200) {
//                resultJson = new org.json.JSONObject(uploadResult);
//                uploadSuccess = true;
//            }
//            ds.close();
//            con.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    public static String getPhotoPathFromContentUri(Context context, Uri uri) {
        String photoPath = "";
        if (context == null || uri == null) {
            return photoPath;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if (isExternalStorageDocument(uri)) {
                String[] split = docId.split(":");
                if (split.length >= 2) {
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        photoPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
            } else if (isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                photoPath = getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String[] split = docId.split(":");
                if (split.length >= 2) {
                    String type = split[0];
                    Uri contentUris = null;
                    if ("image".equals(type)) {
                        contentUris = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUris = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUris = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = MediaStore.Images.Media._ID + "=?";
                    String[] selectionArgs = new String[]{split[1]};
                    photoPath = getDataColumn(context, contentUris, selection, selectionArgs);
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            photoPath = uri.getPath();
        } else {
            photoPath = getDataColumn(context, uri, null, null);
        }

        return photoPath;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return null;
    }

    /**
     * 拷贝文件夹
     *
     * @param fromFile
     * @param toFile
     * @return
     */
    public static int copyFileDir(String fromFile, String toFile) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if (!root.exists()) {
            return -1;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();

        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        //遍历要复制该目录下的全部文件
        for (int i = 0; i < currentFiles.length; i++) {
            if (currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
            {
                copyFileDir(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");

            } else//如果当前项为文件则进行文件拷贝
            {
                copyFileToSDcard(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
            }
        }
        return 0;
    }


    /**
     * 文件拷贝
     *
     * @param fromFile
     * @param toFile
     * @return
     */
    public static boolean copyFileToSDcard(String fromFile, String toFile) {

        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 隐藏键盘
     *
     * @param context
     * @param view
     */
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean updateLocalBikePictrue(Object[] param, SharedPreUtils sp) {
        if (param[0] instanceof Bitmap
                && (Integer.parseInt(param[1].toString()) == RequestID.REQ_DOWNLOAD_PICTURE)) {
            //更新本地车辆图片
            if (TextUtils.isEmpty(sp.getString(ExpressConstant.BIKE_PIC_LOCAL_PATH_KEY))) {
                sp.setString(ExpressConstant.BIKE_PIC_LOCAL_PATH_KEY, ExpressConstant.BIKE_PIC_PATH);
            }
            File file = new File(sp.getString(ExpressConstant.BIKE_PIC_LOCAL_PATH_KEY));
            if (file.exists()) {
                file.delete();
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BitmapUtils.saveBitmapToFile((Bitmap) param[0], file);
            return true;
        }
        return false;
    }

    /**
     * 获取可以还车的停车港
     *
     * @param object
     */
    public static void getBikeSites(JSONObject object, RentInfo rentInfo) {
        JSONObject resultJson = object.getJSONObject("result");
        String content = resultJson.getString("bikesiteList");
        ArrayList<BikeSite> bikeSites = (ArrayList<BikeSite>) JSONArray.parseArray(content, BikeSite.class);
        LogUtil.d(TAG, "bikeSites = " + bikeSites);
        rentInfo.bikesiteList = bikeSites;
    }

    /**
     * 去除字符串中的Emoji表情
     *
     * @param source
     * @return
     */
    public static String removeEmoji(CharSequence source) {
        String result = "";
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (isEmojiCharacter(c)) {
                continue;
            }
            result += c;
        }
        return result;
    }


    /**
     * 判断一个字符串中是否包含有Emoji表情
     *
     * @param input
     * @return true 有Emoji
     */
    public static boolean isEmojiCharacter(CharSequence input) {
        for (int i = 0; i < input.length(); i++) {
            if (isEmojiCharacter(input.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是Emoji 表情
     *
     * @param codePoint
     * @return true 是Emoji表情
     */
    public static boolean isEmojiCharacter(char codePoint) {
        // Emoji 范围
        boolean isScopeOf = (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF) && (codePoint != 0x263a))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));

        return !isScopeOf;
    }

    public static String decode2(String imgPath) {
//        BufferedImage image = null;
        Result result = null;
        try {
//            image = ImageIO.read(new File(imgPath));
            Bitmap bitmap = BitmapUtils.getCompressedBitmapFromFile(new File(imgPath));
//            if (image == null) {
//                System.out.println("the decode image may be not exit.");
//            }
//            LuminanceSource source = new BufferedImageLuminanceSource(image);
//            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Hashtable<DecodeHintType, Object> decodeHints = new
                    Hashtable<DecodeHintType, Object>();
            decodeHints.put(DecodeHintType.CHARACTER_SET, "ISO-8859-1");
            decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
            RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
            result = new MultiFormatReader().decode(binaryBitmap, decodeHints);
            LogUtil.d(TAG, "barcode is !!!! " + result.getText());
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private com.google.zxing.Result parseQRcodeBitmap(String bitmapPath) {
        //解析转换类型UTF-8
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        //获取到待解析的图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        //如果我们把inJustDecodeBounds设为true，那么BitmapFactory.decodeFile(String path, Options opt)
        //并不会真的返回一个Bitmap给你，它仅仅会把它的宽，高取回来给你
        options.inJustDecodeBounds = true;
        //此时的bitmap是null，这段代码之后，options.outWidth 和 options.outHeight就是我们想要的宽和高了
        Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, options);
        //我们现在想取出来的图片的边长（二维码图片是正方形的）设置为400像素
        /**
         options.outHeight = 400;
         options.outWidth = 400;
         options.inJustDecodeBounds = false;
         bitmap = BitmapFactory.decodeFile(bitmapPath, options);
         */
        //以上这种做法，虽然把bitmap限定到了我们要的大小，但是并没有节约内存，如果要节约内存，我们还需要使用inSimpleSize这个属性
        options.inSampleSize = options.outHeight / 400;
        if (options.inSampleSize <= 0) {
            options.inSampleSize = 1; //防止其值小于或等于0
        }
        /**
         * 辅助节约内存设置
         *
         * options.inPreferredConfig = Bitmap.Config.ARGB_4444;    // 默认是Bitmap.Config.ARGB_8888
         * options.inPurgeable = true;
         * options.inInputShareable = true;
         */
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(bitmapPath, options);
        //新建一个RGBLuminanceSource对象，将bitmap图片传给此对象
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap);
        //将图片转换成二进制图片
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
        //初始化解析对象
        QRCodeReader reader = new QRCodeReader();
        //开始解析
        Result result = null;
        try {
            result = reader.decode(binaryBitmap, hints);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return result;
    }


    /**
     * 开启上传服务
     *
     * @param context
     */
    public static void startUploadService(final Context context) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                context.startService(new Intent(context, UploadService.class));//开启后台上传服务

            }
        }, 100);
    }

}
