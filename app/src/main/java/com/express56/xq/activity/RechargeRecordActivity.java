package com.express56.xq.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.andview.refreshview.XRefreshView;
import com.express56.xq.R;
import com.express56.xq.adapter.RechargeRecordAdapter;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.RechargeRecordInfo;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONArray;
import alibaba.fastjson.JSONObject;

/**
 * Created by bojoy-sdk2 on 2017/3/6.
 */

public class RechargeRecordActivity extends BaseActivity {

    private final String TAG = RechargeRecordActivity.class.getSimpleName();

    private RechargeRecordAdapter adapter = null;
    private List<RechargeRecordInfo> list = new ArrayList<>();
    private ListView lv = null;
    private XRefreshView rv = null;
    private String pageNo = "1";
    private boolean isRefresh = false;
    private boolean isLoadMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_record);
        init();
    }

    @Override
    protected void initData() {
        super.initData();

        HttpHelper.sendRequest_getRechargeRecordList(this, RequestID.REQ_GET_RECHARGE_LIST, pageNo, sp.getUserInfo().token, dialog);
    }

    @Override
    protected void initView() {
        super.initView();

        adapter = new RechargeRecordAdapter(this, list);
        lv = getView(R.id.lv_recharge_record);
        rv = getView(R.id.rv_recharge_record);

        rv.setPullLoadEnable(true);
        rv.setAutoRefresh(false);
        rv.setXRefreshViewListener(new XRefreshView.XRefreshViewListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                pageNo = "1";
                list.clear();
                HttpHelper.sendRequest_getRechargeRecordList(RechargeRecordActivity.this, RequestID.REQ_GET_RECHARGE_LIST, pageNo, sp.getUserInfo().token, dialog);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                isLoadMore = true;
                pageNo = String.valueOf(Integer.parseInt(pageNo) + 1);
                HttpHelper.sendRequest_getRechargeRecordList(RechargeRecordActivity.this, RequestID.REQ_GET_RECHARGE_LIST, pageNo, sp.getUserInfo().token, dialog);
            }

            @Override
            public void onRelease(float direction) {

            }

            @Override
            public void onHeaderMove(double headerMovePercent, int offsetY) {

            }
        });

        lv.setAdapter(adapter);
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
                            String content = object.getString("result");
                            List<RechargeRecordInfo> temp = JSONArray.parseArray(content, RechargeRecordInfo.class);
                            list.addAll(temp);
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
