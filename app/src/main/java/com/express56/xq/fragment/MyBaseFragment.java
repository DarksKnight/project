package com.express56.xq.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.model.User;
import com.express56.xq.util.SharedPreUtils;

/**
 * Created by Administrator on 2016/11/17.
 */

public class MyBaseFragment extends Fragment implements View.OnClickListener {

    protected SharedPreUtils sp;
    protected User user = null;


    /**
     * 第一次点击操作时间
     */
    protected long first_click_time = 0;
    protected long clickFirstTime = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void init(View rootView) {
        initView(rootView);
        initData();
    }

    protected void initView(View rootView) {

    }

    protected void initData() {
        sp = new SharedPreUtils(getActivity().getApplicationContext());
        clickFirstTime = 0;
        user = sp.getUserInfo();
    }

    protected boolean checkFastClick() {
        long current_time = System.currentTimeMillis();
        if (current_time - first_click_time < ExpressConstant.DOUBLE_CLICK_TIME_INTERVAL) {
            return true;
        }
        first_click_time = current_time;
        return false;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {

    }
}
