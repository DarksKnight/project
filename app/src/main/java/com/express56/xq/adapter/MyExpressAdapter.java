package com.express56.xq.adapter;

import com.express56.xq.R;
import com.express56.xq.activity.PlaceOrderEditActivity;
import com.express56.xq.activity.PlaceOrderShowActivity;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.model.MyExpressInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        holder.rlBody = (RelativeLayout)convertView.findViewById(R.id.rl_order_express_body);
        holder.tvOrderStatus = (TextView)convertView.findViewById(R.id.tv_order_status);
        holder.tvOrderDate = (TextView)convertView.findViewById(R.id.tv_order_date);
        holder.tvOrderPerson = (TextView)convertView.findViewById(R.id.tv_order_person);
        holder.tvOrderPersonAddress = (TextView)convertView.findViewById(R.id.tv_order_person_address);
        holder.tvOrderPersonPhone = (TextView)convertView.findViewById(R.id.tv_order_person_phone);
        return holder;
    }

    private void bindHolder(final ViewHolder viewHolder, final int position) {
        String status = listMyExpress.get(position).orderStatus;
        if (listMyExpress.get(position).orderStatus.equals(ExpressConstant.EXPRESS_ORDER_NOT_RELEASE)) {
            status = "未发布";
        } else if (listMyExpress.get(position).orderStatus.equals(ExpressConstant.EXPRESS_ORDER_RELEASE)) {
            status = "已发布";
        }
        viewHolder.tvOrderStatus.setText(status);
        viewHolder.tvOrderDate.setText(listMyExpress.get(position).createDate);
        viewHolder.tvOrderPerson.setText("收货人:" + listMyExpress.get(position).receiver);
        viewHolder.tvOrderPersonAddress.setText("收货人地址:" + listMyExpress.get(position).receiveDetailAddress);
        viewHolder.tvOrderPersonPhone.setText("收货人联系电话:" + listMyExpress.get(position).receiverPhone);

        viewHolder.rlBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("orderId", listMyExpress.get(position).id);
                Intent intent = null;
                if (listMyExpress.get(position).orderStatus.equals("1")) {
                    intent = new Intent(activity, PlaceOrderEditActivity.class);
                } else {
                    intent = new Intent(activity, PlaceOrderShowActivity.class);
                }
                intent.putExtras(bundle);
                activity.startActivityForResult(intent, 1001);
            }
        });
    }

    private class ViewHolder {
        public TextView tvOrderStatus = null;
        public TextView tvOrderDate = null;
        public TextView tvOrderPerson = null;
        public TextView tvOrderPersonAddress = null;
        public TextView tvOrderPersonPhone = null;
        public RelativeLayout rlBody = null;
    }
}
