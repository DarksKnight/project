package com.express56.xq.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.express56.xq.R;

/**
 * Created by bojoy-sdk2 on 2017/2/21.
 */

public class TypeChooseLayoutItem extends LinearLayout {

    private LinearLayout llContent = null;
    private TextView tvTitle = null;
    private View vLine = null;

    private int index = -1;

    public TypeChooseLayoutItem(Context context) {
        this(context, null);
    }

    public TypeChooseLayoutItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_type_choose_item, this, false);
        addView(view);

        initView(context);
    }

    private void initView(Context context) {
        llContent = (LinearLayout) findViewById(R.id.ll_type_choose_item);
        tvTitle = (TextView) findViewById(R.id.tv_type_choose_title);
        vLine = findViewById(R.id.v_type_choose_title_line);

        reset();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void selected() {
        tvTitle.setTextColor(getContext().getResources().getColor(R.color.color_red));
        vLine.setVisibility(View.VISIBLE);
    }

    public void reset() {
        tvTitle.setTextColor(Color.BLACK);
        vLine.setVisibility(View.INVISIBLE);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }
}
