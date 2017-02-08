package com.express56.xq.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.express56.xq.R;
import com.express56.xq.adapter.AreaPriceAdapter;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.AreaPriceInfo;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.SharedPreUtils;
import com.express56.xq.widget.CustomListView;
import com.express56.xq.widget.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONArray;
import alibaba.fastjson.JSONObject;

/**
 * Created by bojoy-sdk2 on 17/2/4.
 */

public class AreaPriceSetActivity extends BaseActivity {

    private static final String TAG = AreaPriceSetActivity.class.getSimpleName();

    private CustomListView lv = null;
    private Button btnSave = null;
    private List<AreaPriceInfo> infos = new ArrayList<>();
    private AreaPriceAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_price_set);
        init();
    }

    @Override
    protected void initView() {
        super.initView();

        lv = getView(R.id.lv_area_price);
        btnSave = getView(R.id.btn_area_price_save);

        adapter = new AreaPriceAdapter(this, infos, new AreaPriceAdapter.AreaPriceListener() {
            @Override
            public void firstTextChanged(String text, int position) {
                infos.get(position).price1 = text;
            }

            @Override
            public void nextTextChanged(String text, int position) {
                infos.get(position).price2 = text;
            }
        });
        lv.setAdapter(adapter);
        lv.setFocusable(false);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpHelper.sendRequest_saveAreaPrice(context, RequestID.REQ_GET_AREA_PRICE_SAVE, sp.getUserInfo().token, infos, dialog);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        if (sp == null) sp = new SharedPreUtils(context);
        HttpHelper.sendRequest_getAreaPrice(context, RequestID.REQ_GET_AREA_PRICE, sp.getUserInfo().token, dialog);
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
            case RequestID.REQ_GET_AREA_PRICE:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            List<AreaPriceInfo> tempData = JSONArray.parseArray(content, AreaPriceInfo.class);
                            infos.clear();
                            infos.addAll(tempData);
                            adapter.notifyDataSetChanged();
                        }
                    } else if (code == 0) {
                        showReloginDialog();
                    } else {
                        showErrorMsg(object);
                    }
                }
                break;
            case RequestID.REQ_GET_AREA_PRICE_SAVE:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            ToastUtil.showMessage(this, content, true);
                        }
                    } else if (code == 0) {
                        showReloginDialog();
                    }else {
                        showErrorMsg(object);
                    }
                }
                break;
            default:
                break;
        }
    }
}
