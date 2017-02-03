package com.express56.xq.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.express56.xq.R;
import com.express56.xq.adapter.MyExpressAdapter;
import com.express56.xq.model.MyExpressInfo;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bojoy-sdk2 on 17/2/3.
 */

public class MyExpressActivity extends BaseActivity {

    private PullToRefreshListView mListView = null;

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
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
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

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }
}
