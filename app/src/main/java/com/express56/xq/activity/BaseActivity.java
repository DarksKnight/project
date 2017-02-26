package com.express56.xq.activity;

import com.express56.xq.R;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.IHttpResponse;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.PersonalInfo;
import com.express56.xq.model.User;
import com.express56.xq.model.VersionInfo;
import com.express56.xq.okhttp.OkHttpUtils;
import com.express56.xq.util.DialogUtils;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.PermissionUtil;
import com.express56.xq.util.SharedPreUtils;
import com.express56.xq.widget.CustomDialog;
import com.express56.xq.widget.ToastUtil;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2016/5/22.
 */
public class BaseActivity extends FragmentActivity implements IHttpResponse {

    public Dialog getDialog() {
        return dialog;
    }

    protected Dialog dialog;

    protected Activity context;

    protected SharedPreUtils sp;
    protected User user = null;

    protected VersionInfo versionInfo;
    protected String download_apk_url;

    protected PersonalInfo personalInfo;

    /**
     * 第一次点击操作时间
     */
    protected long first_click_time = 0;

    protected long clickFirstTime = 0;
    private static String TAG = BaseActivity.class.getSimpleName();

    protected ImageView btnBack = null;

    public void setBackKeyActionEnable(boolean canBackKeyAction) {
        this.canBackKeyAction = canBackKeyAction;
    }

    private boolean canBackKeyAction = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        first_click_time = 0;
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads()
//                .detectDiskWrites()
//                .detectAll()   // or .detectAll() for all detectable problems
//                .penaltyLog()
//                .build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects()
//                .detectLeakedClosableObjects()
//                .penaltyLog()
//                .penaltyDeath()
//                .build());
        context = this;
        dialog = DialogUtils.createLoadingDialog(this, false);

//        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
//        StrictMode.ThreadPolicy readPolicy = StrictMode.allowThreadDiskReads();
//        // 从磁盘读取数据
//        StrictMode.setThreadPolicy(oldPolicy);
//        StrictMode.setThreadPolicy(readPolicy);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !canBackKeyAction) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void init() {
        initView();
        initData();
    }

    protected void initData() {
        clickFirstTime = 0;
        ExpressConstant.isOnlyUploadWifi = sp.getBoolean("is_upload_only_wifi", true);
    }

    protected void initView() {
        sp = new SharedPreUtils(this);
        user = sp.getUserInfo();
        btnBack = getView(R.id.search_back_btn);

        if (null != btnBack) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    /**
     * @param id
     * @return
     */
    protected Drawable getMyDrawable(int id) {
        return ContextCompat.getDrawable(context, id);
    }

    @Override
    protected void onResume() {
        super.onResume();

        XGPushClickedResult click = XGPushManager.onActivityStarted(this);
        LogUtil.d("TPush", "onResumeXGPushClickedResult:" + click);
//        if (click != null) { // 判断是否来自信鸽的打开方式
//            Toast.makeText(this, "通知被点击:" + click.toString(),
//                    Toast.LENGTH_SHORT).show();
//        }
    }

    protected boolean checkFastClick() {
        long current_time = System.currentTimeMillis();
        if (current_time - first_click_time < ExpressConstant.DOUBLE_CLICK_TIME_INTERVAL) {
            return true;
        }
        first_click_time = current_time;
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        XGPushManager.onActivityStoped(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
        dialog = null;
        OkHttpUtils.getInstance().cancelTag(this);//取消页面所有相关请求
    }

    @Override
    public void doHttpResponse(Object... param) {

    }

    @Override
    public void doHttpCanceled(Object... param) {

    }

    protected void exitApp() {
        android.os.Process.killProcess(android.os.Process.myPid());//获取PID
        System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
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
     * 显示网络请求错误信息
     *
     * @param object
     */
    protected void showErrorMsg(JSONObject object, String msg) {
        if (object.containsKey("error")) {
            String errorMsg = object.getString("error");
            errorMsg = JSON.parseObject(errorMsg).getString("message");
            ToastUtil.showMessage(this, errorMsg, true);
        } else {
            ToastUtil.showMessage(this, msg, true);
        }
    }

    private static final int REQUEST_CODE_WRITE_SETTINGS = 2;

    protected void requestWriteSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(context)) {
                LogUtil.i(TAG, "onActivityResult write settings granted 1");
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);

//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                intent.setData(Uri.parse("package:" + getPackageName()));
            }
        } else {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //SYSTEM_ALERT_WINDOW，设置悬浮窗
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (Settings.System.canWrite(this)) {
                    LogUtil.i(TAG, "onActivityResult write settings granted");
                } else {
                    LogUtil.i(TAG, "onActivityResult write settings not granted");
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void showPermissionStorageRequest() {
        LogUtil.i(TAG, "Show contacts button pressed. Checking permissions.");

        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
//                != PackageManager.PERMISSION_GRANTED
                ) {
            // Contacts permissions have not been granted.
            LogUtil.i(TAG, "Contact permissions has NOT been granted. Requesting permissions.");
            requestContactsPermissions();
        } else {
            // Contact permissions have been granted. Show the contacts fragment.
            LogUtil.i(TAG, "Contact permissions have already been granted. Displaying contact details.");
            downloadApk();
        }
    }

    private void requestContactsPermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                || ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.READ_PHONE_STATE)
                ) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            LogUtil.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
