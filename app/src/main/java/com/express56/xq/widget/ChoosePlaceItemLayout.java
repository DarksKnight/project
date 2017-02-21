package com.express56.xq.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
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
    private View vLine = null;
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
        vLine = findViewById(R.id.v_choose_place_item);

        llContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.choose(getListAreaInfos(), getIndex());
            }
        });
    }

    public void setAreaInfo(AreaInfo areaInfo) {
        this.areaInfo = areaInfo;
        tvInfo.setText(areaInfo.name);
        resize(areaInfo.name);
    }

    public void setListAreaInfos(List<AreaInfo> listAreaInfos) {
        this.listAreaInfos = listAreaInfos;
    }

    public List<AreaInfo> getListAreaInfos() {
        return listAreaInfos;
    }

    public void setListener(ChoosePlaceItemListener listener) {
        this.listener = listener;
    }

    public interface ChoosePlaceItemListener{
        void choose(List<AreaInfo> listAreaInfos, int index);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void selected(String... text) {
        String str = "请选择";
        if (text.length > 0) {
            str = text[0];
        }
        SpannableStringBuilder style =new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(Color.RED), 0, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tvInfo.setText(style);
        vLine.setVisibility(View.VISIBLE);
        resize(str);
    }

    public void reset(String... text) {
        String str = tvInfo.getText().toString();
        if (text.length > 0) {
            str = text[0];
        }
        SpannableStringBuilder style =new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(Color.BLACK), 0, tvInfo.getText().length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tvInfo.setText(style);
        vLine.setVisibility(View.GONE);
        resize(tvInfo.getText().toString());
    }

    private void resize(String text) {
        TextPaint newPaint = new TextPaint();
        newPaint.setTextSize(getContext().getResources().getDimension(R.dimen.text_14sp));
        float textPaintWidth = newPaint.measureText(text) + getContext().getResources().getDimension(R.dimen.camera_edging_size_vertical);
        setLayoutParams(new LinearLayout.LayoutParams((int)textPaintWidth, LinearLayout.LayoutParams.MATCH_PARENT));
    }
}
