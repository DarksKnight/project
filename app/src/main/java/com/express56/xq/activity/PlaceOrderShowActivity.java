package com.express56.xq.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.MyExpressInfo;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ToastUtil;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

/**
 * Created by bojoy-sdk2 on 2017/2/9.
 */

public class PlaceOrderShowActivity extends BaseActivity {

    private final String TAG = PlaceOrderShowActivity.class.getSimpleName();

    private MyExpressInfo info = null;
    private MyExpressInfo currentInfo = null;

    private TextView tvNumber = null;
    private TextView tvCreateDate = null;
    private TextView tvServiceTime = null;
    private TextView tvCompany = null;
    private TextView tvSender = null;
    private TextView tvSenderPhone = null;
    private TextView tvSenderAddress = null;
    private TextView tvReceiver = null;
    private TextView tvReceiverPhone = null;
    private TextView tvReceiverAddress = null;
    private TextView tvRemark = null;
    private TextView tvWeight = null;
    private TextView tvSupportValue = null;
    private TextView tvSupportCharge = null;
    private TextView tvDesc = null;
    private TextView tvMoney = null;
    private TextView tvSupportMoney = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order_show);

        init();
    }

    @Override
    protected void initView() {
        super.initView();

        tvNumber = getView(R.id.tv_place_order_show_number);
        tvCreateDate = getView(R.id.tv_place_order_show_create_date);
        tvServiceTime = getView(R.id.tv_place_order_show_serviceTime);
        tvCompany = getView(R.id.tv_place_order_show_company);
        tvSender = getView(R.id.tv_place_order_show_sender);
        tvSenderPhone = getView(R.id.tv_place_order_show_sender_phone);
        tvSenderAddress = getView(R.id.tv_place_order_show_sender_address);
        tvReceiver = getView(R.id.tv_place_order_show_receiver);
        tvReceiverPhone = getView(R.id.tv_place_order_show_receiver_phone);
        tvReceiverAddress = getView(R.id.tv_place_order_show_receiver_address);
        tvRemark = getView(R.id.tv_place_order_show_remark);
        tvWeight = getView(R.id.tv_place_order_show_weight);
        tvSupportValue = getView(R.id.tv_place_order_show_support_value);
        tvSupportCharge = getView(R.id.tv_place_order_show_support_charge);
        tvDesc = getView(R.id.tv_place_order_show_desc);
        tvMoney = getView(R.id.tv_place_order_show_money);
        tvSupportMoney = getView(R.id.tv_place_order_show_support_money);
    }

    @Override
    protected void initData() {
        super.initData();

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            info = (MyExpressInfo) bundle.getSerializable("info");
        }

        HttpHelper.sendRequest_getOrder(context, RequestID.REQ_GET_ORDER, info.id, sp.getUserInfo().token, dialog);
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
            case RequestID.REQ_GET_ORDER:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            currentInfo = JSON.parseObject(JSON.parseObject(content).getString("order"), MyExpressInfo.class);
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
        tvNumber.setText(currentInfo.orderNo);
        tvCreateDate.setText(currentInfo.createDate);
        tvServiceTime.setText(currentInfo.serviceTime);
        tvCompany.setText(currentInfo.expressCompanyName);
        tvSender.setText(currentInfo.sender);
        tvSenderPhone.setText(currentInfo.senderPhone);
        tvSenderAddress.setText(currentInfo.sendDetailAddress);
        tvReceiver.setText(currentInfo.receiver);
        tvReceiverPhone.setText(currentInfo.receiverPhone);
        tvReceiverAddress.setText(currentInfo.receiveAddress);
        tvRemark.setText(currentInfo.remarks);
        tvWeight.setText(currentInfo.weight);
        tvSupportValue.setText(currentInfo.isInsurance.equals("1") ? currentInfo.insuranceMoney : "未保价");
        tvSupportCharge.setText(currentInfo.isAgentPay.equals("1") ? currentInfo.agentMoney : "不代收货款");
        tvDesc.setText(currentInfo.thingDesc);
        tvMoney.setText(currentInfo.orderMoney + "元");
        tvSupportMoney.setText(currentInfo.insuranceMoney + "元");
    }
}
