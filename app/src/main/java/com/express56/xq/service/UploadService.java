package com.express56.xq.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import com.express56.xq.R;
import com.express56.xq.activity.InvokeStaticMethod;
import com.express56.xq.activity.LoginActivity;
import com.express56.xq.activity.PhotoActivity;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.db.BarcodeDataHelper;
import com.express56.xq.db.PhotoDataHelper;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.IHttpResponse;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.ExpressPhoto;
import com.express56.xq.util.FileUtil;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.NetUtils;
import com.express56.xq.util.SharedPreUtils;
import com.express56.xq.util.StringUtils;
import com.express56.xq.widget.CustomDialog;
import com.express56.xq.widget.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2016/10/31.
 */

public class UploadService extends Service implements IHttpResponse {

    private static final String TAG = UploadService.class.getSimpleName();
    private boolean isRunBarcode = true;
    private boolean isRunPhoto = true;

    private boolean canDoUploadBarcode = true;
    private boolean canDoUploadPhoto = true;
    private SharedPreUtils sp;
    ArrayList<String> list = null;

    public static boolean flag = true;//是否要执行上传操作
    public static boolean hasPhoto = true;//本地是否有待上传的图片

    public static BarcodeDataHelper barcodeDataHelper;
    public static PhotoDataHelper photoDataHelper;
    private ExpressPhoto expressPhoto;

    public static UploadService uploadService;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        LogUtil.d(TAG, "onCreate");
        super.onCreate();
        uploadService = this;

        sp = new SharedPreUtils(this);

        photoDataHelper = new PhotoDataHelper(this);
        photoDataHelper.open();
        barcodeDataHelper = new BarcodeDataHelper(this);
        barcodeDataHelper.open();

