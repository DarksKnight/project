package com.express56.xq.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.express56.xq.R;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.StringUtils;
import com.express56.xq.widget.ToastUtil;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

public class ForgetPwdActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ForgetPwdActivity.class.getSimpleName();

    /**
     * 倒计时器
     */
    private TimeCount timeCount = null;
    private EditText editText_newPwd;
    private EditText editText_repeatPwd;
    private EditText editText_verifyCode;
    private String newPwdStr;
    private String repeatPwdStr;
    private String verifyCodeStr;
    private TextView btn_validate;

    /**
     * 获取验证码倒计时
     */
    private String tempStr = null;
    private EditText editText_username;
    private String usernameStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        init();
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.forget_pwd_back_btn).setOnClickListener(this);
        findViewById(R.id.forget_pwd_btn_reset_pwd).setOnClickListener(this);
        btn_validate = (TextView) findViewById(R.id.forget_pwd_btn_get_verify_code);
        btn_validate.setOnClickListener(this);

        editText_username = (EditText) findViewById(R.id.forget_pwd_editText_username);
        editText_newPwd = (EditText) findViewById(R.id.forget_pwd_editText_password);
        editText_repeatPwd = (EditText) findViewById(R.id.forget_pwd_editText_repeat_password);
        editText_verifyCode = (EditText) findViewById(R.id.forget_pwd_editText_verify_code);

    }

    /**
     * 待提交数据检查
     *
     * @return
     */
    private boolean checkSubmitData() {
        usernameStr = editText_username.getText().toString();
        newPwdStr = editText_newPwd.getText().toString();
        repeatPwdStr = editText_repeatPwd.getText().toString();
        verifyCodeStr = editText_verifyCode.getText().toString();
        if (TextUtils.isEmpty(usernameStr)) {
            ToastUtil.showMessage(this, getString(R.string.str_prompt_username_check), true);
            return false;
        }
        if (TextUtils.isEmpty(newPwdStr)) {
            ToastUtil.showMessage(this, getString(R.string.str_prompt_password_check), true);
            return false;
        }
        if (TextUtils.isEmpty(repeatPwdStr)) {
            ToastUtil.showMessage(this, getString(R.string.str_prompt_repeat_password_check), true);
            return false;
        }
        if (!newPwdStr.equals(repeatPwdStr)) {
            ToastUtil.showMessage(this, getString(R.string.str_prompt_password_equal_check), true);
            return false;
        }
        return true;
    }

    @Override
    protected void initData() {
        super.initData();
        timeCount = new TimeCount(ExpressConstant.GET_VERIFY_CODE_INTERVAL, 1000);
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btn_validate.setClickable(false);
            btn_validate.setEnabled(false);
            tempStr = null;
            tempStr = getString(R.string.str_btn_reget_validate_code);
            tempStr = String.format(tempStr, millisUntilFinished / 1000);
            btn_validate.setText(tempStr);
        }

        @Override
        public void onFinish() {
            btn_validate.setClickable(true);
            btn_validate.setEnabled(true);
            btn_validate.setText(R.string.str_btn_get_validate_code_2);
        }
    }


    /**
     * 获取验证码
     */
    private void doGetValidateCodeAction() {
        timeCount.start();
        HttpHelper.sendRequest_getValidateCodeForgetPWD(this, RequestID.REQ_GET_VALIDATE_CODE_FORGET_PWD, usernameStr, dialog);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.forget_pwd_btn_get_verify_code:
                if (checkSubmitData()) {
                    doGetValidateCodeAction();
                }
                break;
            case R.id.forget_pwd_back_btn:
                finish();
                break;
            case R.id.forget_pwd_btn_reset_pwd:
                if (checkSubmitData()) {
                    if (TextUtils.isEmpty(verifyCodeStr)) {
                        ToastUtil.showMessage(this, getString(R.string.str_prompt_verify_code_check), true);
                        return;
                    }
                    //重置密码
                    HttpHelper.sendRequest_resetPwd(this,
                            RequestID.REQ_POST_RESET_PWD, newPwdStr, usernameStr, verifyCodeStr, dialog, context);
                }
                break;
            default:
                break;
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
            case RequestID.REQ_GET_VALIDATE_CODE_FORGET_PWD:
            /*{"code":9,"result":"613414"}*/

//                if (object.containsKey("code")) {
//                    int code = object.getIntValue("code");
//                    if (code == 9) {
//                        if (object != null && object.containsKey("result")) {
//                            String content = object.getString("result");
////                            ToastUtil.showMessage(this, content, true);
//                            if (!StringUtils.isEmpty(content)) {
//                                editText_verifyCode.setText(content);
//                            }
//                        }
//                    } else {
//                        showErrorMsg(object);
//                    }
//                }
                break;
            case RequestID.REQ_POST_RESET_PWD:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            ToastUtil.showMessage(this, content, true);
                            finish();
                        }
                    } else if (code == 0) {
                        showReloginDialog();
                    } else {
                        showErrorMsg(object);
                    }
                }
                break;
            default:
                break;
        }
    }
}
