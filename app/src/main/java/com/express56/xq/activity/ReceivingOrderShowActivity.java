package com.express56.xq.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.MyExpressInfo;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ToastUtil;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

/**
 * Created by apple on 2017/2/26.
 */

public class ReceivingOrderShowActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = ReceivingOrderShowActivity.class.getSimpleName();

    private String orderId = "";

    private MyExpressInfo currentInfo = null;

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

    private TextView tvSupportValue = null;

    private TextView tvSupportCharge = null;

    private TextView tvOffer = null;

    private TextView tvTotalMoney = null;

    private TextView tvArrivePay = null;

//    private TextView tvRemarks = null;

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
        tvSupportValue = getView(R.id.tv_receive_order_show_show_support_value);
        tvSupportCharge = getView(R.id.tv_receive_order_show_show_support_charge);
        tvTotalMoney = getView(R.id.tv_receive_order_show_show_total_money);
        tvArrivePay = getView(R.id.tv_receiving_order_show_is_arrive_pay);
//        tvRemarks = getView(R.id.tv_receiving_order_show_remark);
    }

    @Override
    protected void initData() {
        super.initData();

        orderId = getIntent().getStringExtra("orderId");

        btnSave = getView(R.id.btn_receive_order_show_show_pay);

        btnSave.setOnClickListener(this);

        if (null != orderId) {
            HttpHelper.sendRequest_getOrder(context, RequestID.REQ_GET_ORDER, orderId,
                    sp.getUserInfo().token, dialog);
        }
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
            case RequestID.REQ_GET_ORDER:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            currentInfo = JSON
                                    .parseObject(JSON.parseObject(content).getString("order"),
                                            MyExpressInfo.class);
                            setData();
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

    private void setData() {
        tvOrderNo.setText(currentInfo.orderNo);
        tvCreateDate.setText(currentInfo.createDate);
        tvReceiveDate.setText(currentInfo.serviceTime);
        tvReceiver.setText(currentInfo.receiver);
        tvReceiverPhone.setText(currentInfo.receiverPhone);
        tvReceiverAddress.setText(currentInfo.receiveAddress + currentInfo.receiveDetailAddress);
        tvSender.setText(currentInfo.sender);
        tvSenderPhone.setText(currentInfo.senderPhone);
        tvSenderAddress.setText(currentInfo.sendAddress + currentInfo.sendDetailAddress);
        tvWeight.setText(currentInfo.weight);
        tvThingDesc.setText(currentInfo.thingDesc);
        tvSupportValue
                .setText(currentInfo.isInsurance.equals("1") ? currentInfo.insuranceMoney : "否");
        tvSupportCharge
                .setText(currentInfo.isAgentPay.equals("1") ? currentInfo.agentMoney : "否");
        etEpressMoney.setText(currentInfo.expressMoney);
        etSupportMoney.setText(currentInfo.insuranceMoney);
        tvTotalMoney.setText(currentInfo.orderMoney + "元");
        tvArrivePay.setText(currentInfo.isArrivePay.equals("1") ? "是" : "否");
//        tvRemarks.setText(currentInfo.remarks);
    }

    @Override
    protected void onStart() {
        super.onStart();

        XGPushClickedResult click = XGPushManager.onActivityStarted(this);
        if (null != click) {
            String content = click.getCustomContent();
            orderId = JSON.parseObject(content).getString("orderId");

            HttpHelper.sendRequest_getOrder(context, RequestID.REQ_GET_ORDER, orderId,
                    sp.getUserInfo().token, dialog);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        XGPushManager.onActivityStoped(this);
    }
}
