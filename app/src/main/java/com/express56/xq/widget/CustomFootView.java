package com.express56.xq.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.callback.IFooterCallBack;
import com.express56.xq.R;

/**
 * Created by sdk-app-shy on 2017/3/10.
 */

public class CustomFootView extends LinearLayout implements IFooterCallBack {

    public CustomFootView(Context context) {
        this(context, null);
    }

    public CustomFootView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_load_more_complete, this, false);
        addView(view);
    }

    @Override
    public void callWhenNotAutoLoadMore(XRefreshView xRefreshView) {

    }

    @Override
    public void onStateReady() {

    }

    @Override
    public void onStateRefreshing() {

    }

    @Override
    public void onReleaseToLoadMore() {

    }

    @Override
    public void onStateFinish(boolean hidefooter) {

    }

    @Override
    public void onStateComplete() {

    }

    @Override
    public void show(boolean show) {

    }

    @Override
    public boolean isShowing() {
        return false;
    }

    @Override
    public int getFooterHeight() {
        return 0;
    }
}
