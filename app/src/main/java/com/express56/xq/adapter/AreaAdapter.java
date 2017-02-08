package com.express56.xq.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.model.AreaInfo;

import java.util.List;

/**
 * Created by bojoy-sdk2 on 17/2/6.
 */

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.AreaViewHolder> {

    private List<AreaInfo> infos = null;
    private Context context = null;
    private AreaAdapterListener listener = null;

    public AreaAdapter(Context context, List<AreaInfo> infos, AreaAdapterListener listener) {
        this.context = context;
        this.infos = infos;
        this.listener = listener;
    }


    @Override
    public AreaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AreaViewHolder holder = new AreaViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_choose_place_list_item, null,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(AreaViewHolder holder, final int position) {
        holder.tvInfo.setText(infos.get(position).name);
        SpannableStringBuilder style =new SpannableStringBuilder(infos.get(position).name);
        if (!infos.get(position).selected) {
            style.setSpan(new ForegroundColorSpan(Color.BLACK), 0, infos.get(position).name.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        } else {
            style.setSpan(new ForegroundColorSpan(Color.RED), 0, infos.get(position).name.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        holder.tvInfo.setText(style);
        holder.llContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.choose(infos.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    class AreaViewHolder extends RecyclerView.ViewHolder {

        public TextView tvInfo = null;
        public LinearLayout llContent = null;

        public AreaViewHolder(View itemView) {
            super(itemView);

            llContent = (LinearLayout)itemView.findViewById(R.id.ll_choose_place_list_item);
            tvInfo = (TextView)itemView.findViewById(R.id.tv_choose_place_list_item);
        }
    }

    public interface AreaAdapterListener {
        void choose(AreaInfo info);
    }
}
