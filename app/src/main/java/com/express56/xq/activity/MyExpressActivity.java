package com.express56.xq.activity;

import com.andview.refreshview.XRefreshView;
import com.express56.xq.R;
import com.express56.xq.adapter.MyExpressAdapter;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.MyExpressInfo;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.CustomFootView;
import com.express56.xq.widget.SearchBarLayout;
import com.express56.xq.widget.ToastUtil;
import com.express56.xq.widget.TypeChooseLayout;
import com.jaeger.library.StatusBarUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

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

    private SearchBarLayout sbl = null;

    private List<MyExpressInfo> expressInfos = new ArrayList<>();

    private MyExpressAdapter adapter = null;

    private boolean isRefresh = false;

    private boolean isLoadMore = false;

    private String orderStatus = "";

    private String pageNo = "1";

    private String keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myexpress);
        init();
    }

    @Override
    protected void initData() {
        super.initData();

        HttpHelper.sendRequest_getOrderList(this, RequestID.REQ_GET_ORDER_LIST,
                sp.getUserInfo().token, orderStatus, keyword, pageNo, dialog);
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
                if (isLoadMore) {
                    rv.stopLoadMore();
                }
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            final String content = object.getString("result");
                            List<MyExpressInfo> tempData = JSONArray.parseArray(content, MyExpressInfo.class);
                            if (tempData.size() == 0) {
                                rv.setLoadComplete(true);
                            } else {
                                expressInfos.addAll(tempData);
                            }
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
        sbl = getView(R.id.ll_myexpress_search);

        sbl.setListener(new SearchBarLayout.Listener() {
            @Override
            public void onSearch(String key) {
                keyword = key;
                expressInfos.clear();
                pageNo = "1";
                HttpHelper.sendRequest_getOrderList(MyExpressActivity.this,
                        RequestID.REQ_GET_ORDER_LIST, sp.getUserInfo().token, orderStatus, keyword,
                        pageNo, dialog);
            }
        });

        List<String> list = new ArrayList<>();
        list.add("全部");
        list.add("未发布");
        list.add("已发布");
        list.add("待评价");
        list.add("退款");
        tcl.setList(list);
        tcl.select(0);
        tcl.setListener(new TypeChooseLayout.ItemListener() {
            @Override
            public void onClick(int index) {
                switch (index) {
                    case 0:
                        orderStatus = "";
                        break;
                    case 1:
                        orderStatus = "1";
                        break;
                    case 2:
                        orderStatus = "2";
                        break;
                    default:
                        break;
                }
                pageNo = "1";
                expressInfos.clear();
                HttpHelper.sendRequest_getOrderList(MyExpressActivity.this,
                        RequestID.REQ_GET_ORDER_LIST, sp.getUserInfo().token, orderStatus, keyword,
                        pageNo, dialog);
            }
        });

        rv.setPullLoadEnable(true);
        rv.setAutoRefresh(false);
        adapter = new MyExpressAdapter(this, expressInfos);
        mListView.setAdapter(adapter);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MyExpressActivity.this, PlaceOrderEditActivity.class), 1001);
            }
        });

        rv.setXRefreshViewListener(new XRefreshView.XRefreshViewListener() {

            @Override
            public void onRefresh() {
                isRefresh = true;
                pageNo = "1";
                expressInfos.clear();
                rv.setLoadComplete(false);
                HttpHelper.sendRequest_getOrderList(MyExpressActivity.this,
                        RequestID.REQ_GET_ORDER_LIST, sp.getUserInfo().token, orderStatus, keyword,
                        pageNo, dialog);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                isLoadMore = true;
                pageNo = String.valueOf(Integer.parseInt(pageNo) + 1);
                HttpHelper.sendRequest_getOrderList(MyExpressActivity.this,
                        RequestID.REQ_GET_ORDER_LIST, sp.getUserInfo().token, orderStatus, keyword,
                        pageNo, dialog);
            }

            @Override
            public void onRelease(float direction) {

            }

            @Override
            public void onHeaderMove(double headerMovePercent, int offsetY) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1001) {
            expressInfos.clear();
            HttpHelper.sendRequest_getOrderList(MyExpressActivity.this,
                    RequestID.REQ_GET_ORDER_LIST, sp.getUserInfo().token, orderStatus, keyword,
                    pageNo, dialog);
        }
    }
}
