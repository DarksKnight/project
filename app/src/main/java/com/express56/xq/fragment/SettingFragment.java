package com.express56.xq.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.express56.xq.R;
import com.express56.xq.activity.MainActivity;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.PersonalInfo;
import com.express56.xq.util.BitmapUtils;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends MyBaseFragment {

    private static final String TAG  = SettingFragment.class.getSimpleName();

    private static SettingFragment instance = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    //    private String mParam2;
    private PersonalInfo personalInfo;

    private Context context = null;
    private TextView btn_logout = null;

    private TextView textView_nickname = null;
    private TextView textView_account = null;
    private ImageView settingImageView;
    private RelativeLayout setting_scan;

    public void setOnClickListener(FragmentOnClickListener listener) {
        this.listener = listener;
    }

    private FragmentOnClickListener listener = null;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1) {
        SettingFragment fragment = new SettingFragment();
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
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        init(rootView);
        return rootView;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);

        settingImageView = (ImageView) rootView.findViewById(R.id.setting_imageView);

        btn_logout = (TextView) rootView.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);

        textView_nickname = (TextView) rootView.findViewById(R.id.setting_textView_nickname);
        textView_account = (TextView) rootView.findViewById(R.id.setting_textView_account);

        rootView.findViewById(R.id.setting_part).setOnClickListener(this);

        setting_scan = (RelativeLayout) rootView.findViewById(R.id.setting_scan);
        setting_scan.setOnClickListener(this);

        rootView.findViewById(R.id.setting_about_us).setOnClickListener(this);
        rootView.findViewById(R.id.setting_account_safe).setOnClickListener(this);
        rootView.findViewById(R.id.setting_update_portrait).setOnClickListener(this);

    }

    @Override
    protected void initData() {
        super.initData();
        if (sp.getUserInfo() != null) {
            HttpHelper.sendRequest_getSetting(context, RequestID.REQ_GET_SETTING_INFO, sp.getUserInfo().token, null);
        }
        refreshView();

    }

    public void refreshView() {
        personalInfo = MainActivity.mainActivity.getPersonalInfo();
        if (personalInfo == null) return;
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
        if (personalInfo.introduced) {
            setting_scan.setVisibility(View.GONE);
        } else {
            setting_scan.setVisibility(View.VISIBLE);
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
                            RequestID.REQ_DOWNLOAD_PICTURE_PERSONAL_VIEW, personalInfo.userPhoto, settingImageView, null);//下载新图片
                } else {
                    File file = new File(sp.getString(ExpressConstant.USER_HEAD_PORTRAIT_PIC_LOCAL_PATH_KEY));
                    try {
                        if (file.exists() && new FileInputStream(file).available() > 0) {
//                            imageView_portrait.setSrcDrawable(BitmapUtils.getBitmapFromFile(file));
                            settingImageView.setImageBitmap(BitmapUtils.getBitmapFromFile(file));
                        } else {
                            HttpHelper.sendRequest_loadHeadPortraitImage(context,
                                    RequestID.REQ_DOWNLOAD_PICTURE_PERSONAL_VIEW, personalInfo.userPhoto, settingImageView, null);//下载新图片
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.setting_update_portrait) {
            MainActivity.mainActivity.setProtraitImageView(settingImageView);
        }
        if (listener != null) {
            listener.FragmentViewOnClick(v);
        }
    }
}
