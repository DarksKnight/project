
package com.express56.xq.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.express56.xq.R;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.StringUtils;
import com.express56.xq.widget.ToastUtil;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;


public class ModifyPwdActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ModifyPwdActivity.class.getSimpleName();
    private EditText editText_oldPwd;
    private EditText editText_newPwd;
    private EditText editText_repeatPwd;
    private String oldPwdStr;
    private String newPwdStr;
    private String repeatPwdStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moify_pwd);
        init();
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.modify_pwd_back_btn).setOnClickListener(this);
        findViewById(R.id.btn_modify_pwd).setOnClickListener(this);

        editText_oldPwd = (EditText) findViewById(R.id.modify_pwd_editText_old_pwd);
        editText_newPwd = (EditText) findViewById(R.id.modify_pwd_editText_password);
        editText_repeatPwd = (EditText) findViewById(R.id.modify_pwd_editText_repeat_password);

    }

    @Override
    protected void initData() {
        super.initData();
    }

    /**
     * 待提交数据检查
     *
     * @return
     */
    private boolean checkSubmitData() {
        oldPwdStr = editText_oldPwd.getText().toString();
        newPwdStr = editText_newPwd.getText().toString();
        repeatPwdStr = editText_repeatPwd.getText().toString();
        if (TextUtils.isEmpty(oldPwdStr)) {
            ToastUtil.showMessage(this, getString(R.string.str_prompt_old_pwd_check), true);
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
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.modify_pwd_back_btn:
                finish();
                break;
            case R.id.btn_modify_pwd:
                if (checkSubmitData()) {
                    doModifyPwd();
                }
                break;
            default:
                break;
        }
    }

    private void doModifyPwd() {
        HttpHelper.sendRequest_modifyPwd(context, RequestID.REQ_POST_MODIFY_PWD, newPwdStr, oldPwdStr, sp.getUserInfo().token, dialog);
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
            case RequestID.REQ_POST_MODIFY_PWD:
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
