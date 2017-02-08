package com.express56.xq.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.widget.ChoosePlaceLayout;

import java.util.List;

/**
 * Created by bojoy-sdk2 on 17/2/7.
 */

public class PlaceOrderActivity extends BaseActivity {

    private static final String TAG = PlaceOrderActivity.class.getSimpleName();
    private Button btn = null;
    private TextView tv = null;
    private ChoosePlaceLayout cpl = null;
    private String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        init();
    }

    @Override
    protected void initView() {
        super.initView();

        btn = getView(R.id.btnClick);
        cpl = getView(R.id.cpl);
        tv = getView(R.id.tv);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cpl.show(str, dialog);
            }
        });

        cpl.setListener(new ChoosePlaceLayout.ChooseListener() {
            @Override
            public void chooseCompelete(List<String> areaIds) {
                str = "";
                for(String s : areaIds) {
                    str += s + "_";
                }
                str = str.substring(0, str.length() -1);
                tv.setText(str);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void doHttpResponse(Object... param) {
        super.doHttpResponse(param);
    }
}
