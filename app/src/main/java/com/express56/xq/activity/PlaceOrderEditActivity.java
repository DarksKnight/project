package com.express56.xq.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.ExpressCompany;
import com.express56.xq.model.MyExpressInfo;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ChoosePlaceLayout;
import com.express56.xq.widget.ToastUtil;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

/**
 * Created by bojoy-sdk2 on 17/2/7.
 */

public class PlaceOrderEditActivity extends BaseActivity implements OnDateSetListener, View.OnClickListener {

    private static final String TAG = PlaceOrderEditActivity.class.getSimpleName();
    private ChoosePlaceLayout cpl = null;
    private Spinner spExpressCompany = null;
    private Button btnChooseDate = null;
    private TimePickerDialog dateDialog = null;
    private RelativeLayout rlNumberDate = null;
    private TextView tvOrderNumber = null;
    private Button btnSendItem = null;
    private TextView tvSendItem = null;
    private TextView tvGetItem = null;
    private Button btnGetItem = null;

    private List<ExpressCompany> listCompany = new ArrayList<>();
    private String sendAreaId = "";
    private String sendAreaName = "";
    private String getAreaId = "";
    private String getAreaName = "";
    private String getItemDate = "";
    private MyExpressInfo info = null;

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
            info = (MyExpressInfo) bundle.getSerializable("info");
        }

        cpl = getView(R.id.cpl);
        rlNumberDate = getView(R.id.rl_place_order_number_date);
        tvOrderNumber = getView(R.id.tv_place_order_number);
        spExpressCompany = getView(R.id.sp_place_order_edit_express_company);
        btnChooseDate = getView(R.id.btn_place_order_choose_date);
        btnSendItem = getView(R.id.btn_place_order_send_item);
        tvSendItem = getView(R.id.tv_place_order_send_item);
        btnGetItem = getView(R.id.btn_place_order_get_item);
        tvGetItem = getView(R.id.tv_place_order_get_item);
        dateDialog = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(System.currentTimeMillis() + 60 * 60 * 24 * 365)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();

        if (null == info) {
            rlNumberDate.setVisibility(View.GONE);
            tvOrderNumber.setVisibility(View.GONE);
        }

        btnChooseDate.setOnClickListener(this);
        btnSendItem.setOnClickListener(this);
        btnGetItem.setOnClickListener(this);
        tvGetItem.setOnClickListener(this);

        spExpressCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                    getAreaId = "";
                    for (String s : areaIds) {
                        getAreaId += s + "_";
                    }
                    getAreaId = getAreaId.substring(0, getAreaId.length() - 1);
                    getAreaName = "";
                    for (String s : areaNames) {
                        getAreaName += s;
                    }
                    tvGetItem.setText(getAreaName);
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        HttpHelper.sendRequest_getExpressCompany(this, RequestID.REQ_GET_EXPRESS_COMPANY, sp.getUserInfo().token, dialog);
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
                            listCompany.addAll(JSON.parseArray(content, ExpressCompany.class));
                            String[] arrayCompany = new String[listCompany.size()];
                            for (int i = 0; i < listCompany.size(); i++) {
                                arrayCompany[i] = listCompany.get(i).name;
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayCompany);
                            spExpressCompany.setAdapter(adapter);
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
    }

    @Override
    public void onClick(View v) {
        if (v == btnChooseDate) {
            dateDialog.show(getSupportFragmentManager(), "");
        } else if (v == btnSendItem) {
            cpl.show(sendAreaId, dialog, "send_item");
        } else if (v == btnGetItem) {
            cpl.show(getAreaId, dialog, "get_item");
        }
    }
}
