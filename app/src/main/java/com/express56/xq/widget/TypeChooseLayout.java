package com.express56.xq.widget;

import com.express56.xq.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bojoy-sdk2 on 2017/2/21.
 */

public class TypeChooseLayout extends LinearLayout {

    private LinearLayout llContent = null;
    private List<TypeChooseLayoutItem> listItem = new ArrayList<>();
    private ItemListener listener = null;

    public TypeChooseLayout(Context context) {
        this(context, null);
    }

    public TypeChooseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_type_choose, this, false);
        addView(view);

        initView(context);
    }

    private void initView(Context context) {
        llContent = (LinearLayout) findViewById(R.id.ll_type_choose_content);
    }

    public void setList(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            final TypeChooseLayoutItem item = new TypeChooseLayoutItem(getContext());
            item.setTitle(list.get(i));
            item.setIndex(i);
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(TypeChooseLayoutItem tclItem : listItem) {
                        tclItem.reset();
                    }
                    item.selected();
                    listener.onClick(item.getIndex());
                }
            });
            listItem.add(item);
            llContent.addView(item);
        }
    }

    public void setListener (ItemListener listener) {
        this.listener = listener;
    }

    public void select(int index) {
        listItem.get(index).selected();
    }

    public interface ItemListener {
        void onClick(int index);
    }
}
