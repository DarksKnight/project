package com.express56.xq.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.model.AreaInfo;

import java.util.List;

/**
 * Created by bojoy-sdk2 on 17/2/6.
 */

public class ChoosePlaceItemLayout extends LinearLayout {

    private TextView tvInfo = null;
    private LinearLayout llContent = null;
    private AreaInfo areaInfo = null;
    private List<AreaInfo> listAreaInfos = null;
    private ChoosePlaceItemListener listener = null;
    private int index = -1;

    public ChoosePlaceItemLayout(Context context) {
        this(context, null);
    }

    public ChoosePlaceItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_choose_place_item, this, false);
        addView(view);

        tvInfo = (TextView)findViewById(R.id.tv_choose_place_item);
        llContent = (LinearLayout)findViewById(R.id.ll_choose_place_item);

        llContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.choose(listAreaInfos);
            }
        });
    }

    public void setAreaInfo(AreaInfo areaInfo) {
        this.areaInfo = areaInfo;
        tvInfo.setText(areaInfo.areaName);
    }

    public void setListAreaInfos(List<AreaInfo> listAreaInfos) {
        this.listAreaInfos = listAreaInfos;
    }

    public void setListener(ChoosePlaceItemListener listener) {
        this.listener = listener;
    }

    public interface ChoosePlaceItemListener{
        void choose(List<AreaInfo> listAreaInfos);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
