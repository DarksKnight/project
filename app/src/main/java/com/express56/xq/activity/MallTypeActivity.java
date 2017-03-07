package com.express56.xq.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.express56.xq.R;
import com.express56.xq.adapter.MallTypeAdapter;
import com.express56.xq.model.MallTypeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bojoy-sdk2 on 2017/3/6.
 */

public class MallTypeActivity extends BaseActivity {

    private RecyclerView rv = null;
    private MallTypeAdapter adapter = null;
    private List<MallTypeInfo> list = new ArrayList<>();
    private String[] arrayTitle = new String[]{"防水袋", "电子秤", "扫描枪", "手持终端", "大头笔", "验视章", "封箱胶带", "打印机", "信封", "输送带", "名片", "摄像头", "纸箱", "快递车辆", "路由器", "快递包", "服装", "手机", "运单扫描仪", "运单系统", "安检机"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_type);
        init();
    }

    @Override
    protected void initView() {
        super.initView();

        rv = getView(R.id.rv_mall_type);

        for(int i = 0; i < arrayTitle.length; i++) {
            MallTypeInfo info = new MallTypeInfo();
            info.title = arrayTitle[i];
            list.add(info);
        }
        adapter = new MallTypeAdapter(this, list);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
    }
}
