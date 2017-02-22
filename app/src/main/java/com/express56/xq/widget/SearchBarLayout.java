package com.express56.xq.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.express56.xq.R;

/**
 * Created by bojoy-sdk2 on 2017/2/21.
 */

public class SearchBarLayout extends LinearLayout {

    public SearchBarLayout(Context context) {
        this(context, null);
    }

    public SearchBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_search_bar, this, false);
        addView(view);

        initView(context);
    }

    private void initView(Context context) {

    }
}
