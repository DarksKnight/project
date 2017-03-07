package com.express56.xq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.express56.xq.R;

/**
 * Created by bojoy-sdk2 on 2017/3/7.
 */

public class MyAccountActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout llRechargeRecord = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        init();
    }

    @Override
    protected void initView() {
        super.initView();

        llRechargeRecord = getView(R.id.ll_recharge_record);

        llRechargeRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == llRechargeRecord) {
            startActivity(new Intent(this, RechargeRecordActivity.class));
        }
    }
}
