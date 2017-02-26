package com.express56.xq.activity;

import com.express56.xq.R;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ToastUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

/**
 * Created by apple on 2017/2/26.
 */

public class ReceivingOrderShowActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = ReceivingOrderShowActivity.class.getSimpleName();

    private String orderId = "";

    private String insuranceMoney = "";

    private String expressMoney = "";

    private String remarks = "";

    private Button btnSave = null;

    private TextView tvOrderNo = null;

    private TextView tvCreateDate = null;

    private TextView tvReceiveDate = null;

    private TextView tvSender = null;

    private TextView tvSenderPhone = null;

    private TextView tvSenderAddress = null;

    private TextView tvReceiver = null;

    private TextView tvReceiverPhone = null;

    private TextView tvReceiverAddress = null;

    private TextView tvWeight = null;

    private TextView tvThingDesc = null;

    private EditText etEpressMoney = null;

    private EditText etSupportMoney = null;

    private EditText etRemarks = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_order_show);
        init();
    }

    @Override
    protected void initView() {
        super.initView();

        tvOrderNo = getView(R.id.tv_receive_order_show_show_number);
        tvCreateDate = getView(R.id.tv_receive_order_show_show_create_date);
        tvReceiveDate = getView(R.id.tv_receive_order_show_show_serviceTime);
        tvSender = getView(R.id.tv_receive_order_show_show_sender);
        tvSenderPhone = getView(R.id.tv_receive_order_show_show_sender_phone);
        tvSenderAddress = getView(R.id.tv_receive_order_show_show_sender_address);
        tvReceiver = getView(R.id.tv_receive_order_show_show_receiver);
        tvReceiverPhone = getView(R.id.tv_receive_order_show_show_receiver_phone);
        tvReceiverAddress = getView(R.id.tv_receive_order_show_show_receiver_address);
        tvWeight = getView(R.id.tv_receive_order_show_show_weight);
        tvThingDesc = getView(R.id.tv_receive_order_show_show_remark);
        etEpressMoney = getView(R.id.et_receive_order_express_money);
        etSupportMoney = getView(R.id.et_receive_order_express_support_money);
        etRemarks = getView(R.id.et_place_order_desc);
    }

    @Override
    protected void initData() {
        super.initData();

        orderId = getIntent().getStringExtra("orderId");

        btnSave = getView(R.id.btn_receive_order_show_show_pay);

        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSave) {
            expressMoney = etEpressMoney.getText().toString();
            insuranceMoney = etSupportMoney.getText().toString();
            remarks = etRemarks.getText().toString();
            HttpHelper.sendRequest_saveQuotation(this, RequestID.REQ_SAVE_RECEIVING_ORDER,
                    insuranceMoney, expressMoney, orderId, remarks, sp.getUserInfo().token, dialog);
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
            return;
        }
        if (InvokeStaticMethod.isNotJSONstring(this, result)) {
            finish();
            return;
        }
        JSONObject object = JSON.parseObject(result);
        if (object == null) {
            ToastUtil.showMessage(this, "返回数据异常", false);
            return;
        }
        switch (Integer.parseInt(param[1].toString())) {
            case RequestID.REQ_SAVE_RECEIVING_ORDER:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        ToastUtil.showMessage(this, "保存成功");
                        setResult(1000, null);
                        finish();
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
