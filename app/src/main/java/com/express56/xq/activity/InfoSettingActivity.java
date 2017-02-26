package com.express56.xq.activity;

import com.express56.xq.R;
import com.express56.xq.adapter.SpinnerAdapter;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.CompanyInfo;
import com.express56.xq.model.CompanyItemInfo;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ChoosePlaceLayout;
import com.express56.xq.widget.ToastUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

/**
 * Created by bojoy-sdk2 on 2017/2/20.
 */

public class InfoSettingActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = InfoSettingActivity.class.getSimpleName();

    private Button btnSave = null;
    private ChoosePlaceLayout cpl = null;
    private Spinner spCompany = null;
    private SpinnerAdapter adapter = null;
    private CompanyInfo info = null;
    private TextView tvAreaName = null;

    private String areaCode = "";
    private String areaName = "";
    private String companyId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_setting);
        init();
    }

    @Override
    protected void initView() {
        super.initView();

        btnSave = getView(R.id.btn_info_setting_save);
        spCompany = getView(R.id.sp_info_setting);
        tvAreaName = getView(R.id.tv_info_setting_area_name);
        cpl = getView(R.id.cpl_info_setting);

        tvAreaName.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        cpl.setListener(new ChoosePlaceLayout.ChooseListener() {
            @Override
            public void chooseCompelete(List<String> areaIds, List<String> areaNames, String tag) {
                for (String s : areaIds) {
                    areaCode += s + "_";
                }
                areaCode = areaCode.substring(0, areaCode.length() - 1);
                areaName = "";
                for (String s : areaNames) {
                    areaName += s;
                }
                tvAreaName.setText(areaName);
            }
        });

        spCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                companyId = info.companys.get(position).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        HttpHelper.sendRequest_getExpressCompanyList(this, RequestID.REQ_GET_EXPRESS, sp.getUserInfo().token, dialog);
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
            case RequestID.REQ_GET_EXPRESS:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            info = JSONObject.parseObject(content, CompanyInfo.class);
                            CompanyItemInfo itemInfo = new CompanyItemInfo();
                            itemInfo.name = "请选择";
                            info.companys.addFirst(itemInfo);
                            String[] array = new String[info.companys.size()];
                            int index = 0;
                            for (int i = 0; i < info.companys.size(); i++) {
                                array[i] = info.companys.get(i).name;
                                if (info.companys.get(i).selected) {
                                    index = i;
                                }
                            }
                            adapter = new SpinnerAdapter(this, array);
                            spCompany.setAdapter(adapter);
                            spCompany.setSelection(index);
                            tvAreaName.setText(info.areaName);
                            areaName = info.areaName;
                            areaCode = info.areaCode;
                            if (info.areaName.equals("")) {
                                tvAreaName.setText("请选择");
                            }

                        }
                    } else if (code == 0) {
                        showReloginDialog();
                    } else {
                        showErrorMsg(object);
                    }
                }
                break;
            case RequestID.REQ_SAVE_COMPANY:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            ToastUtil.showMessage(this, content, true);
                            setResult(1000, null);
                            finish();
                        }
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
        if (v == tvAreaName) {
            cpl.show(areaCode, dialog, "");
        } else if (v == btnSave) {
            if (companyId.equals("")) {
                ToastUtil.showMessage(this, "请选择地区");
                return;
            }
            HttpHelper.sendRequest_saveExpressCompany(this, RequestID.REQ_SAVE_COMPANY, companyId, areaCode, areaName, sp.getUserInfo().token, dialog);
        }
    }
}
