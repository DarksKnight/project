package com.express56.xq.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by SEELE on 2016/10/8.
 */

public class EditTextLinearLayout extends LinearLayout {
    private ScrollView parentScrollview;
    private EditText editText;
    private int showLineMax = 0;

    public void setParentScrollview(ScrollView parentScrollview) {
        this.parentScrollview = parentScrollview;
    }

    public void setEditeText(EditText editText) {
        this.editText = editText;
        EditTextLinearLayout.LayoutParams lp = (EditTextLinearLayout.LayoutParams) editText.getLayoutParams();
        showLineMax = lp.height / editText.getLineHeight();
    }

    public EditTextLinearLayout(Context context) {
        super(context);
    }

    public EditTextLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (parentScrollview == null) {
            return super.onInterceptTouchEvent(ev);
        } else {
            if (ev.getAction() == MotionEvent.ACTION_DOWN && editText.getLineCount() >= showLineMax) {
                // 将父scrollview的滚动事件拦截
                setParentScrollable(false);
            } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                // 把滚动事件恢复给父Scrollview
                setParentScrollable(true);
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 是否把滚动事件交给父scrollview
     *
     * @param flag
     */
    private void setParentScrollable(boolean flag) {
        parentScrollview.requestDisallowInterceptTouchEvent(!flag);
    }
}