package com.express56.xq.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.adapter.SpinnerAdapter;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.CompanyItemInfo;
import com.express56.xq.model.PlaceOrderInfo;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ChoosePlaceLayout;
import com.express56.xq.widget.ToastUtil;
import com.express56.xq.widget.WeightChoose;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

/**
 * Created by bojoy-sdk2 on 17/2/7.
 */

public class PlaceOrderEditActivity extends BaseActivity implements OnDateSetListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = PlaceOrderEditActivity.class.getSimpleName();

    private ChoosePlaceLayout cpl = null;
    private Spinner spExpressCompany = null;
    private TimePickerDialog dateDialog = null;
    private RelativeLayout rlNumberDate = null;
    private TextView tvOrderNumber = null;
    private TextView tvSendItem = null;
    private TextView tvGetItem = null;
    private TextView tvGetItemDate = null;
    private WeightChoose wcWeight = null;
    private Spinner spSupportValue = null;
    private Spinner spSupportCharge = null;
    private Spinner spArrive = null;
    private LinearLayout llSupportValue = null;
    private EditText etSupportValue = null;
    private LinearLayout llSupportCharge = null;
    private EditText etSupportCharge = null;
    private Button btnSave = null;
    private Button btnSaveRelease = null;
    private EditText etSenderName = null;
    private EditText etSenderPhone = null;
    private EditText etReceiverName = null;
    private EditText etReceiverPhone = null;
    private EditText etRemark = null;
    private EditText etDesc = null;
    private EditText etSenderAddressDesc = null;
    private EditText etReceiverAddressDesc = null;
    private EditText etMoney = null;
    private LinearLayout llMoney = null;
    private TextView tvOrderNo = null;
    private TextView tvCreateDate = null;

    private Map<String, String> order = new HashMap<>();
    private String submitType = "1";
    private String[] flags = new String[]{"0", "1"};
    private String[] flagNames = new String[]{"否", "是"};
    private LinkedList<CompanyItemInfo> listCompany = new LinkedList<>();
    private String sendAreaId = "";
    private String sendAreaName = "";
    private String receiverAreaId = "";
    private String receiverAreaName = "";
    private String getItemDate = "";
    private String expressCompanyId = "";
    private String orderId = "";
    private PlaceOrderInfo info = null;
    private String supportValue = "0";
    private String supportCharge = "0";
    private String arrive = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        init();
    }

    @Override
    protected void initView() {
        super.initView();

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            orderId = bundle.getString("orderId");
            HttpHelper.sendRequest_getOrder(this, RequestID.REQ_GET_ORDER, orderId,
                    sp.getUserInfo().token, dialog);
        } else {
            orderId = "";
        }

        btnSave = getView(R.id.btn_place_order_save);
        btnSaveRelease = getView(R.id.btn_place_order_save_release);
        cpl = getView(R.id.cpl);
        llMoney = getView(R.id.ll_place_order_money);
        rlNumberDate = getView(R.id.rl_place_order_number_date);
        tvOrderNumber = getView(R.id.tv_place_order_express_number);
        spExpressCompany = getView(R.id.sp_place_order_edit_express_company);
        tvSendItem = getView(R.id.tv_place_order_send_item);
        tvGetItem = getView(R.id.tv_place_order_get_item);
        tvGetItemDate = getView(R.id.tv_place_order_get_item_date);
        wcWeight = getView(R.id.wc_place_order);
        spSupportValue = getView(R.id.sp_place_order_support_value);
        spSupportCharge = getView(R.id.sp_place_order_support_charge);
        spArrive = getView(R.id.sp_place_order_arrive);
        llSupportValue = getView(R.id.ll_place_order_support_value);
        etSupportValue = getView(R.id.et_place_order_support_value);
        llSupportCharge = getView(R.id.ll_place_order_support_charge);
        etSupportCharge = getView(R.id.et_place_order_support_charge);
        etSenderName = getView(R.id.et_place_order_sender_name);
        etSenderPhone = getView(R.id.et_place_order_sender_phone);
        etReceiverName = getView(R.id.et_place_order_receiver_name);
        etReceiverPhone = getView(R.id.et_place_order_receiver_phone);
        etRemark = getView(R.id.et_place_order_remark);
        etDesc = getView(R.id.et_place_order_desc);
        etSenderAddressDesc = getView(R.id.et_place_order_sender_address);
        etReceiverAddressDesc = getView(R.id.et_place_order_receiver_address);
        etMoney = getView(R.id.et_place_order_express_money);
        tvOrderNo = getView(R.id.tv_place_order_no);
        tvCreateDate = getView(R.id.tv_place_order_createdate);

        SpinnerAdapter sa = new SpinnerAdapter(this, flagNames);
        spSupportValue.setAdapter(sa);
        spSupportCharge.setAdapter(sa);
        spArrive.setAdapter(sa);

        dateDialog = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis())
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();

        if ("".equals(orderId)) {
            rlNumberDate.setVisibility(View.GONE);
            tvOrderNumber.setVisibility(View.GONE);
        }

        tvGetItemDate.setOnClickListener(this);
        tvSendItem.setOnClickListener(this);
        tvGetItem.setOnClickListener(this);
        tvGetItem.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnSaveRelease.setOnClickListener(this);

        spSupportValue.setOnItemSelectedListener(this);
        spSupportCharge.setOnItemSelectedListener(this);
        spExpressCompany.setOnItemSelectedListener(this);

        cpl.setListener(new ChoosePlaceLayout.ChooseListener() {
            @Override
            public void chooseCompelete(List<String> areaIds, List<String> areaNames, String tag) {
                if (tag.equals("send_item")) {
                    sendAreaId = "";
                    for (String s : areaIds) {
                        sendAreaId += s + "_";
                    }
                    sendAreaId = sendAreaId.substring(0, sendAreaId.length() - 1);
                    sendAreaName = "";
                    for (String s : areaNames) {
                        sendAreaName += s;
                    }
                    tvSendItem.setText(sendAreaName);
                } else if (tag.equals("get_item")) {
                    receiverAreaId = "";
                    for (String s : areaIds) {
                        receiverAreaId += s + "_";
                    }
                    receiverAreaId = receiverAreaId.substring(0, receiverAreaId.length() - 1);
                    receiverAreaName = "";
                    for (String s : areaNames) {
                        receiverAreaName += s;
                    }
                    tvGetItem.setText(receiverAreaName);
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        if (orderId.equals("")) {
            HttpHelper.sendRequest_getExpressCompany(this, RequestID.REQ_GET_EXPRESS_COMPANY, sp.getUserInfo().token, dialog);
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
            case RequestID.REQ_GET_EXPRESS_COMPANY:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            final String content = object.getString("result");
                            listCompany.clear();
                            listCompany.addAll(JSON.parseArray(content, CompanyItemInfo.class));
                            CompanyItemInfo company = new CompanyItemInfo();
                            company.name = "不限";
                            listCompany.addFirst(company);
                            String[] arrayCompany = new String[listCompany.size()];
                            for (int i = 0; i < listCompany.size(); i++) {
                                arrayCompany[i] = listCompany.get(i).name;
                            }
                            SpinnerAdapter adapter = new SpinnerAdapter(this, arrayCompany);
                            spExpressCompany.setAdapter(adapter);
                        }
                    } else if (code == 0) {
                        showReloginDialog();
                    } else {
                        showErrorMsg(object);
                    }
                }
                break;
            case RequestID.REQ_SAVE_ORDER:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if ("".equals(orderId)) {
                            ToastUtil.showMessage(this, "保存成功");
                        } else {
                            ToastUtil.showMessage(this, "修改成功");
                        }
                        setResult(1001, null);
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
                            final String content = object.getString("result");
                            info = JSON.parseObject(content, PlaceOrderInfo.class);
                            initEdit();
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

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(millseconds);
        getItemDate = simpleDateFormat.format(date);
        tvGetItemDate.setText(getItemDate);
    }

    @Override
    public void onClick(View v) {
        if (v == tvGetItemDate) {
            dateDialog.show(getSupportFragmentManager(), "");
        } else if (v == tvSendItem) {
            cpl.show(sendAreaId, dialog, "send_item");
        } else if (v == tvGetItem) {
            cpl.show(receiverAreaId, dialog, "get_item");
        } else if (v == btnSave) {
            submitType = "1";
            save();
        } else if (v == btnSaveRelease) {
            submitType = "2";
            save();
        }
    }

    private void save() {
        if (!"".equals(orderId)) {
            order.put("id", orderId);
        }
        if(getItemDate.trim().length() == 0) {
            ToastUtil.showMessage(this, "请选择取件时间");
            return;
        }
        order.put("serviceTime", getItemDate);
        order.put("remarks", etRemark.getText().toString());
        order.put("isInsurance", supportValue);
        order.put("insuranceMoney", etSupportValue.getText().toString().length() == 0 ? "0" : etSupportValue.getText().toString());
        order.put("isArrivePay", arrive);
        order.put("sendAddress", sendAreaName);
        order.put("sendDetailAddress", etSenderAddressDesc.getText().toString());
        order.put("sendAreaCode", sendAreaId);
        order.put("sender", etSenderName.getText().toString());
        order.put("senderPhone", etSenderPhone.getText().toString());
        order.put("receiveAddress", receiverAreaName);
        order.put("receiveDetailAddress", etReceiverAddressDesc.getText().toString());
        order.put("receiveAreaCode", receiverAreaId);
        order.put("receiver", etReceiverName.getText().toString());
        order.put("receiverPhone", etReceiverPhone.getText().toString());
        order.put("isAgentPay", supportCharge);
        order.put("agentMoney", etSupportCharge.getText().toString().length() == 0 ? "0" : etSupportCharge.getText().toString());
        order.put("expressCompany", expressCompanyId);
        order.put("weight", wcWeight.getWeight() + "");
        order.put("thingDesc", etDesc.getText().toString());
        order.put("orderMoney", etMoney.getText().toString());

        HttpHelper.sendRequest_saveOrder(context, RequestID.REQ_SAVE_ORDER, submitType, order, sp.getUserInfo().token, dialog);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == spExpressCompany) {
            expressCompanyId = listCompany.get(position).id;
        } else if (parent == spSupportValue) {
            supportValue = flags[position];
            if (supportValue.equals("1")) {
                llSupportValue.setVisibility(View.VISIBLE);
            } else {
                llSupportValue.setVisibility(View.GONE);
            }
        } else if (parent == spSupportCharge) {
            supportCharge = flags[position];
            if (supportCharge.equals("1")) {
                llSupportCharge.setVisibility(View.VISIBLE);
            } else {
                llSupportCharge.setVisibility(View.GONE);
            }
        } else if (parent == spArrive) {
            arrive = flags[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if (cpl.isShow()) {
                cpl.hide();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        cpl.dispatchTouchEventA(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void initEdit() {
        listCompany.clear();
        listCompany.addAll(info.companys);
        CompanyItemInfo company = new CompanyItemInfo();
        company.name = "不限";
        listCompany.addFirst(company);
        String[] arrayCompany = new String[listCompany.size()];
        int selectIndex = -1;
        for (int i = 0; i < listCompany.size(); i++) {
            arrayCompany[i] = listCompany.get(i).name;
            if (listCompany.get(i).id.equals(info.order.expressCompany)) {
                selectIndex = i;
            }
        }
        SpinnerAdapter adapter = new SpinnerAdapter(this, arrayCompany);
        spExpressCompany.setAdapter(adapter);
        if (selectIndex != -1) {
            spExpressCompany.setSelection(selectIndex);
        }

        orderId = info.order.id;
        sendAreaId = info.order.sendAreaCode;
        sendAreaName = info.order.sendAddress;
        receiverAreaId = info.order.receiveAreaCode;
        receiverAreaName = info.order.receiveAddress;

        tvOrderNo.setText(info.order.orderNo);
        tvCreateDate.setText(info.order.createDate);
        tvGetItemDate.setText(info.order.serviceTime);
        etSenderName.setText(info.order.sender);
        etSenderPhone.setText(info.order.senderPhone);
        tvSendItem.setText(info.order.sendAddress);
        etSenderAddressDesc.setText(info.order.sendDetailAddress);
        etReceiverName.setText(info.order.receiver);
        etReceiverPhone.setText(info.order.receiverPhone);
        tvGetItem.setText(info.order.receiveAddress);
        etReceiverAddressDesc.setText(info.order.receiveDetailAddress);
        spArrive.setSelection(Integer.parseInt(info.order.isArrivePay));
        spSupportCharge.setSelection(Integer.parseInt(info.order.isAgentPay));
        spSupportValue.setSelection(Integer.parseInt(info.order.isInsurance));
        wcWeight.setWeight(Double.parseDouble(info.order.weight));
        etRemark.setText(info.order.remarks);
        etDesc.setText(info.order.thingDesc);
        etSupportValue.setText(info.order.insuranceMoney);
        getItemDate = info.order.serviceTime;
    }
}
