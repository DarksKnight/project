package com.express56.xq.activity;

import com.express56.xq.R;
import com.express56.xq.adapter.ReceivingOrderAdapter;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.MyExpressInfo;
import com.express56.xq.model.ReceivingOrderInfo;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ToastUtil;
import com.express56.xq.widget.TypeChooseLayout;
import com.kyleduo.switchbutton.SwitchButton;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

/**
 * Created by bojoy-sdk2 on 2017/2/20.
 */

public class ReceivingOrderActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = ReceivingOrderActivity.class.getSimpleName();

    private TextView tvSetting = null;
    private RecyclerView rvList = null;
    private TypeChooseLayout tcl = null;
    private TextView tvCompanyName = null;
    private TextView tvAreaName = null;
    private SwitchButton sbPush = null;

    private ReceivingOrderAdapter adapter = null;
    private List<MyExpressInfo> infos = new ArrayList<>();
    private ReceivingOrderInfo info = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_order);
        init();
    }

    @Override
    protected void initView() {
        super.initView();

        tvSetting = getView(R.id.tv_receiving_order_setting);
        rvList = getView(R.id.rv_receiving_order);
        tcl = getView(R.id.tcl_receiving_order);
        tvCompanyName = getView(R.id.tv_receiving_order_company_name);
        tvAreaName = getView(R.id.tv_receiving_order_area_name);
        sbPush = getView(R.id.sb_push);

        sbPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    HttpHelper.sendRequest_pushOpen(ReceivingOrderActivity.this, RequestID.REQ_OPEN_PUSH, sp.getUserInfo().token, dialog);
                } else {
                    HttpHelper.sendRequest_pushClose(ReceivingOrderActivity.this, RequestID.REQ_CLOSE_PUSH, sp.getUserInfo().token, dialog);
                }
            }
        });

        List<String> list = new ArrayList<>();
        list.add("已报价");
        list.add("待报价");
        list.add("待取件");
        list.add("待退款");
        list.add("已完成");
        tcl.setList(list);
        tcl.select(0);
        tcl.setListener(new TypeChooseLayout.ItemListener() {
            @Override
            public void onClick(int index) {

            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvList.setLayoutManager(manager);
        adapter = new ReceivingOrderAdapter(this, infos, new ReceivingOrderAdapter.Listener() {
            @Override
            public void onClick(String orderId) {
                Intent intent = new Intent(ReceivingOrderActivity.this, ReceivingOrderShowActivity.class);
                intent.putExtra("orderId", orderId);
                startActivityForResult(intent, 1000);
            }
        });
        rvList.setAdapter(adapter);

        tvSetting.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();

        HttpHelper.sendRequest_getReceivingOrder(this, RequestID.REQ_GET_RECEIVING_ORDER, sp.getUserInfo().token, dialog);
    }

    @Override
    public void onClick(View v) {
        if (v == tvSetting) {
            startActivityForResult(new Intent(this, InfoSettingActivity.class), 1000);
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
            case RequestID.REQ_GET_RECEIVING_ORDER:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            info = JSONObject.parseObject(content, ReceivingOrderInfo.class);
                            tvCompanyName.setText(info.companyName);
                            tvAreaName.setText(info.areaName);
                            infos.clear();
                            if (info.orders != null || info.orders.size() > 0) {
                                infos.addAll(info.orders);
                            }
                            if (info.companyName.equals("null")) {
                                tvCompanyName.setText("不限");
                            }
                            if (info.pushFlag.equals("0")) {
                                sbPush.setChecked(false);
                            } else {
                                sbPush.setChecked(true);
                            }
                        }
                    } else if (code == 0) {
                        showReloginDialog();
                    } else {
                        showErrorMsg(object);
                    }
                }
                break;
            case RequestID.REQ_OPEN_PUSH:
            case RequestID.REQ_CLOSE_PUSH:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1000) {
            HttpHelper.sendRequest_getReceivingOrder(this, RequestID.REQ_GET_RECEIVING_ORDER, sp.getUserInfo().token, dialog);
        }
    }
}
