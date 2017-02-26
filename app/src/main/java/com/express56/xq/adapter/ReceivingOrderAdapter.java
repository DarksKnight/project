package com.express56.xq.adapter;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.express56.xq.R;
import com.express56.xq.model.MyExpressInfo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by bojoy-sdk2 on 2017/2/20.
 */

public class ReceivingOrderAdapter
        extends BaseRecyclerAdapter<ReceivingOrderAdapter.ReceivingOrderViewHolder> {

    private Context context = null;

    private List<MyExpressInfo> infos = null;

    private Listener listener = null;

    public ReceivingOrderAdapter(Context context, List<MyExpressInfo> infos, Listener listener) {
        this.context = context;
        this.infos = infos;
        this.listener = listener;
    }

    @Override
    public ReceivingOrderViewHolder getViewHolder(View view) {
        return (ReceivingOrderViewHolder)view.getTag();
    }

    @Override
    public ReceivingOrderViewHolder onCreateViewHolder(ViewGroup parent,
            int viewType, boolean isItem) {
        ReceivingOrderAdapter.ReceivingOrderViewHolder holder
                = new ReceivingOrderAdapter.ReceivingOrderViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_receiving_order_item, null,
                        false));
        parent.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReceivingOrderViewHolder holder,
            final int position, boolean isItem) {
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
        holder.tvSenderAddress.setText(infos.get(position).sendAddress);
        holder.tvMoney.setText("费用：" + infos.get(position).orderMoney + "元");
    }

    @Override
    public int getAdapterItemCount() {
        return infos.size();
    }

    class ReceivingOrderViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout llContent = null;

        public TextView tvOrderNo = null;

        public TextView tvStatus = null;

        public TextView tvCreateDate = null;

        public TextView tvSender = null;

        public TextView tvSenderPhone = null;

        public TextView tvSenderAddress = null;

        public TextView tvMoney = null;

        public ReceivingOrderViewHolder(View itemView) {
            super(itemView);

            llContent = (LinearLayout) itemView.findViewById(R.id.ll_receiving_order_content);
            tvOrderNo = (TextView) itemView.findViewById(R.id.tv_receiving_order_item_number);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_receiving_order_item_status);
            tvCreateDate = (TextView) itemView
                    .findViewById(R.id.tv_receiving_order_item_createdate);
            tvSender = (TextView) itemView.findViewById(R.id.tv_receiving_order_item_sender);
            tvSenderPhone = (TextView) itemView
                    .findViewById(R.id.tv_receiving_order_item_sender_phone);
            tvSenderAddress = (TextView) itemView
                    .findViewById(R.id.tv_receiving_order_item_sender_address);
            tvMoney = (TextView) itemView.findViewById(R.id.tv_receiving_order_item_money);
        }
    }

    public interface Listener {

        void onClick(String orderId);
    }
}
