package com.express56.xq.activity;

import com.express56.xq.R;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.MyExpressInfo;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ToastUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by bojoy-sdk2 on 2017/2/9.
 */

public class PlaceOrderShowActivity extends BaseActivity implements View.OnClickListener {

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

    private TextView tvTitleExpressMoney = null;

    private LinearLayout llExpressMoney = null;

    private TextView tvOffer = null;

    private TextView tvTotalMoney = null;

    private LinearLayout llMoney = null;

    private LinearLayout llCompany = null;

    private Button btnPay = null;

    private Dialog dialog = null;

    private TextView tvUserMoney = null;

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
        tvTitleExpressMoney = getView(R.id.tv_place_order_express_money);
        llExpressMoney = getView(R.id.ll_place_order_express_money);
        tvOffer = getView(R.id.tv_place_order_offer);
        tvTotalMoney = getView(R.id.tv_place_order_show_total_money);
        llMoney = getView(R.id.ll_place_order_show_money);
        llCompany = getView(R.id.ll_place_order_show_express_company);
        btnPay = getView(R.id.btn_place_order_show_pay);

        tvOffer.setOnClickListener(this);
        btnPay.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            info = (MyExpressInfo) bundle.getSerializable("info");
        }

        HttpHelper.sendRequest_getOrder(context, RequestID.REQ_GET_ORDER, info.id,
                sp.getUserInfo().token, dialog);
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
            case RequestID.REQ_GET_USER_MONEY:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            tvUserMoney.setText(content + "元");
                            dialog.show();
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
        tvOffer.setText(currentInfo.guotationCount + "个报价");
        tvNumber.setText(currentInfo.orderNo);
        tvCreateDate.setText(currentInfo.createDate);
        tvServiceTime.setText(currentInfo.serviceTime);
        tvCompany.setText(currentInfo.expressCompanyName);
        if (currentInfo.expressCompanyName.equals("")) {
            llCompany.setVisibility(GONE);
        }
        tvSender.setText(currentInfo.sender);
        tvSenderPhone.setText(currentInfo.senderPhone);
        tvSenderAddress.setText(currentInfo.sendDetailAddress);
        tvReceiver.setText(currentInfo.receiver);
        tvReceiverPhone.setText(currentInfo.receiverPhone);
        tvReceiverAddress.setText(currentInfo.receiveAddress);
        tvRemark.setText(currentInfo.remarks);
        tvWeight.setText(currentInfo.weight);
        tvSupportValue
                .setText(currentInfo.isInsurance.equals("1") ? currentInfo.insuranceMoney : "未保价");
        tvSupportCharge
                .setText(currentInfo.isAgentPay.equals("1") ? currentInfo.agentMoney : "不代收货款");
        tvDesc.setText(currentInfo.thingDesc);
        tvMoney.setText(currentInfo.orderMoney + "元");
        tvSupportMoney.setText(currentInfo.insuranceMoney + "元");
        double totalMoney = Double.parseDouble(currentInfo.orderMoney) + Double
                .parseDouble(currentInfo.insuranceMoney);
        tvTotalMoney.setText(totalMoney + "元");
        if (currentInfo.orderStatus.equals(ExpressConstant.EXPRESS_ORDER_NOT_RELEASE)) {
            tvTitleExpressMoney.setVisibility(GONE);
            llExpressMoney.setVisibility(GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == tvOffer) {
            startActivityForResult(new Intent(this, OfferActivity.class), 1000);
        } else if (v == btnPay) {
            createDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1000) {
            llMoney.setVisibility(VISIBLE);
            btnPay.setVisibility(VISIBLE);
        }
    }

    private void createDialog() {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.layout_dialog_pay, null);
        tvUserMoney = (TextView)layout.findViewById(R.id.tv_dialog_pay_money);
        dialog = new Dialog(this, 0);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        HttpHelper.sendRequest_getUserMoney(this, RequestID.REQ_GET_USER_MONEY, sp.getUserInfo().token, dialog);
    }
}