//            Snackbar.make(v, "permission_contacts_rationale",
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction("ok", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            ActivityCompat
//                                    .requestPermissions(MapViewActivity.this, ExpressConstant.PERMISSIONS_LOCATION,
//                                            ExpressConstant.REQUEST_PERMISSION_ID);
//                        }
//                    })
//                    .show();
            ActivityCompat
                    .requestPermissions(BaseActivity.this, ExpressConstant.PERMISSIONS_STORAGE,
                            ExpressConstant.REQUEST_PERMISSION_ID);
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, ExpressConstant.PERMISSIONS_STORAGE, ExpressConstant.REQUEST_PERMISSION_ID);
        }
        // END_INCLUDE(contacts_permission_request)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ExpressConstant.REQUEST_PERMISSION_ID) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                downloadApk();
            } else {
                showPermissionStorageRequest();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void downloadApk() {
        //AsyncTask
        makeDir(ExpressConstant.DOWNLOAD_APK_DIR);//创建下载文件夹

        HttpHelper.sendRequest_downloadApk(this, download_apk_url, RequestID.REQ_DOWNLOAD_APK);
    }

    /**
     * 创建文件夹
     *
     * @param dir
     */
    protected void makeDir(String dir) {
        File file = new File(dir);
        if (!file.exists() && file != null) {
            file.mkdirs();
        }
        LogUtil.d(TAG, "file.isDirectory();" + file.isDirectory());
    }


    /**
     * 弹出提示用户当前网络非WIFI对话框
     */
    protected void showNotWifiNetworkStatusDialog() {
        String prompt = getString(R.string.str_dialog_update_not_wifi_prompt);
        String confirm = getString(R.string.str_dialog_update_not_wifi_btn_update);
        String cancel = getString(R.string.str_dialog_update_not_wifi_btn_cancel);
        final CustomDialog customDialog = new CustomDialog(this, prompt, confirm, cancel, 60);
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                customDialog.dismiss();
                //去下载
                showPermissionStorageRequest();
            }

            @Override
            public void doCancel() {
                customDialog.dismiss();
                if (versionInfo.upgrade == ExpressConstant.UPGRADE_FORCE) {
                    exitApp();
                }
            }
        });
    }

    /**
     * 重新登录
     */
    protected void showReloginDialog() {
        personalInfo = null;
        sp.remove("userInfo");
        String prompt = getString(R.string.str_dialog_prompt_token_expire);
        String confirm = getString(R.string.str_dialog_btn_to_ok);
        String cancel = "";
        final CustomDialog customDialog = new CustomDialog(this, prompt, confirm, cancel, 60);
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                customDialog.dismiss();
                startActivity(new Intent(BaseActivity.this, LoginActivity.class));
            }

            @Override
            public void doCancel() {
                customDialog.dismiss();
            }
        });
    }

    protected final <E extends View> E getView(int id) {
        return (E) findViewById(id);
    }
}
