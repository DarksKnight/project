package com.express56.xq.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.activity.InvokeStaticMethod;
import com.express56.xq.util.DisplayUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/8.
 */

public class MainGridViewAdapter extends BaseAdapter {

    private static final String TAG = MainGridViewAdapter.class.getSimpleName();

    private LayoutInflater inflater = null;// 渲染填充器

    private Context context = null;

    private ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();

    public MainGridViewAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        dataList = list;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.grid_view_func_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.functionImageView);
            holder.textView = (TextView) convertView.findViewById(R.id.nameTextView);
            holder.convertView = convertView;
            int width = DisplayUtil.screenWidth / 4;
            int height = (int) (DisplayUtil.screenWidth / 4 - DisplayUtil.density * 20);
            convertView.setPadding(convertView.getPaddingLeft(),
                    (int)DisplayUtil.density * 24, convertView.getPaddingRight(), (int)DisplayUtil.density * 24);
//            holder.textView.setWidth(width);
//            holder.textView.setHeight(height);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imageName = dataList.get(position).get("iamgeName");
        holder.funcId = Integer.parseInt(dataList.get(position).get("funcId"));
        holder.textView.setText(dataList.get(position).get("functionName"));


        // 功能图标的资源id
        int imageId = context.getResources().getIdentifier(context.getPackageName() + ":drawable/" + holder.imageName, null, null);
        holder.imageView.setImageResource(imageId);

        return convertView;
    }

    public class ViewHolder {

        public int funcId = -1;// 功能id

        public String imageName = null;// 功能图片的名称（不包含后缀）;

        public ImageView imageView = null;// 功能图标显示的控件

        public TextView textView = null;//

        public View convertView = null;

    }

}