package com.express56.xq.activity;

import com.express56.xq.R;
import com.express56.xq.adapter.OfferAdapter;
import com.express56.xq.model.OfferInfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 2017/2/26.
 */

public class OfferActivity extends BaseActivity {

    private RecyclerView rv = null;
    private OfferAdapter adapter = null;
    private List<OfferInfo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        init();
    }

    @Override
    protected void initView() {
        super.initView();

        rv = getView(R.id.rv_offer);
    }

    @Override
    protected void initData() {
        super.initData();

        for(int i = 0; i < 10; i++) {
            OfferInfo info = new OfferInfo();
            list.add(info);
        }
        adapter = new OfferAdapter(this, list, new OfferAdapter.Listener() {
            @Override
            public void onChoose() {
                Intent intent = new Intent();
                setResult(1000, intent);
                finish();
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(adapter);
    }
}
