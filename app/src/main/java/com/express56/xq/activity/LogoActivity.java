package com.express56.xq.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.express56.xq.R;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.Adv;
import com.express56.xq.model.VersionInfo;
import com.express56.xq.util.DisplayUtil;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.StringUtils;
import com.express56.xq.widget.ToastUtil;

import java.io.File;
import java.util.ArrayList;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

public class LogoActivity extends BaseActivity {

    public static final String TAG = LogoActivity.class.getSimpleName();

    /**
     *  apk 安装文件
     */
    private File file = null;

    /**
     * logo
     */
    private ImageView logoIcon = null;

    private ImageView ridingImage = null;

    /**
     *  版本更新信息
     */
    private VersionInfo versionInfo;
    private boolean canEnter;
    private boolean isAnimOver;

    private long time = 0;
    private boolean needGoToLoginView = false;
    private TextView promptWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        isAnimOver = false;
        canEnter = false;
        needGoToLoginView = false;
        time = System.currentTimeMillis();

        DisplayUtil.ini(this);
        setBackKeyActionEnable(false);
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void initView() {
        super.initView();
        logoIcon = (ImageView) findViewById(R.id.logo_view_icon);

//        promptWords = (TextView) findViewById(R.id.logo_prompt_words);
//        promptWords.setVisibility(View.INVISIBLE);
//
//        ridingImage = (ImageView) findViewById(R.id.riding_animation);
//        ridingImage.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void initData() {
        super.initData();
        if (sp.getUserInfo() != null && !StringUtils.isEmpty(sp.getUserInfo().token)) {
            HttpHelper.sendRequest_getAdvertizement(context, RequestID.REQ_GET_ADVERTIZEMENTS, sp.getUserInfo().token, null);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    enter();
                }
            }, 500);
        }
//        ridingImage.setVisibility(View.VISIBLE);
//        AnimationDrawable drawable = (AnimationDrawable) ridingImage.getDrawable();
//        drawable.start();
//        ObjectAnimator transxObjectAnimator = ObjectAnimator.ofFloat(ridingImage, "translationX",
//                0 - InvokeStaticMethod.getViewSize(ridingImage)[0] * 3.0f, DisplayUtil.screenWidth);
//        transxObjectAnimator.setDuration(ExpressConstant.RIDING_BIKE_ANIMATION_INTERVAL);
//        transxObjectAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                super.onAnimationStart(animation);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        doLogoIconAnimation();
//                    }
//                }, 1000);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                isAnimOver = true;
//                enterMapView();
//            }
//        });
//        transxObjectAnimator.start();

    }

    private void enter() {
        user = sp.getUserInfo();
        if (user != null && !StringUtils.isEmpty(user.token)) {
            startActivity(new Intent(context, MainActivity.class));
        } else {
            startActivity(new Intent(context, LoginActivity.class));
        }
        finish();
    }

    /**
     * logo 透明度动画
     */
    private void doLogoIconAnimation() {
        logoIcon.setVisibility(View.VISIBLE);
        promptWords.setVisibility(View.VISIBLE);
        //fade in
        ObjectAnimator alphaObjectAnimator = ObjectAnimator.ofFloat(logoIcon, "alpha", 0, 0.3f, 0.4f, 0.5f, 1);
        alphaObjectAnimator.setDuration(ExpressConstant.ALPHA_ANIMATION_INTERVAL);
        alphaObjectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
        alphaObjectAnimator.start();

    }

    private void gotoLoginView() {
        needGoToLoginView = true;
        canEnter = true;
        if (isAnimOver && canEnter) {
            isAnimOver = false;
            canEnter = false;
            Intent intent = new Intent(LogoActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void doHttpResponse(Object... param) {
        if (this.isFinishing()) {
            return;
        }
        String result = (String) param[0];
        LogUtil.d(TAG, "response str=" + result);
        if (result == null) {
            String errMsg = (String) param[2];
//            if (StringUtils.isEmpty(errMsg)) {//没有错误信息  弹出默认错误提示
            errMsg = getString(R.string.str_net_request_fail);
//            }
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
            switch (Integer.parseInt(param[1].toString())) {
                case RequestID.REQ_DOWNLOAD_APK:
                    exitApp();
                    break;
                default:
                    finish();
                    break;
            }
            return;
        }
        if (InvokeStaticMethod.isNotJSONstring(this, result)) {
            finish();
            return;
        }
        JSONObject object = JSON.parseObject(result);
        if (object == null) {
            ToastUtil.showMessage(this, "登录失败", false);
            return;
        }
        switch (Integer.parseInt(param[1].toString())) {
            case RequestID.REQ_GET_ADVERTIZEMENTS:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            MainActivity.advs.clear();
                            MainActivity.advs.addAll((ArrayList<Adv>) JSON.parseArray(content, Adv.class));
                            for (int i = 0; i < MainActivity.advs.size(); i++) {
                                MainActivity.advs.get(i).image = HttpHelper.HTTP + HttpHelper.IP + "/images/"
                                        + MainActivity.advs.get(i).image;
//                                http://120.195.137.162:82/images/ads/ads1.jpg
                            }
//                                canShowAdv = true;
//                            init();
                        }
                    } else if (code == 0) {
                        showReloginDialog();
                    } else {
                        showErrorMsg(object);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            enter();
                        }
                    }, 500);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 进入主页面
     */
    private void enterMapView() {
        if (needGoToLoginView) {
            gotoLoginView();
            return;
        }
        if (isAnimOver) {
            isAnimOver = false;
            canEnter = false;
            //进入主界面
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
