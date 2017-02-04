package com.express56.xq.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.express56.xq.R;
import com.express56.xq.adapter.MainGridViewAdapter;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.fragment.FragmentOnClickListener;
import com.express56.xq.fragment.MainFragment;
import com.express56.xq.fragment.MallFragment;
import com.express56.xq.fragment.PersonalFragment;
import com.express56.xq.fragment.SettingFragment;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.AboutInfo;
import com.express56.xq.model.Adv;
import com.express56.xq.model.PersonalInfo;
import com.express56.xq.model.UpgradeInfo;
import com.express56.xq.model.User;
import com.express56.xq.scan.CaptureActivity;
import com.express56.xq.service.UploadService;
import com.express56.xq.util.BitmapUtils;
import com.express56.xq.util.DisplayUtil;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.PermissionUtil;
import com.express56.xq.util.SharedPreUtils;
import com.express56.xq.util.StringUtils;
import com.express56.xq.util.VersionUtils;
import com.express56.xq.widget.CustomDialog;
import com.express56.xq.widget.CycleViewPager;
import com.express56.xq.widget.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

import static android.view.View.VISIBLE;

public class MainActivity extends UploadUserPortraitActivity implements BottomNavigationBar.OnTabSelectedListener, FragmentOnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static MainActivity mainActivity;

    private ArrayList<Fragment> fragments;

    /**
     * 拍快递单
     */
    public static final int EXPRESS_PHOTO_TAKE = 0;
    private MainFragment mainFragment;
    public static boolean canShowAdv = false;
    private FrameLayout frameLayout;

    public ArrayList<Adv> getAdvs() {
        return advs;
    }

    public static ArrayList<Adv> advs = new ArrayList<>();

    public CycleViewPager getCycleViewPager() {
        return cycleViewPager;
    }

    private CycleViewPager cycleViewPager;

    /**
     * 获取头像imageview
     *
     * @return
     */
    public ImageView getProtraitImageView() {
        return imageView;
    }

    public void setProtraitImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    /**
     * 快递单扫码
     */
    public static final int EXPRESS_PHOTO_SCAN_BARCODE = EXPRESS_PHOTO_TAKE + 1;

    /**
     * 快递单查询
     */
    public static final int EXPRESS_PHOTO_SEARCH = EXPRESS_PHOTO_SCAN_BARCODE + 1;

    /**
     * 充值
     */
    public static final int EXPRESS_PHOTO_RECHARGE = EXPRESS_PHOTO_SEARCH + 1;

    /**
     * 快递下单
     */
    public static final int EXPRESS_PHOTO_ORDER = EXPRESS_PHOTO_RECHARGE + 1;

    /**
     * 快递单拿单
     */
    public static final int EXPRESS_PHOTO_GET_TAKE_ORDER = EXPRESS_PHOTO_ORDER + 1;
    private PersonalFragment personalFragment;
    private SettingFragment settingFragment;
    public boolean hadRefreshHeadPortrait;

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

//    private PersonalInfo personalInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExpressApplication.isForeground = true;
        setContentView(R.layout.activity_tab);
        mainActivity = this;
        sp = new SharedPreUtils(context);
        if (sp.getUserInfo() != null) {
            HttpHelper.sendRequest_getUserInfo(context, RequestID.REQ_GET_USERINFO, sp.getUserInfo().token, null);
        }
//        if (sp.getUserInfo() != null) {
//            HttpHelper.sendRequest_getAdvertizement(context, RequestID.REQ_GET_ADVERTIZEMENTS, sp.getUserInfo().token, null);
//        }
        startService(new Intent(context, UploadService.class));//开启后台上传服务
        init();
    }

    @Override
    protected void initView() {
        super.initView();
        frameLayout = (FrameLayout) findViewById(R.id.advFrame);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        LinearLayout.LayoutParams newLp = new LinearLayout.LayoutParams(lp.width, DisplayUtil.screenWidth * 3 / 5);
        frameLayout.setLayoutParams(newLp);
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);

//        BadgeItem numberBadgeItem = new BadgeItem()
//                .setBorderWidth(4)
//                .setBackgroundColor(Color.RED)
//                .setText("4")
//                .setHideOnSelect(true);

        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_home_white_24dp, "主页").setActiveColorResource(R.color.blue))
                .addItem(new BottomNavigationItem(R.mipmap.ic_book_white_24dp, "我的").setActiveColorResource(R.color.blue))
                .addItem(new BottomNavigationItem(R.mipmap.ic_music_note_white_24dp, "商城").setActiveColorResource(R.color.blue))
