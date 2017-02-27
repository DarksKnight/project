package com.express56.xq.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.express56.xq.R;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.IHttpProgressResponse;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.Adv;
import com.express56.xq.model.UpgradeInfo;
import com.express56.xq.model.User;
import com.express56.xq.util.DeviceUtil;
import com.express56.xq.util.DisplayUtil;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.StringUtils;
import com.express56.xq.util.VersionUtils;
import com.express56.xq.widget.CustomDialog;
import com.express56.xq.widget.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, IHttpProgressResponse {

    private static final String TAG = LoginActivity.class.getSimpleName();

    /**
     * 用户名输入框
     */
    private EditText editText_username = null;

    /**
     * 密码输入框
     */
    private EditText editText_password = null;

    /**
     * 登录按钮
     */
    private Button btn_login = null;

    private TextView btn_to_register = null;

    private TextView btn_to_forget = null;

    /**
     * 手机号码
     */
    private String str_username = null;

    /**
     * 验证码
     */
    private String str_password = null;

    public static LoginActivity loginActivityNew = null;
    private EditText editText_ip_set;
    private Button btn_save_ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        DisplayUtil.ini(this);
        init();
        loginActivityNew = this;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
//                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
//                String str = formatter.format(curDate);
                URL url = null;//取得资源对象
                try {
                    url = new URL("http://www.baidu.com");
                    URLConnection uc = url.openConnection();//生成连接对象
                    uc.connect(); //发出连接
                    long time = uc.getDate(); //取得网站日期时间
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                    String deadline = "2017年12月21日";
                    //得到毫秒数
                    long deadTime = 0;
                    try {
                        deadTime = sdf.parse(deadline).getTime();
                        if (time >= deadTime) {
                            exitApp();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    LogUtil.i(TAG, "deadline---->>>>" + deadTime);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 100);
//        if (user != null && !StringUtils.isEmpty(user.token) && UploadService.uploadService == null)
//        {
//            LogUtil.d(TAG, "user.token= " + user.token);
//            startActivity(new Intent(context, MainActivity.class));
////            startService(new Intent(context, UploadService.class));//开启后台上传服务
//            finish();
//        }
//        checkUpgrade();
    }

    /**
     * 调用接口检测是否需要升级
     * 强制升级
     * 非强制升级
     */
    private void checkUpgrade() {
        HttpHelper.sendRequest_getUpgradeInfo(context, RequestID.REQ_UPDATE_VERSION_CHECK, dialog);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String username = intent.getStringExtra("username");
            editText_username.setText(username);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        editText_username = (EditText) findViewById(R.id.login_username_edit);
        editText_password = (EditText) findViewById(R.id.login_password_edit);

        btn_to_register = (TextView) findViewById(R.id.login_textView_register);
        btn_to_register.setOnClickListener(this);

        btn_to_forget = (TextView) findViewById(R.id.login_textView_forget_password);
        btn_to_forget.setOnClickListener(this);

        btn_login = (Button) findViewById(R.id.login_btn_login);
        btn_login.setOnClickListener(this);

//        setEditTextHintSize(editText_username, R.string.str_input_mobile_hint);
//        setEditTextHintSize(editText_password, R.string.str_verify_code_hint);

        editText_ip_set = (EditText) findViewById(R.id.editText_set_ip);
        btn_save_ip = (Button) findViewById(R.id.btn_save_ip);
        btn_save_ip.setOnClickListener(this);

    }

    private void setEditTextHintSize(EditText editText, int hintStrID) {
        // 新建一个可以添加属性的文本对象
        SpannableString code = new SpannableString(getString(hintStrID));
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan codeAss = new AbsoluteSizeSpan(12, true);
        // 附加属性到文本
        code.setSpan(codeAss, 0, code.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        editText.setHint(new SpannedString(code)); // 一定要进行转换,否则属性会消失
    }

    @Override
    protected void initData() {
        super.initData();
//        if (sp != null && sp.getUserInfo() != null && !StringUtils.isEmpty(sp.getUserInfo().phone)) {
//            editText_username.setText(sp.getUserInfo().phone);
//        }
    }

    @Override
    public void onClick(View v) {
        if (checkFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.login_btn_login:
                dologinAction();
                break;
            case R.id.login_textView_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.login_textView_forget_password:
                startActivity(new Intent(this, ForgetPwdActivity.class));
                break;
            default:
                break;
        }
    }

    /**
     * 登录
     */
    private void dologinAction() {
        str_username = editText_username.getText().toString();
        str_password = editText_password.getText().toString();
        if (TextUtils.isEmpty(str_username)) {
            ToastUtil.showMessage(this, getString(R.string.str_prompt_username_check), true);
            return;
        }
        if (TextUtils.isEmpty(str_password)) {
            ToastUtil.showMessage(this, getString(R.string.str_prompt_password_check), true);
            return;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        HttpHelper.sendRequest_login(this, RequestID.REQ_LOGIN,
                str_username, str_password, DeviceUtil.getTheDeviceID(this, deviceId), dialog);

    }

    /**
     * 弹出提示用户更新的对话框
     */
    private void showUpgradeDialog() {
        String prompt = getString(R.string.str_dialog_update_optional_prompt);
        String confirm = getString(R.string.str_dialog_update_optional_btn_update);
        String cancel = getString(R.string.str_dialog_update_optional_btn_cancel);
        final CustomDialog customDialog = new CustomDialog(this, prompt, confirm, cancel, 60);
        customDialog.setClickListener(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                customDialog.dismiss();
                //检查是否是wifi网络
                if (InvokeStaticMethod.isWifiOpen(context)) {
                    //去下载
                    showPermissionStorageRequest();
                } else {
                    //提示用户当前非wifi网络
                    showNotWifiNetworkStatusDialog();
                }
            }

            @Override
            public void doCancel() {
                customDialog.dismiss();
                enterMainActivity();
            }
        });
        customDialog.show();
    }

    /**
     * 弹出提示用户强制更新的对话框
     */
    private void showForceUpgradeDialog() {
        String prompt = getString(R.string.str_dialog_update_force_prompt);
        String confirm = getString(R.string.str_dialog_update_force_btn_update);
        String cancel = getString(R.string.str_dialog_update_force_btn_cancel);
        final CustomDialog customDialog = new CustomDialog(this, prompt, confirm, cancel, 60);
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                customDialog.dismiss();
                //检查是否是wifi网络
                if (InvokeStaticMethod.isWifiOpen(context)) {
                    //去下载
                    showPermissionStorageRequest();
                } else {
                    //提示用户当前非wifi网络
                    showNotWifiNetworkStatusDialog();
                }
            }

            @Override
            public void doCancel() {
                customDialog.dismiss();
                exitApp();
            }
        });
    }

    @Override
    public void doHttpResponse(Object... param) {
        if (this.isFinishing()) {
            return;
        }
        if (param[0] != null && (param[0] instanceof File)) {
            switch (Integer.parseInt(param[1].toString())) {
                case RequestID.REQ_DOWNLOAD_APK:
                    //安装 apk
                    InvokeStaticMethod.installApk(
                            ExpressConstant.DOWNLOAD_APK_DIR + File.separator + ExpressConstant.APK_FILE_NAME, this);
                    break;
                default:
                    break;
            }
        } else if (param[0] == null || param[0] instanceof String) {
            if (param[0] == null) {
                if (!InvokeStaticMethod.isNetWorkOpen(this)) {
                    ToastUtil.showMessage(this, getString(R.string.str_network_closed), false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exitApp();
                        }
                    }, 2000);
                    return;
                }
            }
            String result = (String) param[0];
            LogUtil.d(TAG, "response str=" + result);
            if (result == null) {
                String errMsg = (String) param[2];
//            if (StringUtils.isEmpty(errMsg)) {//没有错误信息  弹出默认错误提示
                errMsg = getString(R.string.str_net_request_fail);
//            }
                Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
                switch (Integer.parseInt(param[1].toString())) {
                    case RequestID.REQ_DOWNLOAD_APK:
                        exitApp();
                        break;
                    default:
                        finish();
                        break;
                }
                return;
            }
            if (InvokeStaticMethod.isNotJSONstring(this, result)) {
                finish();
                return;
            }
            JSONObject object = JSON.parseObject(result);
            if (object == null) {
                ToastUtil.showMessage(this, "登录失败", false);
                return;
            }
            switch (Integer.parseInt(param[1].toString())) {
                case RequestID.REQ_UPDATE_VERSION_CHECK:
                    if (object.containsKey("code")) {
                        int code = object.getIntValue("code");
                        if (code == 9) {

                            if (object != null && object.containsKey("result")) {
                                String content = object.getString("result");
                                UpgradeInfo upgradeInfo = JSON.parseObject(content, UpgradeInfo.class);
                                String url = HttpHelper.HTTP + HttpHelper.IP + File.separator + upgradeInfo.downloadPath;
                                upgradeInfo.downloadPath = url;
                                download_apk_url = url;
                                if (!upgradeInfo.version.equals(VersionUtils.getVersionName(context))) {
                                    //强制升级提示框
                                    if (upgradeInfo.isRequire == ExpressConstant.UPGRADE_FORCE) {//强制升级
                                        showForceUpgradeDialog();
                                    } else {
                                        //非强制升级提示框
                                        showUpgradeDialog();
                                    }
                                    return;
                                }
                            }
                        }
                    }
                    enterMainActivity();
                    break;
                case RequestID.REQ_GET_VALIDATE_CODE:
                    // {"success":true,"result":{"checkCode":"338469"},"error":null,"unAuthorizedRequest":false}
                    if (object.containsKey("success"))

                    {
                        boolean success = object.getBoolean("success");
                        if (success) {
                            if (object.containsKey("result")) {
                                JSONObject object1 = JSON.parseObject(object.getString("result"));
                                if (object1 != null) {
                                    String code = object1.getString("checkCode");
                                    if (code != null && code.length() != 0) {
                                        editText_password.setText(code);
                                    }
                                }
                            }
                        } else {
                            showErrorMsg(object);
                        }
                    }
                    break;
                case RequestID.REQ_LOGIN:
                    if (object.containsKey("code")) {
                        int code = object.getIntValue("code");
                        if (code == 9) {
                            if (object != null && object.containsKey("result")) {
                                String content = object.getString("result");
                                object = JSON.parseObject(content);
                                User user = JSON.parseObject(content, User.class);
//                            String token = object.getString("token");
//                            int userType = object.getIntValue("userType");
                                user.phone = str_username;
//                            user.token = token;
//                            LogUtil.d(TAG, " token= " + user.token);
//                            user.userType = userType;

                                ExpressConstant.isOnlyUploadWifi = (user.wifiUpload == 1) ? true : false;
                                sp.setBoolean("is_upload_only_wifi", ExpressConstant.isOnlyUploadWifi);
                                sp.saveUserInfo(user);

                                if (sp.getUserInfo() != null && !StringUtils.isEmpty(sp.getUserInfo().token)) {
                                    HttpHelper.sendRequest_getAdvertizement(context, RequestID.REQ_GET_ADVERTIZEMENTS, sp.getUserInfo().token, null);
                                }

//                            ToastUtil.showMessage(this, "login success token is " + token);

//                            //需要添加头部参数
//                            OkHttpUtils.setNeedAddHeadParam(true);
//                            OkHttpUtils.getInstance();
//                            OkHttpUtils.get().addHeader("appId", HttpHelper.entryptPassword("admin"));

//                                startActivity(new Intent(this, MainActivity.class));
//                                finish();
                            }
                        } else if (code == 1) {
                            ToastUtil.showMessage(context, "余额不足，请充值！");
                        } else {
                            showErrorMsg(object);
                        }
                    }
                    break;
                case RequestID.REQ_GET_ADVERTIZEMENTS:
                    if (object.containsKey("code")) {
                        int code = object.getIntValue("code");
                        if (code == 9) {
                            if (object != null && object.containsKey("result")) {
                                String content = object.getString("result");
                                MainActivity.advs.clear();
                                MainActivity.advs.addAll((ArrayList<Adv>) JSON.parseArray(content, Adv.class));
                                for (int i = 0; i < MainActivity.advs.size(); i++) {
                                    MainActivity.advs.get(i).image = HttpHelper.HTTP + HttpHelper.IP + "/images/"
                                            + MainActivity.advs.get(i).image;
//                                http://120.195.137.162:82/images/ads/ads1.jpg
                                }
//                                canShowAdv = true;
//                            init();
                            }
                        } else if (code == 0) {
                            showReloginDialog();
                        } else {
                            showErrorMsg(object);
                        }
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                    break;
                case RequestID.REQ_AUT0_LOGIN:
                    if (object.containsKey("success"))

                    {
                        boolean success = object.getBoolean("success");
                        if (success) {
                            if (object.containsKey("result")) {
                                String content = object.getString("result");
                                object = JSON.parseObject(content);
                                String userJsonStr = object.getString("userDto");
                                LogUtil.d(TAG, "json user->" + userJsonStr);
                                User user = JSONObject.parseObject(userJsonStr, User.class);
                                sp.saveUserInfo(user);
                                LogUtil.d("TEST", "token : " + sp.getUserInfo().token);
                                InvokeStaticMethod.registerXGPush(this, sp.getUserInfo().token);
                            }
                            //进入主界面
                            finish();
                        } else {
                            //给出错误提示 跳转到登录页面
                            String errorMsg = getString(R.string.str_prompt_auto_login_fail);
                            showErrorMsg(object, errorMsg);
                            //跳转
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }

    private void enterMainActivity() {
        user = sp.getUserInfo();
        if (user != null && !StringUtils.isEmpty(user.token)) {
            startActivity(new Intent(context, MainActivity.class));
            finish();
        }
    }


    @Override
    public void doHttpCanceled(Object... param) {
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (clickFirstTime > 0) {
            if (currentTime - clickFirstTime < ExpressConstant.EXIT_APP_DURATION) {//连续按下返回键
                //退出应用
                exitApp();
            } else {
                clickFirstTime = currentTime;
                ToastUtil.showMessage(this, getString(R.string.str_prompt_back_key_exit));
            }
        } else { //第一次点击
            clickFirstTime = currentTime;
            ToastUtil.showMessage(this, getString(R.string.str_prompt_back_key_exit));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        loginActivityNew = null;
//        android.os.Process.killProcess(android.os.Process.myPid());//获取PID
//        System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出

//        ActivityManager am = (ActivityManager)getSystemService (Context.ACTIVITY_SERVICE);
//        am.restartPackage(getPackageName());
    }

    @Override
    public void doHttpProgressResponse(float progress) {

    }
}
