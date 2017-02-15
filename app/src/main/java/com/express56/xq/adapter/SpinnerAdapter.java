package com.express56.xq.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.express56.xq.R;

/**
 * Created by bojoy-sdk2 on 2017/2/15.
 */

public class SpinnerAdapter extends BaseAdapter {

    private String[] array = null;
    private Activity activity = null;

    public SpinnerAdapter(Activity activity, String[]array) {
        this.activity = activity;
        this.array = array;
    }

    @Override
    public int getCount() {
        return array.length;
    }

    @Override
    public Object getItem(int position) {
        return array[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(activity, R.layout.layout_spinner, null);
            viewHolder = createHolder(convertView);
            convertView.setTag(R.id.tag_spinner, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.id.tag_spinner);
        }
        viewHolder.tvTitle.setText(array[position]);
        return convertView;
    }

    private ViewHolder createHolder(View convertView) {
        final ViewHolder holder = new ViewHolder();
        holder.tvTitle = (TextView)convertView.findViewById(R.id.tv_spinner);
        return holder;
    }

    private class ViewHolder {
        public TextView tvTitle = null;
    }
}
