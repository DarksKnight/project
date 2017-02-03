package com.express56.xq.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.express56.xq.R;

import com.express56.xq.util.DisplayUtil;
import com.express56.xq.util.LogUtil;

/**
 * slide button for unlock or lock action
 */
public class SlideButtonView extends RelativeLayout implements View.OnTouchListener, View.OnClickListener, Animation.AnimationListener {

    private static final String TAG = SlideButtonView.class.getSimpleName();

    private String mPromptString; // TODO: use a default from R.string...

    private int mPromptColor = Color.BLACK; // TODO: use a default from R.color...
    private int textSize = 16; // TODO: use a default from R.dimen...

    public void setArrowDrawable(Drawable arrowDrawable) {
        this.mArrowDrawable = arrowDrawable;
        refreshArrow();
    }

    private Drawable mArrowDrawable;
    private Drawable mLeftDrawable;
    private Drawable mLeftPressedDrawable;
    private Drawable mRightDrawable;

    public void setLeftPressedDrawable(Drawable leftPressedDrawable) {
        this.mLeftPressedDrawable = leftPressedDrawable;
    }

    public void setRightDrawable(Drawable mRightDrawable) {
        this.mRightDrawable = mRightDrawable;
        invalidateAndMeasurements();
    }

    private Context context;

    private ImageView leftImageView;
    private TextView promptTV;
    private OnSlideListener slideListener = null;

    /**
     * X方向起始位置
     */
    private float touch_down_X = 0;

    /**
     * X方向移动终止位置
     */
    private float touch_up_X = -1;

    /**
     * 移动操作最小有效距离
     */
    private float moveActionMinDistance = 60;

    private int leftMargin;

    public void setRightMargin(int rightMargin) {
        this.rightMargin = rightMargin;
    }

    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    private int rightMargin;


    private ImageView rightImageView;

    /**
     * 可移动图片初始X坐标
     */
    private float leftBtn_X;

    private int maxMoveDistance;
    private boolean first = false;
    private TranslateAnimation animationLeft;

    private TranslateAnimation animationRight;
    public SlideButtonView(Context context) {
        super(context);
        this.context = context;
        first = true;
        init(null, 0);
    }

    public SlideButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        first = true;
        init(attrs, 0);
    }


    public SlideButtonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        first = true;
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SlideButtonView, defStyle, 0);

        rightImageView = new ImageView(context);
        leftImageView = new ImageView(context);
        leftImageView.setOnTouchListener(this);
        leftImageView.setOnClickListener(this);

        promptTV = new TextView(context);
        RelativeLayout.LayoutParams promptlayoutParams =
                new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        promptlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        RelativeLayout.LayoutParams leftButtonLayoutParams =
                new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        leftButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        leftButtonLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);

        RelativeLayout.LayoutParams rightLayoutParams =
                new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rightLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rightLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);

        int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.SlideButtonView_promptString:
                    mPromptString = a.getString(
                            R.styleable.SlideButtonView_promptString);
                    promptTV.setText(mPromptString);
                    break;
                case R.styleable.SlideButtonView_textColor:
                    mPromptColor = a.getColor(
                            R.styleable.SlideButtonView_textColor, mPromptColor);
                    promptTV.setTextColor(mPromptColor);
                    break;
                case R.styleable.SlideButtonView_arrowDrawable:
                    mArrowDrawable = a.getDrawable(
                            R.styleable.SlideButtonView_arrowDrawable);
                    mArrowDrawable.setCallback(this);
                    refreshArrow();
                    break;
                case R.styleable.SlideButtonView_leftButtonDrawable:
                    mLeftDrawable = a.getDrawable(
                            R.styleable.SlideButtonView_leftButtonDrawable);
                    mLeftDrawable.setCallback(this);
                    leftImageView.setImageDrawable(mLeftDrawable);
                    break;
                case R.styleable.SlideButtonView_leftButtonPressedDrawable:
                    mLeftPressedDrawable = a.getDrawable(
                            R.styleable.SlideButtonView_leftButtonPressedDrawable);
                    mLeftPressedDrawable.setCallback(this);
                    break;
                case R.styleable.SlideButtonView_rightDrawable:
                    mRightDrawable = a.getDrawable(
                            R.styleable.SlideButtonView_rightDrawable);
                    rightImageView.setImageDrawable(mRightDrawable);
                    break;
                case R.styleable.SlideButtonView_leftBtnMargin:
                    leftMargin = a.getInteger(R.styleable.SlideButtonView_leftBtnMargin, (int) (3 * DisplayUtil.density));
                    leftButtonLayoutParams.setMargins(leftMargin, leftMargin, leftMargin, leftMargin);
                    break;
                case R.styleable.SlideButtonView_rightBtnMargin:
                    rightMargin = a.getInteger(R.styleable.SlideButtonView_rightBtnMargin, (int) (3 * DisplayUtil.density));
                    rightLayoutParams.setMargins(rightMargin, rightMargin, rightMargin, rightMargin);
                    break;
                case R.styleable.SlideButtonView_textSizeInt:
                    textSize = a.getInteger(
                            R.styleable.SlideButtonView_textSizeInt, textSize);
                    promptTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                    break;
                default:
                    break;
            }
        }
        a.recycle();
        addView(promptTV, promptlayoutParams);
        addView(rightImageView, rightLayoutParams);
        addView(leftImageView, leftButtonLayoutParams);

