package com.express56.xq.adapter;

import com.express56.xq.R;
import com.express56.xq.model.MyExpressInfo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by bojoy-sdk2 on 2017/2/20.
 */

public class ReceivingOrderAdapter extends RecyclerView.Adapter<ReceivingOrderAdapter.ReceivingOrderViewHolder> {

    private Context context = null;
    private List<MyExpressInfo> infos = null;
    private Listener listener = null;

    public ReceivingOrderAdapter(Context context, List<MyExpressInfo> infos, Listener listener) {
        this.context = context;
        this.infos = infos;
        this.listener = listener;
    }

    @Override
    public ReceivingOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ReceivingOrderAdapter.ReceivingOrderViewHolder holder = new ReceivingOrderAdapter.ReceivingOrderViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_receiving_order_item, null,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ReceivingOrderViewHolder holder, final int position) {
        holder.llContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(infos.get(position).id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    class ReceivingOrderViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout llContent = null;

        public ReceivingOrderViewHolder(View itemView) {
            super(itemView);

            llContent = (LinearLayout)itemView.findViewById(R.id.ll_receiving_order_content);
        }
    }

    public interface Listener {
        void onClick(String orderId);
    }
}
