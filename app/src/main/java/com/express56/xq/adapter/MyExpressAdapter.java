package com.express56.xq.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.model.MyExpressInfo;

import java.util.List;

/**
 * Created by bojoy-sdk2 on 17/2/3.
 */

public class MyExpressAdapter extends BaseAdapter {

    private List<MyExpressInfo> listMyExpress = null;
    private Activity activity = null;

    public MyExpressAdapter(Activity activity, List<MyExpressInfo> listMyExpress) {
        this.activity = activity;
        this.listMyExpress = listMyExpress;
    }

    @Override
    public int getCount() {
        return listMyExpress.size();
    }

    @Override
    public Object getItem(int position) {
        return listMyExpress.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(activity, R.layout.my_express_list_item, null);
            viewHolder = createHolder(convertView);
            convertView.setTag(R.id.tag_my_express, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.id.tag_my_express);
        }
        bindHolder(viewHolder, position);
        return convertView;
    }

    private ViewHolder createHolder(View convertView) {
        final ViewHolder holder = new ViewHolder();
        holder.tvOrderNumber = (TextView)convertView.findViewById(R.id.tv_order_number);
        holder.tvOrderStatus = (TextView)convertView.findViewById(R.id.tv_order_status);
        holder.tvOrderDate = (TextView)convertView.findViewById(R.id.tv_order_date);
        holder.tvOrderPerson = (TextView)convertView.findViewById(R.id.tv_order_person);
        holder.tvOrderAddress = (TextView)convertView.findViewById(R.id.tv_order_address);
        return holder;
    }

    private void bindHolder(final ViewHolder viewHolder, int position) {
        viewHolder.tvOrderNumber.setText(listMyExpress.get(position).expressNo);
        viewHolder.tvOrderStatus.setText(listMyExpress.get(position).status);
        viewHolder.tvOrderDate.setText(listMyExpress.get(position).createDate);
        viewHolder.tvOrderPerson.setText(listMyExpress.get(position).person);
        viewHolder.tvOrderAddress.setText(listMyExpress.get(position).address);
    }

    private class ViewHolder {
        public TextView tvOrderNumber = null;
        public TextView tvOrderStatus = null;
        public TextView tvOrderDate = null;
        public TextView tvOrderPerson = null;
        public TextView tvOrderAddress = null;
    }
}
