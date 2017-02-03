package com.express56.xq.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.express56.xq.R;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.User;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.StringUtils;
import com.express56.xq.widget.ToastUtil;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = RegisterActivity.class.getSimpleName();


    /**
     * 注册
     */
    private TextView btn_register = null;

    /**
     * 验证码
     */
    private String verificationCode = null;


    /**
     * 获取验证码倒计时
     */
    private String tempStr = null;

    /**
     * 倒计时器
     */
    private TimeCount timeCount = null;

    /**
     * 是否勾选
     */
    private boolean isRead = false;
    private EditText editText_username;
    private EditText editText_password;
    private EditText editText_repeat_password;
    private EditText editText_realName;
    private TextView textView_type_normal;
    private TextView textView_type_courier;

    /**
     * 2:normal 3：courier
     */
    private int userType = 2;

    private String usernameStr;
    private String passwordStr;
    private String repasswordStr;
    private String realNameStr;
    private Drawable drawableSelected;
    private Drawable drawableUnSelected;
    private TextView btn_validate;
    private EditText editText_verifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    @Override
    protected void initData() {
        super.initData();
        timeCount = new TimeCount(ExpressConstant.GET_VERIFY_CODE_INTERVAL, 1000);
        drawableSelected = getResources().getDrawable(R.drawable.icon_select_select_normal);
        drawableUnSelected = getResources().getDrawable(R.drawable.icon_select_unselect_normal);

    }

    @Override
    protected void initView() {
        super.initView();

        btn_validate = (TextView) findViewById(R.id.register_btn_get_verify_code);
        btn_validate.setOnClickListener(this);

        editText_username = (EditText) findViewById(R.id.register_editText_username);
        editText_password = (EditText) findViewById(R.id.register_editText_password);
        editText_repeat_password = (EditText) findViewById(R.id.register_editText_repeat_password);
        editText_realName = (EditText) findViewById(R.id.register_editText_real_name);

        editText_verifyCode = (EditText) findViewById(R.id.register_editText_verify_code);

        textView_type_normal = (TextView) findViewById(R.id.register_textView_type_normal);
        textView_type_normal.setOnClickListener(this);
        textView_type_courier = (TextView) findViewById(R.id.register_textView_type_courier);
        textView_type_courier.setOnClickListener(this);

        btn_register = (TextView) findViewById(R.id.register_btn);
        btn_register.setOnClickListener(this);

        findViewById(R.id.register_back_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (checkFastClick()) {
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.register_textView_type_normal:
                drawableSelected.setBounds(0, 0, drawableSelected.getMinimumWidth(), drawableSelected.getMinimumHeight());
                drawableUnSelected.setBounds(0, 0, drawableUnSelected.getMinimumWidth(), drawableUnSelected.getMinimumHeight());
                textView_type_normal.setCompoundDrawables(drawableSelected, null, null, null);
                textView_type_courier.setCompoundDrawables(drawableUnSelected, null, null, null);
                editText_realName.setHint("昵称");
                userType = 2;
                break;
            case R.id.register_textView_type_courier:
                drawableSelected.setBounds(0, 0, drawableSelected.getMinimumWidth(), drawableSelected.getMinimumHeight());
                drawableUnSelected.setBounds(0, 0, drawableUnSelected.getMinimumWidth(), drawableUnSelected.getMinimumHeight());
                textView_type_courier.setCompoundDrawables(drawableSelected, null, null, null);
                textView_type_normal.setCompoundDrawables(drawableUnSelected, null, null, null);
                editText_realName.setHint("姓名");
                userType = 3;
                break;
            case R.id.register_back_btn:
                finish();
                break;
            case R.id.register_btn:
                doRegisterAction();
                break;
            case R.id.register_btn_get_verify_code:
                if (checkSubmitData()) {
                    doGetValidateCodeAction();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 待提交数据检查
     *
     * @return
     */
    private boolean checkSubmitData() {
        usernameStr = editText_username.getText().toString();
        passwordStr = editText_password.getText().toString();
        repasswordStr = editText_repeat_password.getText().toString();
        realNameStr = editText_realName.getText().toString();
        verificationCode = editText_verifyCode.getText().toString();
        if (TextUtils.isEmpty(usernameStr)) {
            ToastUtil.showMessage(this, getString(R.string.str_prompt_username_check), true);
            return false;
        }
        if (TextUtils.isEmpty(passwordStr)) {
            ToastUtil.showMessage(this, getString(R.string.str_prompt_password_check), true);
            return false;
        }
        if (TextUtils.isEmpty(repasswordStr)) {
            ToastUtil.showMessage(this, getString(R.string.str_prompt_repeat_password_check), true);
            return false;
        }
        if (!repasswordStr.equals(passwordStr)) {
            ToastUtil.showMessage(this, getString(R.string.str_prompt_password_equal_check), true);
            return false;
        }
        if (TextUtils.isEmpty(realNameStr)) {
            ToastUtil.showMessage(this, getString(R.string.str_prompt_realname_check), true);
            return false;
        }
        return true;
    }

    /**
     * 注册
     */
    private void doRegisterAction() {

        if (checkSubmitData()) {
            if (TextUtils.isEmpty(verificationCode)) {
                ToastUtil.showMessage(this, getString(R.string.str_prompt_verify_code_check), true);
                return;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //提交注册请求
                    HttpHelper.sendRequest_registerNow(
                            RegisterActivity.this, RequestID.REQ_REGISTER, usernameStr, passwordStr, repasswordStr, realNameStr, usernameStr, verificationCode, userType, "00000000001", dialog);
                }
            }, 1000);
        }

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
        usernameStr = editText_username.getText().toString();
        if (TextUtils.isEmpty(usernameStr)) {
            ToastUtil.showMessage(this, getString(R.string.str_prompt_username_check), true);
            return;
        }
        if (usernameStr.matches(ExpressConstant.PHONE_REGEX)) {
            timeCount.start();
            HttpHelper.sendRequest_getValidateCodeRegister(this, RequestID.REQ_GET_VALIDATE_CODE_REGISTER, usernameStr, dialog);
        } else {
            ToastUtil.showMessage(this, getString(R.string.str_prompt_username_check), true);
            return;
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
            case RequestID.REQ_GET_VALIDATE_CODE_REGISTER:
            /*{"code":9,"result":"613414"}*/

                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
//                            ToastUtil.showMessage(this, content, true);
                            if (!StringUtils.isEmpty(content)) {
                                editText_verifyCode.setText(content);
                            }
                        }
                    } else {
                        showErrorMsg(object);
                    }
                }
                break;
            case RequestID.REQ_REGISTER:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            ToastUtil.showMessage(this, content, true);

                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class).putExtra("username", usernameStr));
                            finish();

                        }
                    } else {
                        showErrorMsg(object);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理登录请求返回结果 进入主界面
     *
     * @param object
     */
    private void enterMainView(JSONObject object) {
        if (object.containsKey("result")) {
            String content = object.getString("result");
            object = JSON.parseObject(content);
            String userJsonStr = object.getString("userDto");
            LogUtil.d(TAG, "json user->" + userJsonStr);
            User user = JSONObject.parseObject(userJsonStr, User.class);
            sp.saveUserInfo(user);
        }
        if (LoginActivity.loginActivityNew != null) {
            LoginActivity.loginActivityNew.finish();
            LoginActivity.loginActivityNew = null;
        }
        //进入主界面
        this.finish();
    }

}
