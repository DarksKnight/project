package com.express56.xq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.adapter.ReceivingOrderAdapter;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.ReceivingOrderInfo;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ToastUtil;
import com.express56.xq.widget.TypeChooseLayout;

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

    private ReceivingOrderAdapter adapter = null;
    private List<ReceivingOrderInfo> infos = null;

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

        List<String> list = new ArrayList<>();
        list.add("全部");
        list.add("待报价");
        list.add("待取件");
        list.add("待退款");
        list.add("已完成");
        tcl.setList(list);
        tcl.select(0);
        tcl.setListener(new TypeChooseLayout.ItemListener() {
            @Override
            public void onClick() {

            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvList.setLayoutManager(manager);
        infos = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            ReceivingOrderInfo info = new ReceivingOrderInfo();
            infos.add(info);
        }
        adapter = new ReceivingOrderAdapter(this, infos);
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
            startActivity(new Intent(this, InfoSettingActivity.class));
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
                            ReceivingOrderInfo tempData = JSONObject.parseObject(content, ReceivingOrderInfo.class);
                            String a = "";
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
