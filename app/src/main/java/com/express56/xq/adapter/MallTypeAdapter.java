package com.express56.xq.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.model.MallTypeInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by bojoy-sdk2 on 2017/3/6.
 */

public class MallTypeAdapter extends RecyclerView.Adapter<MallTypeAdapter.MallTypeViewHolder> {

    private Context context = null;
    private List<MallTypeInfo> list = null;

    public MallTypeAdapter(Context context, List<MallTypeInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MallTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MallTypeViewHolder holder = new MallTypeViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_mall_type_item, null,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MallTypeViewHolder holder, int position) {
        holder.tvTitle.setText(list.get(position).title);
        try {
            InputStream is = context.getResources().getAssets().open("malltype/" + list.get(position).icon);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            BitmapDrawable bd = new BitmapDrawable(bitmap);
            holder.ivIcon.setBackground((Drawable)bd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MallTypeViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivIcon = null;
        public TextView tvTitle = null;

        public MallTypeViewHolder(View itemView) {
            super(itemView);

            ivIcon = (ImageView) itemView.findViewById(R.id.iv_mall_type_icon);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_mall_type_title);
        }
    }
}
