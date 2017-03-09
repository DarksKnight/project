package com.express56.xq.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.express56.xq.R;
import com.express56.xq.adapter.MallTypeAdapter;
import com.express56.xq.model.MallTypeInfo;

import java.util.ArrayList;
import java.util.List;

public class MallFragment extends MyBaseFragment {

    private RecyclerView rv = null;
    private MallTypeAdapter adapter = null;
    private List<MallTypeInfo> list = new ArrayList<>();
    private String[] arrayTitle = new String[]{"防水袋", "电子秤", "扫描枪", "手持终端", "大头笔", "验视章", "封箱胶带", "打印机", "信封", "输送带", "名片", "摄像头", "纸箱", "快递车辆", "路由器", "快递包", "服装", "手机", "运单扫描仪", "运单系统", "安检机"};


    private static MallFragment instance = null;

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    public void setOnClickListener(FragmentOnClickListener listener) {
        this.listener = listener;
    }

    private FragmentOnClickListener listener = null;

    public MallFragment() {
    }
    public static MallFragment newInstance(String param1) {
        MallFragment fragment = new MallFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_mall, container, false);
        init(rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        listener.FragmentViewOnClick(v);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);

        rv = (RecyclerView)rootView.findViewById(R.id.rv_mall_type);

        list.clear();
        for(int i = 0; i < arrayTitle.length; i++) {
            MallTypeInfo info = new MallTypeInfo();
            info.title = arrayTitle[i];
            list.add(info);
        }
        adapter = new MallTypeAdapter(getActivity(), list);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
    }
}
