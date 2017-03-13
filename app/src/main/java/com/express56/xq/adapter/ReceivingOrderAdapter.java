package com.express56.xq.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.model.MyExpressInfo;

import java.util.List;

/**
 * Created by bojoy-sdk2 on 2017/2/20.
 */

public class ReceivingOrderAdapter extends BaseAdapter {

    private Context context = null;

    private List<MyExpressInfo> infos = null;

    private Listener listener = null;

    public ReceivingOrderAdapter(Context context, List<MyExpressInfo> infos, Listener listener) {
        this.context = context;
        this.infos = infos;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public Object getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.layout_receiving_order_item, null);
            viewHolder = createHolder(convertView);
            convertView.setTag(R.id.tag_receiving_order, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.id.tag_receiving_order);
        }
        bindHolder(viewHolder, position);
        return convertView;
    }

    private void bindHolder(final ReceivingOrderAdapter.ViewHolder holder, final int position) {
        holder.llContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(infos.get(position).id);
            }
        });
        holder.tvOrderNo.setText(infos.get(position).orderNo);
        String status = "";
        if (infos.get(position).orderStatus.equals("") || infos.get(position).orderStatus.equals("2")) {
            status = "待报价";
        } else if (infos.get(position).orderStatus.equals("3")) {
            status = "已报价";
        }
        holder.tvStatus.setText(status);
        holder.tvCreateDate.setText(infos.get(position).createDate);
        holder.tvSender.setText(infos.get(position).sender);
        holder.tvSenderPhone.setText(infos.get(position).senderPhone);
        holder.tvSenderAddress.setText(infos.get(position).sendAddress + infos.get(position).sendDetailAddress);
        holder.tvMoney.setText("费用：" + infos.get(position).orderMoney + "元");
    }

    private ViewHolder createHolder(View itemView) {
        final ViewHolder holder = new ViewHolder();
        holder.llContent = (LinearLayout) itemView.findViewById(R.id.ll_receiving_order_content);
        holder.tvOrderNo = (TextView) itemView.findViewById(R.id.tv_receiving_order_item_number);
        holder.tvStatus = (TextView) itemView.findViewById(R.id.tv_receiving_order_item_status);
        holder.tvCreateDate = (TextView) itemView
                .findViewById(R.id.tv_receiving_order_item_createdate);
        holder.tvSender = (TextView) itemView.findViewById(R.id.tv_receiving_order_item_sender);
        holder.tvSenderPhone = (TextView) itemView
                .findViewById(R.id.tv_receiving_order_item_sender_phone);
        holder.tvSenderAddress = (TextView) itemView
                .findViewById(R.id.tv_receiving_order_item_sender_address);
        holder.tvMoney = (TextView) itemView.findViewById(R.id.tv_receiving_order_item_money);
        return holder;
    }

    private class ViewHolder {
        public LinearLayout llContent = null;

        public TextView tvOrderNo = null;

        public TextView tvStatus = null;

        public TextView tvCreateDate = null;

        public TextView tvSender = null;

        public TextView tvSenderPhone = null;

        public TextView tvSenderAddress = null;

        public TextView tvMoney = null;
    }

    public interface Listener {

        void onClick(String orderId);
    }
}
