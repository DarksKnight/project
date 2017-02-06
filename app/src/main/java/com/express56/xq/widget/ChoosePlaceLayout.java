package com.express56.xq.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.express56.xq.R;
import com.express56.xq.adapter.AreaAdapter;
import com.express56.xq.model.AreaInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bojoy-sdk2 on 17/2/6.
 */

public class ChoosePlaceLayout extends LinearLayout {

    private LinearLayout llContent = null;
    private RecyclerView rvList = null;
    private ArrayList<AreaInfo> infos = null;
    private AreaAdapter adapter = null;

    public ChoosePlaceLayout(Context context) {
        this(context, null);
    }

    public ChoosePlaceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_choose_place, this, false);
        addView(view);

        initView(context);
    }

    private void initView(final Context context) {
        llContent = (LinearLayout)findViewById(R.id.ll_choose_place);
        rvList = (RecyclerView)findViewById(R.id.rv_choose_place);

        AreaInfo area = new AreaInfo();
        area.areaName = "江苏省";
        ChoosePlaceItemLayout item = createItem(context, area);
        llContent.addView(item);

        infos = new ArrayList<>();
        AreaInfo info = null;
        for(int i = 0; i < 15; i++) {
            info = new AreaInfo();
            info.areaName = "苏州市" + i;
            infos.add(info);
        }
        item.setListAreaInfos((List<AreaInfo>) infos.clone());

        adapter = new AreaAdapter(context, infos, new AreaAdapter.AreaAdapterListener() {
            @Override
            public void choose(AreaInfo info) {
                ChoosePlaceItemLayout item = createItem(context, info);
                llContent.addView(item);

                infos.clear();
                for(int i = 0; i < 15; i++) {
                    info = new AreaInfo();
                    info.areaName = "姑苏区" + i;
                    infos.add(info);
                }
                item.setListAreaInfos((List<AreaInfo>) infos.clone());
                adapter.notifyDataSetChanged();
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(context));
        rvList.setAdapter(adapter);
    }

    private ChoosePlaceItemLayout createItem(Context context, AreaInfo info) {
        final ChoosePlaceItemLayout item = new ChoosePlaceItemLayout(context);
        item.setIndex(item.getIndex() + 1);
        item.setAreaInfo(info);
        item.setListener(new ChoosePlaceItemLayout.ChoosePlaceItemListener() {
            @Override
            public void choose(List<AreaInfo> listAreaInfos) {
                llContent.removeViews(item.getIndex() + 1, llContent.getChildCount() - (item.getIndex() + 1));
                infos.clear();
                infos.addAll(listAreaInfos);
                adapter.notifyDataSetChanged();
            }
        });
        return item;
    }
}
