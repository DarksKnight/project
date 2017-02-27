package com.express56.xq.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.model.OfferInfo;

import java.util.List;

/**
 * Created by apple on 2017/2/26.
 */

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private Context context = null;
    private List<OfferInfo> infos = null;
    private Listener listener = null;

    public OfferAdapter(Context context, List<OfferInfo> infos, Listener listener) {
        this.context = context;
        this.infos = infos;
        this.listener = listener;
    }

    @Override
    public OfferViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        OfferAdapter.OfferViewHolder holder = new OfferAdapter.OfferViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_offer_item, null,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(OfferViewHolder holder, final int i) {
        holder.btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChoose(i);
            }
        });
        holder.tvTotalMoney.setText(infos.get(i).orderMoney);
        holder.tvExpressMoney.setText(infos.get(i).expressMoney);
        holder.tvCompany.setText(infos.get(i).expressCompanyName);
        holder.tvSupportMoney.setText(infos.get(i).insuranceMoney);
        holder.tvRemarks.setText(infos.get(i).remarks);
        holder.tvDate.setText(infos.get(i).publishDate);
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    class OfferViewHolder extends RecyclerView.ViewHolder {

        public TextView tvCompany = null;
        public TextView tvTotalMoney = null;
        public TextView tvExpressMoney = null;
        public TextView tvSupportMoney = null;
        public TextView tvDate = null;
        public Button btnChoose = null;
        public TextView tvRemarks = null;

        public OfferViewHolder(View itemView) {
            super(itemView);

            tvCompany = (TextView)itemView.findViewById(R.id.tv_offer_company);
            tvTotalMoney = (TextView)itemView.findViewById(R.id.tv_offer_total_money);
            tvExpressMoney = (TextView)itemView.findViewById(R.id.tv_offer_express_money);
            tvSupportMoney = (TextView)itemView.findViewById(R.id.tv_offer_support_money);
            tvDate = (TextView)itemView.findViewById(R.id.tv_offer_date);
            btnChoose = (Button)itemView.findViewById(R.id.btn_offer_choose);
            tvRemarks = (TextView)itemView.findViewById(R.id.tv_offer_remarks);
        }
    }

    public interface Listener {
        void onChoose(int index);
    }

}
