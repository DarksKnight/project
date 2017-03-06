package com.express56.xq.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.model.RechargeRecordInfo;

import java.util.List;

/**
 * Created by bojoy-sdk2 on 2017/3/6.
 */

public class RechargeRecordAdapter extends BaseAdapter {

    private Context context = null;
    private List<RechargeRecordInfo> list = null;

    public RechargeRecordAdapter(Context context, List<RechargeRecordInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.layout_recharge_record_item, null);
            viewHolder = createHolder(convertView);
            convertView.setTag(R.id.tag_recharge_record_item, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.id.tag_recharge_record_item);
        }
        bindHolder(viewHolder, position);
        return convertView;
    }

    private void bindHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.tvTitle.setText("￥" + list.get(position).rechargeMoney + "送" + list.get(position).rechargeGiveaway);
        viewHolder.tvStatus.setText(list.get(position).status.equals("0") ? "处理中" : "已完成");
        viewHolder.tvDate.setText(list.get(position).createDate);
    }

    private ViewHolder createHolder(View convertView) {
        final ViewHolder holder = new ViewHolder();
        holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_recharge_record_title);
        holder.tvStatus = (TextView) convertView.findViewById(R.id.tv_recharge_record_status);
        holder.tvDate = (TextView) convertView.findViewById(R.id.tv_recharge_record_date);
        return holder;
    }

    private class ViewHolder {
        public TextView tvTitle = null;
        public TextView tvStatus = null;
        public TextView tvDate = null;
    }
}