//                .addItem(new BottomNavigationItem(R.mipmap.ic_tv_white_24dp, "4").setActiveColorResource(R.color.brown).setBadgeItem(numberBadgeItem))
                .addItem(new BottomNavigationItem(R.mipmap.ic_setting_white_24dp, "设置").setActiveColorResource(R.color.blue))
                .setFirstSelectedPosition(0)
                .setBarBackgroundColor("#00000000")
                .initialise();

//        bottomNavigationBar
//                .setActiveColor(R.color.primary)
//                .setInActiveColor("#FFFFFF")
//                .setBarBackgroundColor("#ECECEC")

        cycleViewPager = new CycleViewPager();

        fragments = getFragments();
        setDefaultFragment();
        bottomNavigationBar.setTabSelectedListener(this);


    }

    /**
     * 设置默认的
     */
    private void setDefaultFragment() {
        android.app.FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        frameLayout.setVisibility(VISIBLE);
        transaction.replace(R.id.advFrame, cycleViewPager);
        transaction.replace(R.id.layFrame, MainFragment.newInstance("主页"));
        MainFragment.newInstance("主页").setOnClickListener(this);
        transaction.commitAllowingStateLoss();
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        mainFragment = MainFragment.newInstance("主页");
        mainFragment.setOnClickListener(this);
        fragments.add(mainFragment);
        personalFragment = PersonalFragment.newInstance("我的");
        personalFragment.setOnClickListener(this);
        fragments.add(personalFragment);
        MallFragment mallFragment = MallFragment.newInstance("商城");
        fragments.add(mallFragment);
        settingFragment = SettingFragment.newInstance("设置");
        settingFragment.setOnClickListener(this);
        fragments.add(settingFragment);

        return fragments;
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onTabSelected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                if (fragment instanceof PersonalFragment && !StringUtils.isEmpty(sp.getUserInfo().token)
                        && personalInfo == null) {
                    HttpHelper.sendRequest_getUserInfo(context, RequestID.REQ_GET_USERINFO_CLICK, sp.getUserInfo().token, dialog);
//                } else if (fragment instanceof SettingFragment && !StringUtils.isEmpty(sp.getUserInfo().token)) {
//                    HttpHelper.sendRequest_getSetting(context, RequestID.REQ_GET_SETTING_INFO_CLICK, sp.getUserInfo().token, null);
                } else {
                    if (fragment instanceof MainFragment) {
                        frameLayout.setVisibility(View.VISIBLE);
                    } else {
                        frameLayout.setVisibility(View.GONE);
                    }
                    if (fragment.isAdded()) {
                        ft.replace(R.id.layFrame, fragment);
                    } else {
                        ft.add(R.id.layFrame, fragment);
                    }
                    ft.commitAllowingStateLoss();
                }
            }
        }
    }

    @Override
    public void onTabUnselected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                ft.remove(fragment);
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabReselected(int i) {

    }

    private void doLogout() {
        HttpHelper.sendRequest_logout(this, RequestID.REQ_LOGOUT, user.token, dialog);
    }

    /**
     * 弹出提示用户更新的对话框
     */
    private void showUpgradeDialog() {
        String prompt = getString(R.string.str_dialog_update_optional_prompt);
        String confirm = getString(R.string.str_dialog_update_optional_btn_update);
        String cancel = getString(R.string.str_dialog_update_optional_btn_cancel);
        final CustomDialog customDialog = new CustomDialog(this, prompt, confirm, cancel, 60);
        customDialog.setClickListener(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                customDialog.dismiss();
                //检查是否是wifi网络
                if (InvokeStaticMethod.isWifiOpen(context)) {
                    //去下载
                    showPermissionStorageRequest();
                } else {
                    //提示用户当前非wifi网络
                    showNotWifiNetworkStatusDialog();
                }
            }

            @Override
            public void doCancel() {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }

    /**
     * 弹出提示用户强制更新的对话框
     */
    private void showForceUpgradeDialog() {
        String prompt = getString(R.string.str_dialog_update_force_prompt);
        String confirm = getString(R.string.str_dialog_update_force_btn_update);
        String cancel = getString(R.string.str_dialog_update_force_btn_cancel);
        final CustomDialog customDialog = new CustomDialog(this, prompt, confirm, cancel, 60);
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                customDialog.dismiss();
                //检查是否是wifi网络
                if (InvokeStaticMethod.isWifiOpen(context)) {
                    //去下载
                    showPermissionStorageRequest();
                } else {
                    //提示用户当前非wifi网络
                    showNotWifiNetworkStatusDialog();
                }
            }

            @Override
            public void doCancel() {
                customDialog.dismiss();
                exitApp();
            }
        });
    }

    @Override
    public void doHttpResponse(Object... param) {
        super.doHttpResponse(param);
        if (this.isFinishing()) {
            return;
        }
        if (param[1] != null
                && param[0] != null
                && param[0] instanceof Bitmap
                && (Integer.parseInt(param[1].toString()) == RequestID.REQ_DOWNLOAD_PICTURE)) {
            return;
        }
        if (param[0] instanceof Bitmap
                && (Integer.parseInt(param[1].toString()) == RequestID.REQ_DOWNLOAD_PICTURE_PERSONAL_VIEW)) {
            //更新本地图片
            if (TextUtils.isEmpty(sp.getString(ExpressConstant.USER_HEAD_PORTRAIT_PIC_LOCAL_PATH_KEY))) {
                sp.setString(ExpressConstant.USER_HEAD_PORTRAIT_PIC_LOCAL_PATH_KEY, ExpressConstant.PROTRAIT_PIC_PATH);
            }
            File file = new File(sp.getString(ExpressConstant.USER_HEAD_PORTRAIT_PIC_LOCAL_PATH_KEY));
            if (file.exists()) {
                file.delete();
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BitmapUtils.saveBitmapToFile((Bitmap) param[0], file);
            return;
        }
        String result = (String) param[0];
        LogUtil.d(TAG, "response str=" + result);
        if (result == null) {
            String errMsg = getString(R.string.str_net_request_fail);
//            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
            return;
        }
        if (InvokeStaticMethod.isNotJSONstring(this, result)) {
            finish();
            return;
        }
        JSONObject object = JSON.parseObject(result);
        if (object == null) {
            ToastUtil.showMessage(this, "返回数据异常", false);
            return;
        }
        switch (Integer.parseInt(param[1].toString())) {
            case RequestID.REQ_LOGOUT:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            sp.remove("userInfo");
                            ToastUtil.showMessage(this, "退出登录成功");
                            startActivity(new Intent(this, LoginActivity.class));
                        }
                    } else {
                        sp.remove("userInfo");
                        startActivity(new Intent(this, LoginActivity.class));
                    }
                    finish();
                }
                break;
            case RequestID.REQ_UPDATE_VERSION_CHECK:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {

                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            UpgradeInfo upgradeInfo = JSON.parseObject(content, UpgradeInfo.class);
                            String url = HttpHelper.HTTP + HttpHelper.IP + File.separator + upgradeInfo.downloadPath;
                            upgradeInfo.downloadPath = url;
                            download_apk_url = url;
                            if (!upgradeInfo.version.equals(VersionUtils.getVersionName(context))) {
                                //强制升级提示框
                                if (upgradeInfo.isRequire == ExpressConstant.UPGRADE_FORCE) {//强制升级
                                    showForceUpgradeDialog();
                                } else {
                                    //非强制升级提示框
                                    showUpgradeDialog();
                                }
                                return;
                            }
                        }
                    }
                }
                break;
            case RequestID.REQ_GET_USERINFO:
            case RequestID.REQ_GET_USERINFO_CLICK:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            personalInfo = JSON.parseObject(object.getString("result"), PersonalInfo.class);
                            if (!StringUtils.isEmpty(personalInfo.userPhoto)) {
                                String url = HttpHelper.HTTP + HttpHelper.IP.substring(0, HttpHelper.IP.length() - 0) + "/images/" + personalInfo.userPhoto.replace("\\", "/");
                                personalInfo.userPhoto = url;
                            }
                            if (personalFragment != null
                                    && Integer.parseInt(param[1].toString()) == RequestID.REQ_GET_USERINFO_CLICK) {
                                FragmentManager fm = getFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                if (personalFragment.isAdded()) {
                                    ft.replace(R.id.layFrame, personalFragment);
                                } else {
                                    ft.add(R.id.layFrame, personalFragment);
                                }
                                frameLayout.setVisibility(View.GONE);
                                ft.commitAllowingStateLoss();
                            }
                        }
                    } else if (code == 0) {
                        showReloginDialog();
                    } else {
                        showErrorMsg(object);
                    }
                }
                break;
            case RequestID.REQ_GET_SETTING_INFO:
            case RequestID.REQ_GET_SETTING_INFO_CLICK:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            object = JSON.parseObject(content);
                            User user = JSON.parseObject(content, User.class);
                            String name = object.getString("name");
                            String phone = object.getString("loginName");
                            String userPhoto = object.getString("userPhoto");
                            if (personalInfo == null) {
                                personalInfo = new PersonalInfo();

                            }
                            personalInfo.name = name;
                            personalInfo.phone = phone;
                            personalInfo.userPhoto = userPhoto;
                            personalInfo.introduced = object.getBoolean("introduced");
                            if (!StringUtils.isEmpty(personalInfo.userPhoto)) {
                                String url = HttpHelper.HTTP
                                        + HttpHelper.IP.substring(0, HttpHelper.IP.length() - 0) + "/images/"
                                        + personalInfo.userPhoto.replace("\\", "/");
                                personalInfo.userPhoto = url;
                            }
                            if (settingFragment != null) {
                                settingFragment.refreshView();
                            }
