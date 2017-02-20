package com.express56.xq.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.express56.xq.R;
import com.express56.xq.model.ReceivingOrderInfo;

import java.util.List;

/**
 * Created by bojoy-sdk2 on 2017/2/20.
 */

public class ReceivingOrderAdapter extends RecyclerView.Adapter<ReceivingOrderAdapter.ReceivingOrderViewHolder> {

    private Context context = null;
    private List<ReceivingOrderInfo> infos = null;

    public ReceivingOrderAdapter(Context context, List<ReceivingOrderInfo> infos) {
        this.context = context;
        this.infos = infos;
    }

    @Override
    public ReceivingOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ReceivingOrderAdapter.ReceivingOrderViewHolder holder = new ReceivingOrderAdapter.ReceivingOrderViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_receiving_order_item, null,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ReceivingOrderViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    class ReceivingOrderViewHolder extends RecyclerView.ViewHolder {

        public ReceivingOrderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
