package com.express56.xq.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.express56.xq.R;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.IHttpProgressResponse;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.AboutInfo;
import com.express56.xq.model.UpgradeInfo;
import com.express56.xq.model.VersionInfo;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.StringUtils;
import com.express56.xq.util.VersionUtils;
import com.express56.xq.widget.CustomDialog;
import com.express56.xq.widget.ToastUtil;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

public class AboutActivity extends BaseActivity implements View.OnClickListener, IHttpProgressResponse {

    private static final String TAG = AboutActivity.class.getSimpleName();

    private AboutInfo aboutInfo;
    private TextView textView_company_name;
    private TextView textView_company_contact;
    private TextView textView_company_weixin;
    private TextView textView_company_email;
    private TextView textView_company_copyright_1;
    private TextView textView_company_copyright_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aboutInfo = (AboutInfo) getIntent().getSerializableExtra("about_info");
        setContentView(R.layout.activity_about);
        init();
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.about_back_btn).setOnClickListener(this);
        findViewById(R.id.about_btn_check_upgrade).setOnClickListener(this);

        textView_company_name = (TextView) findViewById(R.id.about_textView_company_name);
        textView_company_contact = (TextView) findViewById(R.id.about_textView_company_contact);
        textView_company_weixin = (TextView) findViewById(R.id.about_textView_company_weixin);
        textView_company_email = (TextView) findViewById(R.id.about_textView_company_email);
        textView_company_copyright_1 = (TextView) findViewById(R.id.about_copyright_1);
        textView_company_copyright_2 = (TextView) findViewById(R.id.about_copyright_2);
    }

    @Override
    protected void initData() {
        super.initData();
        if (aboutInfo != null) {
            textView_company_name.setText(aboutInfo.name);
            textView_company_contact.setText(aboutInfo.mobile);
            textView_company_weixin.setText(aboutInfo.weixin);
            textView_company_email.setText(aboutInfo.email);
            textView_company_copyright_2.setText(aboutInfo.rights);
            textView_company_copyright_1.setText(aboutInfo.name + "版权所有");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.about_back_btn:
                finish();
                break;
            case R.id.about_btn_check_upgrade:
                checkUpgrade();
                break;
            default:
                break;
        }
    }

    private void checkUpgrade() {
        if (sp.getUserInfo() != null) {
            HttpHelper.sendRequest_getUpgradeInfo(context, RequestID.REQ_UPDATE_VERSION_CHECK, dialog);
        }

    }

    @Override
    public void doHttpResponse(Object... param) {
        if (this.isFinishing()) {
            return;
        }
        String result = (String) param[0];
        LogUtil.d(TAG, "response str=" + result);
        if (result == null) {
            String errMsg = (String) param[2];
            if (StringUtils.isEmpty(errMsg)) {//没有错误信息  弹出默认错误提示
                errMsg = getString(R.string.str_net_request_fail);
            }
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
            return;
        }
        if (InvokeStaticMethod.isNotJSONstring(this, result)) {
            finish();
            return;
        }
        JSONObject object = JSON.parseObject(result);
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
//                            "version": "20161115.1.0beta",
//                                    "isRequire": "1",
//                                    "remarks": "测试",
//                                    "downloadPath": "app/android/express.apk"
                            if (upgradeInfo.version.equals(VersionUtils.getVersionName(context))) {
                                showResult();
                            } else {
                                if (upgradeInfo.isRequire != ExpressConstant.UPGRADE_FORCE) {//非强制升级
                                    showUpgradeDialog();
                                } else {
                                    showResult();
                                }
                            }
                        }
                    } else if (code == 0) {
                        showReloginDialog();
                    } else {
//                        showResult();
                        showErrorMsg(object);
                    }
                }
                break;

            default:
                break;
        }
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
                showResult();
            }
        });
        customDialog.show();
    }


//    /**
//     * 清除账户相关信息
//     */
//    private void clearAccountInfo() {
//        sp.remove("userInfo");
//        sp.remove("bike");
//        sp.remove(BikeConstant.BIKE_PIC_URL_KEY);
//        sp.remove(BikeConstant.BIKE_PIC_LOCAL_PATH_KEY);
//        sp.remove(BikeConstant.AUTHENTICATION_PIC_URL_KEY);
//        sp.remove(BikeConstant.AUTHENTICATION_PIC_LOCAL_PATH_KEY);
//        sp.remove(BikeConstant.USER_HEAD_PORTRAIT_PIC_URL_KEY);
//        sp.remove(BikeConstant.USER_HEAD_PORTRAIT_PIC_LOCAL_PATH_KEY);
//        sp.remove(BikeConstant.LATEST_PUSH_MSG);
//
//        FileUtil.deleteSDcardFile(BikeConstant.IMAGE_FILE_LOCATION_PATH);
//        FileUtil.deleteSDcardFile(BikeConstant.BIKE_PIC_PATH);
//        FileUtil.deleteSDcardFile(BikeConstant.AUTHENTICATION_PIC_PATH);
//        FileUtil.deleteSDcardFile(BikeConstant.USER_HEAD_PORTRAIT_PIC_PATH);
//    }

    private void showResult() {
        ToastUtil.showMessageLong(context, getString(R.string.str_about_us_item_no_version_update_prompt));
    }


    @Override
    public void doHttpProgressResponse(float progress) {

    }
}
