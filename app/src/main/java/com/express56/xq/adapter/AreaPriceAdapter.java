package com.express56.xq.adapter;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.model.AreaPriceInfo;
import com.express56.xq.util.LogUtil;

import java.util.List;

/**
 * Created by bojoy-sdk2 on 17/2/4.
 */

public class AreaPriceAdapter extends BaseAdapter {

    private List<AreaPriceInfo> listAreaPriceInfos = null;
    private Activity activity = null;
    private AreaPriceListener listener = null;

    public AreaPriceAdapter(Activity activity, List<AreaPriceInfo> listAreaPriceInfos, AreaPriceListener listener) {
        this.activity = activity;
        this.listAreaPriceInfos = listAreaPriceInfos;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return listAreaPriceInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return listAreaPriceInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(activity, R.layout.area_price_list_item, null);
            viewHolder = createHolder(convertView);
            convertView.setTag(R.id.tag_area_price_set, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.id.tag_area_price_set);
        }
        bindHolder(viewHolder, position);
        return convertView;
    }

    private ViewHolder createHolder(View convertView) {
        final ViewHolder holder = new ViewHolder();
        holder.tvArea = (TextView)convertView.findViewById(R.id.tv_area_price_area);
        holder.etFirstPrice = (EditText)convertView.findViewById(R.id.et_area_price_first_price);
        holder.etNextPrice = (EditText)convertView.findViewById(R.id.et_area_price_next_price);
        return holder;
    }

    private void bindHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.tvArea.setText(listAreaPriceInfos.get(position).areaName);
        viewHolder.etFirstPrice.setText(listAreaPriceInfos.get(position).price1);
        viewHolder.etNextPrice.setText(listAreaPriceInfos.get(position).price2);

        viewHolder.etFirstPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    listener.firstTextChanged(viewHolder.etFirstPrice.getText().toString(), position);
                }
            }
        });

        viewHolder.etNextPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    listener.nextTextChanged(viewHolder.etNextPrice.getText().toString(), position);
                }
            }
        });
    }

    private class ViewHolder {
        public TextView tvArea = null;
        public EditText etFirstPrice = null;
        public EditText etNextPrice = null;
    }

    public interface AreaPriceListener{
        void firstTextChanged(String text, int position);
        void nextTextChanged(String text, int position);
    }
}
