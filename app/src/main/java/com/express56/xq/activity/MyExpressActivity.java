package com.express56.xq.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import com.andview.refreshview.XRefreshView;
import com.express56.xq.R;
import com.express56.xq.adapter.MyExpressAdapter;
import com.express56.xq.model.MyExpressInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bojoy-sdk2 on 17/2/3.
 */

public class MyExpressActivity extends BaseActivity {

    private ListView mListView = null;
    private XRefreshView rv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myexpress);
        init();
    }

    @Override
    protected void initView() {
        super.initView();

        mListView = getView(R.id.listView_my_express);
        rv = getView(R.id.rv_my_express);
        rv.setPullLoadEnable(true);
        rv.setAutoRefresh(false);
        List<MyExpressInfo> expressInfos = new ArrayList<>();
        for(int i = 0; i < 20; i++) {
            MyExpressInfo info = new MyExpressInfo();
            info.expressNo = "123456" + i;
            info.createDate = "asdfasdf";
            info.address = "撒旦法阿斯蒂芬";
            info.person = "阿斯蒂芬阿斯蒂芬啊";
            info.status = "未完成" + i;
            expressInfos.add(info);
        }
        MyExpressAdapter adapter = new MyExpressAdapter(this, expressInfos);
        mListView.setAdapter(adapter);

        rv.setXRefreshViewListener(new XRefreshView.XRefreshViewListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rv.stopRefresh();
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        rv.stopLoadMore();
                    }
                }, 2000);
            }

            @Override
            public void onRelease(float direction) {

            }

            @Override
            public void onHeaderMove(double headerMovePercent, int offsetY) {

            }
        });
    }
}
