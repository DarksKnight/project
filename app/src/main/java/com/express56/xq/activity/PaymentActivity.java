package com.express56.xq.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.express56.xq.R;
import com.express56.xq.adapter.PaymentAdapter;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.PaymentItemInfo;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONArray;
import alibaba.fastjson.JSONObject;

/**
 * Created by bojoy-sdk2 on 2017/2/23.
 */

public class PaymentActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = PaymentActivity.class.getSimpleName();

    private RecyclerView rv = null;
    private PaymentAdapter adapter = null;
    private List<PaymentItemInfo> list = new ArrayList<>();
    private RelativeLayout rlAliPay = null;
    private RelativeLayout rlWechat = null;
    private PaymentItemInfo info = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_express);
        init();
    }

    @Override
    protected void initData() {
        super.initData();

        HttpHelper.sendRequest_getRechargeList(this, RequestID.REQ_GET_RECHARGE_LIST, sp.getUserInfo().token, dialog);
    }

    @Override
    protected void initView() {
        super.initView();

        rv = getView(R.id.rv_payment);
        rlAliPay = getView(R.id.rl_alipay);
        rlWechat = getView(R.id.rl_wechat);

        rlAliPay.setOnClickListener(this);
        rlWechat.setOnClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rv.setLayoutManager(gridLayoutManager);
        adapter = new PaymentAdapter(this, list, new PaymentAdapter.Listener() {
            @Override
            public void onClick(int index) {
                for(PaymentItemInfo info : list) {
                    info.selected = false;
                }
                list.get(index).selected = true;
                info = list.get(index);
                adapter.notifyDataSetChanged();
            }
        });
        rv.setAdapter(adapter);
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
            case RequestID.REQ_GET_RECHARGE_LIST:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            list.clear();
                            List<PaymentItemInfo> data = JSONArray.parseArray(content, PaymentItemInfo.class);
                            list.addAll(data);
                            adapter.notifyDataSetChanged();
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
    public void onClick(View v) {
        if (v == rlAliPay) {
            if (null == info) {
                ToastUtil.showMessage(this, "请选择充值金额");
                return;
            }
        } else if (v == rlWechat) {
            if (null == info) {
                ToastUtil.showMessage(this, "请选择充值金额");
                return;
            }
        }
    }
}
