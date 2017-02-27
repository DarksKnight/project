package com.express56.xq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.express56.xq.R;
import com.express56.xq.adapter.OfferAdapter;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.OfferInfo;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONArray;
import alibaba.fastjson.JSONObject;

/**
 * Created by apple on 2017/2/26.
 */

public class OfferActivity extends BaseActivity {

    private final String TAG = OfferActivity.class.getSimpleName();

    private RecyclerView rv = null;
    private OfferAdapter adapter = null;
    private List<OfferInfo> list = new ArrayList<>();
    private String orderId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        init();
    }

    @Override
    protected void initView() {
        super.initView();

        rv = getView(R.id.rv_offer);
    }

    @Override
    protected void initData() {
        super.initData();

        orderId = getIntent().getStringExtra("orderId");

        HttpHelper.sendRequest_getQuotationList(this, RequestID.REQ_GET_QUOTATION_LIST, sp.getUserInfo().token, orderId, dialog);

        adapter = new OfferAdapter(this, list, new OfferAdapter.Listener() {
            @Override
            public void onChoose(int index) {
                Intent intent = new Intent();
                intent.putExtra("offerInfo", list.get(index));
                setResult(1000, intent);
                finish();
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(context));
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
            case RequestID.REQ_GET_QUOTATION_LIST:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            final String content = object.getString("result");
                            List<OfferInfo> tempData = JSONArray.parseArray(content, OfferInfo.class);
                            list.clear();
                            list.addAll(tempData);
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
}
