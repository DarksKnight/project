package com.express56.xq.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.model.PaymentItemInfo;

import java.util.List;

/**
 * Created by bojoy-sdk2 on 2017/2/28.
 */

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    private Context context = null;
    private List<PaymentItemInfo> infos = null;
    private Listener listener = null;

    public PaymentAdapter(Context context, List<PaymentItemInfo> infos, Listener listener) {
        this.context = context;
        this.infos = infos;
        this.listener = listener;
    }

    @Override
    public PaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PaymentAdapter.PaymentViewHolder holder = new PaymentAdapter.PaymentViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_payment_info_item, null,
                        false));
        return holder;
    }

    @Override
    public void onBindViewHolder(PaymentViewHolder holder, final int position) {
        holder.tvTitle.setText(infos.get(position).rechargeMoney + "元(" + infos.get(position).giveaway + ")");
        if (infos.get(position).selected) {
            holder.llContent.setBackgroundResource(R.drawable.bg_payment_info_item);
        } else {
            holder.llContent.setBackgroundResource(R.color.color_bg);
        }

        holder.llContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    class PaymentViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle = null;
        public LinearLayout llContent = null;

        public PaymentViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_payment_info_item_title);
            llContent = (LinearLayout) itemView.findViewById(R.id.ll_content);
        }
    }

    public interface Listener{
        void onClick(int index);
    }
}