//                            if (settingFragment != null
//                                    && Integer.parseInt(param[1].toString()) == RequestID.REQ_GET_SETTING_INFO_CLICK) {
//                                FragmentManager fm = getFragmentManager();
//                                FragmentTransaction ft = fm.beginTransaction();
//                                if (settingFragment.isAdded()) {
//                                    ft.replace(R.id.layFrame, settingFragment);
//                                } else {
//                                    ft.add(R.id.layFrame, settingFragment);
//                                }
//                                ft.commitAllowingStateLoss();
//                            }
                        }
                    } else if (code == 0) {
                        showReloginDialog();
                    } else {
                        showErrorMsg(object);
                    }
                }
                break;
            case RequestID.REQ_GET_ABOUT_US:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            AboutInfo aboutInfo = JSON.parseObject(content, AboutInfo.class);
                            startActivity(new Intent(context, AboutActivity.class).putExtra("about_info", aboutInfo));

                        }
                    } else if (code == 0) {
                        showReloginDialog();
                    } else {
                        showErrorMsg(object);
                    }
                }
                break;
            case RequestID.REQ_GET_ADVERTIZEMENTS:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            advs.clear();
                            advs.addAll((ArrayList<Adv>) JSON.parseArray(content, Adv.class));
                            for (int i = 0; i < advs.size(); i++) {
                                advs.get(i).image = HttpHelper.HTTP + HttpHelper.IP + "/images/" + advs.get(i).image;
//                                http://120.195.137.162:82/images/ads/ads1.jpg
                            }
                            canShowAdv = true;
