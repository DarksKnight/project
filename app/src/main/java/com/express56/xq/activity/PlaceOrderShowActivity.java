package com.express56.xq.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.MyExpressInfo;
import com.express56.xq.model.OfferInfo;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ToastUtil;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by bojoy-sdk2 on 2017/2/9.
 */

public class PlaceOrderShowActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = PlaceOrderShowActivity.class.getSimpleName();

    private String orderId = "";

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

    private Dialog dialogPay = null;

    private TextView tvUserMoney = null;

    private TextView tvArrivePay = null;

    private Button btnCancel = null;

    private String payType = "";

    private boolean canPay = true;

    private LinearLayout llNormalAccount = null;
    private LinearLayout llCash = null;
    private LinearLayout llAlipay = null;
    private LinearLayout llWechat = null;

    private TextView llNotEnoughMoney = null;

    private OfferInfo offerInfo = null;

    private String totalMoney = "";

    private String userMoney = "0";

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
        btnCancel = getView(R.id.btn_place_order_show_cancel);
        tvArrivePay = getView(R.id.tv_place_order_show_is_arrive_pay);

        tvOffer.setOnClickListener(this);
        btnPay.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        initDialog();
    }

    @Override
    protected void initData() {
        super.initData();

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            orderId = bundle.getString("orderId");
        }

        HttpHelper.sendRequest_getOrder(context, RequestID.REQ_GET_ORDER, orderId,
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
                            userMoney = content;
                            tvUserMoney.setText(content + "元");
                            dialogPay.show();
                        }
                    } else if (code == 0) {
                        showReloginDialog();
                    } else {
                        showErrorMsg(object);
                    }
                }
                break;
            case RequestID.REQ_ORDER_CANCEL:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            ToastUtil.showMessage(this, "取消成功", true);
                            finish();
                        }
                    } else if (code == 0) {
                        showReloginDialog();
                    } else {
                        showErrorMsg(object);
                    }
                }
                break;
            case RequestID.REQ_QUOTATION_PAY:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");

//                            ToastUtil.showMessage(this, content, true);
//                            finish();
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
        if (currentInfo.isArrivePay.equals("0")) {
            btnPay.setText("确定");
        } else {
            btnPay.setText("支付");
        }

        tvOffer.setText("选择报价（" + currentInfo.quotationCount + "）");
        tvNumber.setText(currentInfo.orderNo);
        tvCreateDate.setText(currentInfo.createDate);
        tvServiceTime.setText(currentInfo.serviceTime);
        tvCompany.setText(currentInfo.expressCompanyName);
        if (currentInfo.expressCompanyName.equals("")) {
            llCompany.setVisibility(GONE);
        }
        tvSender.setText(currentInfo.sender);
        tvSenderPhone.setText(currentInfo.senderPhone);
        tvSenderAddress.setText(currentInfo.sendAddress + currentInfo.sendDetailAddress);
        tvReceiver.setText(currentInfo.receiver);
        tvReceiverPhone.setText(currentInfo.receiverPhone);
        tvReceiverAddress.setText(currentInfo.receiveAddress + currentInfo.receiveDetailAddress);
        tvRemark.setText(currentInfo.remarks);
        tvWeight.setText(currentInfo.weight);
        tvSupportValue
                .setText(currentInfo.isInsurance.equals("1") ? "是，报价费：" + currentInfo.insuranceMoney + "元" : "否");
        tvSupportCharge
                .setText(currentInfo.isAgentPay.equals("1") ? currentInfo.agentMoney : "否");
        tvArrivePay.setText(currentInfo.isArrivePay.equals("1") ? "是" : "否");
        tvDesc.setText(currentInfo.thingDesc);
        tvMoney.setText(currentInfo.expressExpense.equals("") ? "0元" : currentInfo.expressExpense + "元");
        tvSupportMoney.setText(currentInfo.insuranceExpense.equals("") ? "0元" : currentInfo.insuranceExpense + "元");
        tvTotalMoney.setText(currentInfo.orderMoney + "元");
        tvOffer.setText("选择报价（" + currentInfo.quotationCount + "）");
        if (currentInfo.orderStatus.equals(ExpressConstant.EXPRESS_ORDER_NOT_RELEASE)) {
            tvTitleExpressMoney.setVisibility(GONE);
            llExpressMoney.setVisibility(GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == tvOffer) {
            Intent intent = new Intent(this, OfferActivity.class);
            intent.putExtra("orderId", currentInfo.id);
            startActivityForResult(intent, 1000);
        } else if (v == btnPay) {
            createDialog();
        } else if (v == btnCancel) {
            HttpHelper.sendRequest_cancelOrder(context, RequestID.REQ_ORDER_CANCEL, currentInfo.id,
                    sp.getUserInfo().token, dialog);
        } else if (v == llNormalAccount) {
            payType = "4";
            if(canPay) {
                pay();
            }
        } else if (v == llCash) {
            payType = "1";
            pay();
        } else if (v == llAlipay) {
            payType = "2";
            pay();
        } else if (v == llWechat) {
            payType = "3";
            pay();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1000) {
            llMoney.setVisibility(VISIBLE);
            btnPay.setVisibility(VISIBLE);
            offerInfo = (OfferInfo) data.getSerializableExtra("offerInfo");
            if (Double.parseDouble(currentInfo.orderMoney.equals("") ? "0" : currentInfo.orderMoney) > Double.parseDouble(userMoney)) {
                llNotEnoughMoney.setVisibility(VISIBLE);
                canPay = false;
                llNormalAccount.setBackgroundResource(R.color.color_gray);
            } else {
                llNotEnoughMoney.setVisibility(GONE);
                canPay = true;
                llNormalAccount.setBackgroundResource(R.color.color_bg);
            }
            tvMoney.setText(offerInfo.expressMoney + "元");
            tvSupportMoney.setText(offerInfo.insuranceMoney + "元");
            totalMoney = String.valueOf(Double.parseDouble(currentInfo.orderMoney) + Double.parseDouble(offerInfo.expressMoney) + Double.parseDouble(offerInfo.insuranceMoney));
            tvTotalMoney.setText(totalMoney + "元");
        }
    }

    private void pay() {
        HttpHelper.sendRequest_quotationPay(this, RequestID.REQ_QUOTATION_PAY, currentInfo.id, offerInfo.id, totalMoney, offerInfo.expressMoney, payType, offerInfo.insuranceMoney, sp.getUserInfo().token, dialog);
    }

    private void initDialog() {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.layout_dialog_pay, null);

        tvUserMoney = (TextView) layout.findViewById(R.id.tv_dialog_pay_money);
        llNormalAccount = (LinearLayout) layout.findViewById(R.id.ll_normal_account);
        llCash = (LinearLayout) layout.findViewById(R.id.ll_cash);
        llAlipay = (LinearLayout) layout.findViewById(R.id.ll_alipay);
        llWechat = (LinearLayout) layout.findViewById(R.id.ll_wechat);
        llNotEnoughMoney = (TextView) layout.findViewById(R.id.tv_not_enough_money);

        llNormalAccount.setOnClickListener(this);
        llCash.setOnClickListener(this);
        llAlipay.setOnClickListener(this);
        llWechat.setOnClickListener(this);

        dialogPay = new Dialog(this, 0);
        dialogPay.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPay.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogPay.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialogPay.getWindow().setAttributes(lp);
    }

    private void createDialog() {
        HttpHelper.sendRequest_getUserMoney(this, RequestID.REQ_GET_USER_MONEY, sp.getUserInfo().token, dialog);
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
}
