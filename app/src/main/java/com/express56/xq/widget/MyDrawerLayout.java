package com.express56.xq.widget;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.express56.xq.util.LogUtil;

/**
 * TODO: document your custom view class.
 */
public class MyDrawerLayout extends DrawerLayout {

    private float mInitialMotionX;
    private float mInitialMotionY;
    private float mInitialMoveMotionX;
    private float mInitialMoveMotionY;
    private float dx;
    private float dy;

    public MyDrawerLayout(Context context) {
        super(context);
    }

    public MyDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return super.onInterceptTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        LogUtil.d("Event -> " , "ev:" + ev.toString());

//        final int action = ev.getAction();
//        switch (action & MotionEventCompat.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN: {
//                final float x = ev.getX();
//                final float y = ev.getY();
//                mInitialMotionX = x;
//                mInitialMotionY = y;
//                break;
//            }
////            case MotionEvent.ACTION_UP: {
////                final float x = ev.getX();
////                final float y = ev.getY();
////                dx = x - mInitialMotionX;
////                dy = y - mInitialMotionY;
////                break;
////            }
//            case MotionEvent.ACTION_MOVE:
//                final float x = ev.getX();
//                final float y = ev.getY();
//
//                dx = x - mInitialMotionX;
//                dy = y - mInitialMotionY;
//                if ((dx > 0 && dy > 0)
//                        && (x > mInitialMoveMotionX || y > mInitialMoveMotionY)) {
//                    return true;
//                }
//                mInitialMoveMotionX = x;
//                mInitialMoveMotionY = y;
//                break;
//            default:
//                break;
//        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
//        return true;
    }
}
