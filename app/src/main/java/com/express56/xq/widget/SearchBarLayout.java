package com.express56.xq.widget;

import com.express56.xq.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by bojoy-sdk2 on 2017/2/21.
 */

public class SearchBarLayout extends LinearLayout {

    private EditText etSearch = null;

    private Listener listener = null;

    public SearchBarLayout(Context context) {
        this(context, null);
    }

    public SearchBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_search_bar, this, false);
        addView(view);

        initView(context);
    }

    private void initView(Context context) {
        etSearch = (EditText) findViewById(R.id.et_search_bar);

        etSearch.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER&& event.getAction() == KeyEvent.ACTION_DOWN) {
                    listener.onSearch(etSearch.getText().toString());
                }
                return false;
            }
        });
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {

        void onSearch(String key);
    }
}