//                            init();
                        }
                    } else if (code == 0) {
                        showReloginDialog();
                    } else {
                        showErrorMsg(object);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                startService(new Intent(context, UploadService.class));//开启后台上传服务
//
//            }
//        }, 100);
        if (!ExpressApplication.isForeground) {//从后台切换到前台
            ExpressApplication.isForeground = true;
//            ToastUtil.showMessage(context, "invoke check upgrade");
            checkUpgrade();
        }

    }

    /**
     * 调用接口检测是否需要升级
     * 强制升级
     * 非强制升级
     */
    private void checkUpgrade() {
        HttpHelper.sendRequest_getUpgradeInfo(context, RequestID.REQ_UPDATE_VERSION_CHECK, null);

    }


    protected void openCameraToScan() {
        InvokeStaticMethod.makeFiles();
        if (Build.VERSION.SDK_INT >= 23) { //android.os.Build.VERSION_CODES.M
            showPermissionQuestScan();
        } else {
            rentScan();
        }
    }

    protected void showPermissionQuestScan() {
        LogUtil.i(TAG, "Show contacts button pressed. Checking permissions.");

        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                ) {
            // Contacts permissions have not been granted.
            LogUtil.i(TAG, "Contact permissions has NOT been granted. Requesting permissions.");
            requestContactsPermissionsScan();
        } else {
            // Contact permissions have been granted. Show the contacts fragment.
            LogUtil.i(TAG, "Contact permissions have already been granted. Displaying contact details.");
            rentScan();
        }
    }

    protected void requestContactsPermissionsScan() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                ) {
            LogUtil.i(TAG, "Displaying contacts permission rationale to provide additional context.");
            ActivityCompat
                    .requestPermissions(this, ExpressConstant.PERMISSIONS_STORAGE_AND_CAMERA,
                            ExpressConstant.REQUEST_PERMISSION_ID_SCAN);

        } else {
            ActivityCompat.requestPermissions(this, ExpressConstant.PERMISSIONS_STORAGE_AND_CAMERA, ExpressConstant.REQUEST_PERMISSION_ID);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null && data.getBooleanExtra("camera_forbidden", false)) {
                showDialog();
                return;
            }
        }
    }

    private void showDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        String prompt = getString(R.string.str_please_grant_permission_of_camera_photo);
        String confirm = getString(R.string.str_dialog_btn_to_ok);
        final CustomDialog customDialog = new CustomDialog(this, prompt, confirm, "", 60);
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                customDialog.dismiss();
            }

            @Override
            public void doCancel() {
                customDialog.dismiss();
            }
        });
    }

    /**
     * 弹出提示用户开启相机权限
     */
    private void showOpenPermissionPromptDialogCamera() {
        String prompt = getString(R.string.str_dialog_prompt_need_grant_permission_camera);
        String confirm = getString(R.string.str_dialog_btn_to_ok);
        String cancel = "";
        final CustomDialog customDialog = new CustomDialog(this, prompt, confirm, cancel, 60);
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                customDialog.dismiss();
            }

            @Override
            public void doCancel() {
                customDialog.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ExpressConstant.REQUEST_PERMISSION_ID_SCAN) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                rentScan();
            } else {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        if (permissions[i].equals(Manifest.permission.CAMERA)) {
                            showOpenPermissionPromptDialogCamera();
                            break;
                        }
                    }
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 扫条形码
     */
    private void rentScan() {
        Intent intent = new Intent(context, CaptureActivity.class);
        startActivityForResult(intent, ExpressConstant.REQUEST_CODE_SCAN);

    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (clickFirstTime > 0) {
            if (currentTime - clickFirstTime < ExpressConstant.EXIT_APP_DURATION) {//连续按下返回键
                //退出应用
                exitApp();
            } else {
                clickFirstTime = currentTime;
                ToastUtil.showMessage(this, getString(R.string.str_prompt_back_key_exit));
            }
        } else { //第一次点击
            clickFirstTime = currentTime;
            ToastUtil.showMessage(this, getString(R.string.str_prompt_back_key_exit));
        }
    }

    /**
     * 弹出提示用户开启存储卡读写权限的对话框
     */
    private void showOpenStoragePermissionPromptDialog() {
        String prompt = getString(R.string.str_dialog_prompt_need_grant_permission_storage);
        String confirm = getString(R.string.str_dialog_btn_to_ok);
        String cancel = "";
        final CustomDialog customDialog = new CustomDialog(this, prompt, confirm, cancel, 60);
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                customDialog.dismiss();
            }

            @Override
            public void doCancel() {
                customDialog.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final MainGridViewAdapter.ViewHolder viewHolder = (MainGridViewAdapter.ViewHolder) view.getTag();
        switch (viewHolder.funcId) {
            case EXPRESS_PHOTO_TAKE:
                startActivity(new Intent(this, PhotoActivity.class));
                break;
            case EXPRESS_PHOTO_SCAN_BARCODE:
                openCameraToScan();
                break;
            case EXPRESS_PHOTO_SEARCH:
                startActivity(new Intent(this, SearchActivity.class));
                break;
//            case EXPRESS_PHOTO_RECHARGE:
//                break;
//            case EXPRESS_PHOTO_ORDER:
//                break;
//            case EXPRESS_PHOTO_GET_TAKE_ORDER:
//                break;
            case 3:
                startActivity(new Intent(this, MyExpressActivity.class));
                break;
            case 4:

                break;
            case 5:
                startActivity(new Intent(this, AreaPriceSetActivity.class));
                break;
            default:
                break;
        }

    }

    @Override
    public void FragmentViewOnClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_logout:
                doLogout();
                break;
            case R.id.setting_about_us:
                HttpHelper.sendRequest_getAboutUs(context, RequestID.REQ_GET_ABOUT_US, sp.getUserInfo().token, dialog);
                break;
            case R.id.setting_account_safe:
                startActivity(new Intent(context, ModifyPwdActivity.class));
//                ToastUtil.showMessage(context, "modify pwd click");
                break;
            case R.id.personal_main_part:
            case R.id.setting_part:
//                ToastUtil.showMessage(context, "person click");
                break;
            case R.id.setting_update_portrait:
                //上传头像
                doUploadPic();
                break;
            case R.id.setting_scan:
                openScan();
                break;
            default:
                break;
        }
    }

    private void openScan() {
        Intent intent = new Intent(context, CaptureActivity.class);
        startActivity(intent.putExtra("actionID", ExpressConstant.ACTION_ID_SETTING_SCAN));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainActivity = null;
    }
}
