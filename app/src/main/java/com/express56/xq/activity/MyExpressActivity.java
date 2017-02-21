package com.express56.xq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.andview.refreshview.XRefreshView;
import com.express56.xq.R;
import com.express56.xq.adapter.MyExpressAdapter;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.MyExpressInfo;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ToastUtil;
import com.express56.xq.widget.TypeChooseLayout;

import java.util.ArrayList;
import java.util.List;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONArray;
import alibaba.fastjson.JSONObject;

/**
 * Created by bojoy-sdk2 on 17/2/3.
 */

public class MyExpressActivity extends BaseActivity {

    private final String TAG = MyExpressActivity.class.getSimpleName();

    private ListView mListView = null;
    private Button btnPlaceOrder = null;
    private XRefreshView rv = null;
    private TypeChooseLayout tcl = null;

    private List<MyExpressInfo> expressInfos = new ArrayList<>();
    private MyExpressAdapter adapter = null;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myexpress);
        init();
    }

    @Override
    protected void initData() {
        super.initData();

        HttpHelper.sendRequest_getOrderList(this, RequestID.REQ_GET_ORDER_LIST, sp.getUserInfo().token, "1", "1", dialog);
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
            case RequestID.REQ_GET_ORDER_LIST:
                if (isRefresh) {
                    rv.stopRefresh();
                }
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            final String content = object.getString("result");
                            expressInfos.clear();
                            expressInfos.addAll(JSONArray.parseArray(content, MyExpressInfo.class));
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
    protected void initView() {
        super.initView();

        mListView = getView(R.id.listView_my_express);
        btnPlaceOrder = getView(R.id.btn_place_order);
        rv = getView(R.id.rv_my_express);
        tcl = getView(R.id.tcl_myexpress);

        List<String> list = new ArrayList<>();
        list.add("全部");
        list.add("待发布");
        list.add("待付款");
        list.add("待评价");
        list.add("退款");
        tcl.setList(list);
        tcl.select(0);
        tcl.setListener(new TypeChooseLayout.ItemListener() {
            @Override
            public void onClick() {

            }
        });

        rv.setPullLoadEnable(true);
        rv.setAutoRefresh(false);
        adapter = new MyExpressAdapter(this, expressInfos);
        mListView.setAdapter(adapter);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyExpressActivity.this, PlaceOrderEditActivity.class));
            }
        });

        rv.setXRefreshViewListener(new XRefreshView.XRefreshViewListener() {

            @Override
            public void onRefresh() {
                isRefresh = true;
                HttpHelper.sendRequest_getOrderList(MyExpressActivity.this, RequestID.REQ_GET_ORDER_LIST, sp.getUserInfo().token, "1", "1", dialog);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                rv.stopLoadMore();
            }

            @Override
            public void onRelease(float direction) {

            }

            @Override
            public void onHeaderMove(double headerMovePercent, int offsetY) {

            }
        });
    }
}