        ExpressPhoto expressPhoto = photoDataHelper.getExpressPhoto(sp.getUserInfo().phone);
        if (expressPhoto.photoFilePath == null
                || expressPhoto.photoFilePath.length() == 0
                || (expressPhoto.photo_type != 1 && expressPhoto.photo_type != 2)) {
            //启动后 本地数据库没有待上传的图片
            hasPhoto = false;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunBarcode) {
                    try {
                        //upload
                        uploadBarcodes();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunPhoto) {
                    try {
                        if (!hasPhoto && ExpressConstant.photoNumber == 0) {
                            flag = false;
                        } else {
                            flag = true;
                        }
                        if (flag) {
                            //upload
                            uploadPhotos();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy---------Service is closed ! ");
        super.onDestroy();
        uploadService = null;
        if (photoDataHelper != null) {
            photoDataHelper.close();
        }
        if (barcodeDataHelper != null) {
            barcodeDataHelper.close();
        }
    }

    /**
     * wifi 网络环境下 上传
     *
     * @throws InterruptedException
     */
    private void uploadBarcodes() throws InterruptedException {
//        LogUtil.d(TAG, "---------uploadBarcodes -----------");
//        if (!NetUtils.isWifi(getApplicationContext())) {
//            return;
//        }
        if (sp.getUserInfo() != null && canDoUploadBarcode && !StringUtils.isEmpty(sp.getUserInfo().token)) {
            canDoUploadBarcode = false;
            //get data from database
            ArrayList<String> barcodesList = getBarcodesFromDB();
            if (barcodesList.size() > 0) {
                String barcodesStr = JSON.toJSONString(barcodesList);
                if (!TextUtils.isEmpty(barcodesStr)) {
                    //上传
                    LogUtil.d(TAG, "---------uploadBarcodes -----------");
                    HttpHelper.sendRequest_uploadBarcodes(this, RequestID.REQ_UPLOAD_BARCODES, sp.getUserInfo().token, barcodesStr, null);
                } else {
                    canDoUploadBarcode = true;
                }
            } else {
                canDoUploadBarcode = true;
            }
            //  thread sleep
            Thread.sleep(ExpressConstant.UPLOAD_CHECK_INTERVAL);
        }
    }

    /**
     * wifi 网络环境下 上传图片
     *
     * @throws InterruptedException
     */
    private void uploadPhotos() throws Exception {
        LogUtil.d(TAG, "---------uploadPhotos -----------");
        if (!NetUtils.isWifi(getApplicationContext()) && ExpressConstant.isOnlyUploadWifi) {
            LogUtil.d(TAG, "---------uploadPhotos ---------1--");

            return;
        }
        LogUtil.d(TAG, "---------uploadPhotos -----2------");

        if (sp.getUserInfo() != null && canDoUploadPhoto && !StringUtils.isEmpty(sp.getUserInfo().token)) {
            LogUtil.d(TAG, "---------uploadPhotos ------3-----");

            canDoUploadPhoto = false;
            expressPhoto = photoDataHelper.getExpressPhoto(sp.getUserInfo().phone);
            if (expressPhoto == null) { //过三天未能上传 则从数据库中删除图片
                canDoUploadPhoto = true;
                return;
            }
            if (expressPhoto.photoFilePath == null
                    || expressPhoto.photoFilePath.length() == 0
                    || expressPhoto.upload_status == ExpressConstant.UPLOAD_STATUS_UPLOADING
                    || StringUtils.isEmpty(expressPhoto.photo_express_no)
                    || (expressPhoto.photo_type != 1 && expressPhoto.photo_type != 2)) {
                canDoUploadPhoto = true;
                return;
            }
            File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "photo.jpeg");
            if (file.exists()) {
                file.delete();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    canDoUploadPhoto = true;
                    e.printStackTrace();
                }
            }
//            file = FileUtil.createFileWithByte(expressPhoto.photoFilePath, "photo.jpeg");
            file = new File(expressPhoto.photoFilePath);
            if (FileUtil.getFileSize(file) > 0) {
                UploadService.photoDataHelper.updatePhotoUploadStatus(expressPhoto.photo_id,
                        sp.getUserInfo().phone, ExpressConstant.UPLOAD_STATUS_UPLOADING);//更新数据库记录上传状态
                HttpHelper.sendRequest_uploadPic(this, file, sp.getUserInfo().token,
                        RequestID.REQ_UPLOAD_PICTURE_AUTO, expressPhoto.photo_type,
                        expressPhoto.photo_express_no, expressPhoto.sender, expressPhoto.receiver, null);
            } else {
                canDoUploadPhoto = true;
            }
            Thread.sleep(ExpressConstant.UPLOAD_CHECK_PHOTO_INTERVAL);
        }
    }

    /**
     * 格式化后的条形码字符串
     */
    private String getBarcodesFormatString() {
        ArrayList<String> barcodesList = getBarcodesFromDB();
        HashMap<String, ArrayList<String>> map = new HashMap<>();
//        map.put("barcodes", barcodesList);
        String str = JSON.toJSONString(barcodesList);
//        StringBuilder sb = new StringBuilder();
//        if (barcodesList.size() > 0) {
//            sb.append("[");
//        }
//        for (int i = 0; i < barcodesList.size(); i++) {
//            sb.append("'").append(barcodesList.get(i)).append("'").append(",");
//        }
//        if (barcodesList.size() > 0) {
//            sb.deleteCharAt(sb.length() - 1).append("]");
//        }
        return str;
    }

    /**
     * 从数据库中取出所有条码
     */
    private ArrayList<String> getBarcodesFromDB() {
        list = barcodeDataHelper.getAll();
        return list;
    }

    @Override
    public void doHttpResponse(Object... param) {
        String result = (String) param[0];
        LogUtil.d(TAG, "response str=" + result);
        if (result == null) {
            resetUploadFlag(param[1]);
            if (param[2] != null && param[2] instanceof String) {
                String errMsg = (String) param[2];
                if (StringUtils.isEmpty(errMsg)) {//没有错误信息  弹出默认错误提示
                    errMsg = getString(R.string.str_net_request_fail);
                }
                Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (InvokeStaticMethod.isNotJSONstring(this, result)) {
            resetUploadFlag(param[1]);
            return;
        }
        JSONObject object = JSON.parseObject(result);
        if (object == null) {
            resetUploadFlag(param[1]);
            ToastUtil.showMessage(this, "返回数据异常", false);
            return;
        }
        switch (Integer.parseInt(param[1].toString())) {
            case RequestID.REQ_UPLOAD_BARCODES:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            //上传成功后，从数据库中删除记录
                            if (list != null && list.size() > 0) {
                                barcodeDataHelper.deleteByBarcode(list);
//                                ToastUtil.showMessage(this, "条码上传成功");
                            }
                        }
                    } else {
                        if (code == 0) {
                            //token 过期
                            showReloginDialog();
                        } else {
                             //条码上传失败
//                            showErrorMsg(object);
                        }
                    }
                    canDoUploadBarcode = true;
                }
                break;
            case RequestID.REQ_UPLOAD_PICTURE_AUTO:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            photoDataHelper.deletePhotoByID(expressPhoto.photo_id, sp.getUserInfo().phone);
                            PhotoActivity.photoActivity.refreshAfterDelete(expressPhoto.photo_id);
//                            ToastUtil.showMessage(this, "上传成功");
                            if (ExpressConstant.photoNumber > 0) {
                                ExpressConstant.photoNumber -= 1;
                            }
                        }
                    } else if (code == 0) {
                        UploadService.photoDataHelper.updatePhotoUploadStatus(expressPhoto.photo_id, sp.getUserInfo().phone, ExpressConstant.UPLOAD_STATUS_NORMAL);//更新数据库记录上传状态
                        //token 过期
                        showReloginDialog();
                    } else {
                        UploadService.photoDataHelper.updatePhotoUploadStatus(expressPhoto.photo_id, sp.getUserInfo().phone, ExpressConstant.UPLOAD_STATUS_NORMAL);//更新数据库记录上传状态
                        photoDataHelper.updateUploadFailTime(expressPhoto.photo_id);
//                        showErrorMsg(object);
                    }
                    canDoUploadPhoto = true;
                }
                break;
            default:
                break;
        }
    }

    private void resetUploadFlag(Object o) {
        if (Integer.parseInt(o.toString()) == RequestID.REQ_UPLOAD_BARCODES) {
            canDoUploadBarcode = true;
        } else {
            canDoUploadPhoto = true;
        }
    }

    /**
     * 显示网络请求错误信息
     *
     * @param object
     */
    protected void showErrorMsg(JSONObject object) {
        if (object != null && object.containsKey("result")) {
            String content = object.getString("result");
            ToastUtil.showMessage(this, content, true);
        }
    }

    /**
     * 重新登录
     */
    protected void showReloginDialog() {
        sp.remove("userInfo");
        String prompt = getString(R.string.str_dialog_prompt_token_expire);
        String confirm = getString(R.string.str_dialog_btn_to_ok);
        String cancel = "";
        final CustomDialog customDialog = new CustomDialog(getApplicationContext(), prompt, confirm, cancel, 60);
        customDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                customDialog.dismiss();
//                startActivity(new Intent(UploadService.this, LoginActivity.class));
                Intent dialogIntent = new Intent(getBaseContext(), LoginActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(dialogIntent);
            }

            @Override
            public void doCancel() {
                customDialog.dismiss();
            }
        });
    }

    @Override
    public void doHttpCanceled(Object... param) {

    }



}
