package com.express56.xq.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.activity.InvokeStaticMethod;
import com.express56.xq.activity.MainActivity;
import com.express56.xq.activity.RechargeRecordActivity;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.PersonalInfo;
import com.express56.xq.util.BitmapUtils;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.StringUtils;
import com.express56.xq.widget.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;


public class PersonalFragment extends MyBaseFragment implements View.OnClickListener{

    private static final String TAG = PersonalFragment.class.getSimpleName();

    private static PersonalFragment instance = null;

    private Context context = null;

    private TextView btn_logout = null;

    private TextView textView_nickname = null;
    private TextView textView_account = null;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private PersonalInfo personalInfo;
    private TextView textView_account_normal;
    private TextView textView_account_credit;
    private TextView textView_credit_limit;
    private TextView textView_account_installment;
    private ImageView imageView_portrait;
//    private String mParam2;

    private LinearLayout llRechargeRecord = null;

    public PersonalFragment() {
        // Required empty public constructor
    }

    public void setOnClickListener(FragmentOnClickListener listener) {
        this.listener = listener;
    }

    private FragmentOnClickListener listener = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalFragment newInstance(String param1) {
        PersonalFragment fragment = new PersonalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        personalInfo = MainActivity.mainActivity.getPersonalInfo();
        View rootView = inflater.inflate(R.layout.fragment_personal, container, false);
        init(rootView);
        return rootView;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        context = getActivity();

        btn_logout = (TextView) rootView.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);

        rootView.findViewById(R.id.btn_modify_pwd).setOnClickListener(this);

        textView_nickname = (TextView) rootView.findViewById(R.id.textView_nickname);
        textView_account = (TextView) rootView.findViewById(R.id.textView_account);

        imageView_portrait = (ImageView) rootView.findViewById(R.id.my_personal_imageView);

        textView_account_normal = (TextView) rootView.findViewById(R.id.accout_normal);
        textView_account_credit = (TextView) rootView.findViewById(R.id.accout_credit);
        textView_credit_limit = (TextView) rootView.findViewById(R.id.accout_credit_limit);
        textView_account_installment = (TextView) rootView.findViewById(R.id.accout_installment);

        llRechargeRecord = (LinearLayout) rootView.findViewById(R.id.ll_recharge_record);

        llRechargeRecord.setOnClickListener(this);

        rootView.findViewById(R.id.personal_main_part).setOnClickListener(this);

    }

    @Override
    protected void initData() {
        super.initData();

        HttpHelper.sendRequest_getUserInfo(context, RequestID.REQ_GET_USERINFO, sp.getUserInfo().token, null);
        setData();
    }

    private void setData() {
        loadImageView();
        String tempNicknameStr = null;
        if (sp.getUserInfo().userType == ExpressConstant.USER_TYPE_NORMAL) {
            tempNicknameStr = getString(R.string.str_nickname);
        } else {
            tempNicknameStr = getString(R.string.str_real_name);
        }
        tempNicknameStr = String.format(tempNicknameStr, personalInfo.name);
        textView_nickname.setText(tempNicknameStr);

        String tempAccountStr = getString(R.string.str_account);
        tempAccountStr = String.format(tempAccountStr, personalInfo.phone);
        textView_account.setText(tempAccountStr);

        textView_account_normal.setText("￥" + personalInfo.commonAccount);
        textView_account_credit.setText("￥" + personalInfo.creditAccount);
        textView_credit_limit.setText("￥" + personalInfo.creditLimit);
        textView_account_installment.setText("￥" + personalInfo.installmentAccount);
    }

    public void doHttpResponse(Object... param) {
        String result = (String) param[0];
        LogUtil.d(TAG, "response str=" + result);
        if (result == null) {
            return;
        }
        if (InvokeStaticMethod.isNotJSONstring(getActivity(), result)) {
            return;
        }
        JSONObject object = JSON.parseObject(result);
        if (object == null) {
            ToastUtil.showMessage(getActivity(), "返回数据异常", false);
            return;
        }
        switch (Integer.parseInt(param[1].toString())) {
            case RequestID.REQ_GET_ORDER_LIST:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            final String content = object.getString("result");
                            personalInfo = JSON.parseObject(object.getString("result"), PersonalInfo.class);
                            if (!StringUtils.isEmpty(personalInfo.userPhoto)) {
                                String url = HttpHelper.HTTP + HttpHelper.IP.substring(0, HttpHelper.IP.length() - 0) + "/images/" + personalInfo.userPhoto.replace("\\", "/");
                                personalInfo.userPhoto = url;
                                setData();
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void loadImageView() {
        user = sp.getUserInfo();
        LogUtil.d(TAG, "user.headimg = " + personalInfo.userPhoto);
        LogUtil.d(TAG, "sp.getString(BikeConstant.USER_HEAD_PORTRAIT_PIC_URL_KEY) = " + sp.getString(ExpressConstant.USER_HEAD_PORTRAIT_PIC_URL_KEY));
        if (user != null) {
            if (!StringUtils.isEmpty(personalInfo.userPhoto)) {
                if (TextUtils.isEmpty(sp.getString(ExpressConstant.USER_HEAD_PORTRAIT_PIC_URL_KEY))
                        || !personalInfo.userPhoto.equals(sp.getString(ExpressConstant.USER_HEAD_PORTRAIT_PIC_URL_KEY))
                        ) {
                    sp.setString(ExpressConstant.USER_HEAD_PORTRAIT_PIC_URL_KEY, personalInfo.userPhoto);//更新url
                    HttpHelper.sendRequest_loadHeadPortraitImage(context,
                            RequestID.REQ_DOWNLOAD_PICTURE_PERSONAL_VIEW, personalInfo.userPhoto, imageView_portrait, null);//下载新图片
                } else {
                    File file = new File(sp.getString(ExpressConstant.USER_HEAD_PORTRAIT_PIC_LOCAL_PATH_KEY));
                    try {
                        if (file.exists() && new FileInputStream(file).available() > 0) {
//                            imageView_portrait.setSrcDrawable(BitmapUtils.getBitmapFromFile(file));
                            imageView_portrait.setImageBitmap(BitmapUtils.getBitmapFromFile(file));
                        } else {
                            HttpHelper.sendRequest_loadHeadPortraitImage(context,
                                    RequestID.REQ_DOWNLOAD_PICTURE_PERSONAL_VIEW, personalInfo.userPhoto, imageView_portrait, null);//下载新图片
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        listener.FragmentViewOnClick(v);

        if (v == llRechargeRecord) {
            startActivity(new Intent(getActivity(), RechargeRecordActivity.class));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