//        this.setBackgroundResource(R.drawable.bg_btn_slide_lock_control);
//        this.setBackgroundResource(R.drawable.bg_slide_button);

//        if (first) {
//            first = false;
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DisplayUtil.screenWidth * 2 / 3, LayoutParams.WRAP_CONTENT);
//            this.setLayoutParams(layoutParams);
//        }

        leftBtn_X = leftImageView.getX();

    }

    private void refreshArrow() {
        mArrowDrawable.setBounds(0, 0, mArrowDrawable.getMinimumWidth(), mArrowDrawable.getMinimumHeight());
        promptTV.setCompoundDrawables(null, null, mArrowDrawable, null);
        promptTV.setCompoundDrawablePadding((int) (DisplayUtil.density * 10));
    }

    private void invalidateAndMeasurements() {
        this.leftImageView.setImageDrawable(mLeftDrawable);
        this.rightImageView.setImageDrawable(mRightDrawable);
        this.promptTV.setText(mPromptString);
        this.promptTV.setTextColor(mPromptColor);
        this.promptTV.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if (first) {
//            first = false;
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DisplayUtil.screenWidth * 2 / 3, LayoutParams.WRAP_CONTENT);
//            this.setLayoutParams(layoutParams);
//        }
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        if (first) {
//            first = false;
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DisplayUtil.screenWidth * 2 / 3, LayoutParams.WRAP_CONTENT);
//            this.setLayoutParams(layoutParams);
//        }
        //        setMeasuredDimension(DisplayUtil.screenWidth * 2 / 3, this.getMeasuredHeight());
        setMeasuredDimension(getBackground().getIntrinsicWidth(),  getBackground().getIntrinsicHeight());

    }

    /**
     * @param mLeftDrawable
     */
    public void setLeftDrawable(Drawable mLeftDrawable) {
        this.mLeftDrawable = mLeftDrawable;
        invalidateAndMeasurements();
    }

    /**
     * @param mPromptString
     */
    public void setPromptString(String mPromptString) {
        this.mPromptString = mPromptString;
        this.promptTV.setText(mPromptString);
    }

    /**
     * @param mPromptColor
     */
    public void setPromptColor(int mPromptColor) {
        this.mPromptColor = mPromptColor;
        invalidateAndMeasurements();
    }

    /**
     * @param textSize
     */
    public void setPromptTextSize(int textSize) {
        this.textSize = textSize;
        invalidateAndMeasurements();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof ImageView && v.equals(leftImageView)) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    touch_down_X = event.getX();
                    leftImageView.setImageDrawable(mLeftPressedDrawable);
                    ((RelativeLayout.LayoutParams) (leftImageView.getLayoutParams())).setMargins(0, 0, 0, 0);
                    maxMoveDistance = this.getMeasuredWidth() - leftImageView.getMeasuredWidth();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up_X = leftImageView.getX();
                    maxMoveDistance = this.getMeasuredWidth() - leftImageView.getMeasuredWidth() / 2;
                    float nowCenterX = (touch_up_X + leftImageView.getMeasuredWidth() / 2);
                    if (nowCenterX > this.getMeasuredWidth() / 2) {
                        LogUtil.d(TAG, "nowCenterX = " + nowCenterX );
                        LogUtil.d(TAG, "maxMoveDistance = " + maxMoveDistance );
                        if (nowCenterX < maxMoveDistance) {
                            doTranslateAnimationRight(0, maxMoveDistance - nowCenterX, 0);//右移动动画
                        }
                    } else {
                        doTranslateAnimationLeft(0, -(touch_up_X), 0); //左移动动画
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    float moveX = event.getX() - touch_down_X;
                    float newX = leftImageView.getX() + moveX;
                    if (newX > 0) {
                        maxMoveDistance = this.getMeasuredWidth() - leftImageView.getMeasuredWidth();
                        if (newX >= maxMoveDistance) {
                            newX = maxMoveDistance;
                            this.slideListener.onSlide();
                        }
                        leftImageView.setX(newX);
                    } else {
                        leftImageView.setX(0);
                    }
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    private void doTranslateAnimationLeft(float start, float end, float Y) {
        animationLeft = new TranslateAnimation(start, end, Y, Y);
        animationLeft.setDuration(100);//设置动画持续时间
        animationLeft.setRepeatCount(0);//设置重复次数
        animationLeft.setInterpolator(new AccelerateInterpolator());
        animationLeft.setAnimationListener(this);
        animationLeft.setFillAfter(false);
        leftImageView.startAnimation(animationLeft);
    }

    private void doTranslateAnimationRight(float start, float end, float Y) {
        animationRight = new TranslateAnimation(start, end, Y, Y);
        animationRight.setDuration(100);//设置动画持续时间
        animationRight.setRepeatCount(0);//设置重复次数
        animationRight.setInterpolator(new AccelerateInterpolator());
        animationRight.setAnimationListener(this);
        animationRight.setFillAfter(false);
        leftImageView.startAnimation(animationRight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean flag = super.onInterceptTouchEvent(ev);
        return flag;
//        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setSlideListener(OnSlideListener slideListener) {
        this.slideListener = slideListener;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onAnimationStart(Animation animation) {
        LogUtil.d(TAG, "onAnimationStart");
    }

    @Override
    public void onAnimationEnd(final Animation animation) {
        LogUtil.d(TAG, "onAnimationEnd");
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                leftImageView.setImageDrawable(mLeftDrawable);
                ((RelativeLayout.LayoutParams) (leftImageView.getLayoutParams()))
                        .setMargins(leftMargin, leftMargin, leftMargin, leftMargin);
                if (animation.equals(animationLeft)) {
                    leftImageView.setX(leftBtn_X);
                } else {
                    int edge = mLeftPressedDrawable.getIntrinsicWidth()
                            + (mLeftPressedDrawable.getIntrinsicWidth() - mRightDrawable.getIntrinsicWidth()) / 2;
                    leftImageView.setX(SlideButtonView.this.getMeasuredWidth() - edge);
                    //操作成功
                    if (SlideButtonView.this.slideListener != null) {
                        SlideButtonView.this.slideListener.onSlide();//调用接口
                    }
                }
            }
        });
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public interface OnSlideListener {
        void onSlide();
    }
}
