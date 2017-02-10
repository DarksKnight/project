package com.express56.xq.activity;

import android.os.Bundle;

import com.express56.xq.R;
import com.express56.xq.widget.ChoosePlaceLayout;

import java.util.List;

/**
 * Created by bojoy-sdk2 on 17/2/7.
 */

public class PlaceOrderEditActivity extends BaseActivity {

    private static final String TAG = PlaceOrderEditActivity.class.getSimpleName();
    private ChoosePlaceLayout cpl = null;
    private String areaStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        init();
    }

    @Override
    protected void initView() {
        super.initView();

        cpl = getView(R.id.cpl);

        cpl.setListener(new ChoosePlaceLayout.ChooseListener() {
            @Override
            public void chooseCompelete(List<String> areaIds) {
                areaStr = "";
                for(String s : areaIds) {
                    areaStr += s + "_";
                }
                areaStr = areaStr.substring(0, areaStr.length() -1);
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
