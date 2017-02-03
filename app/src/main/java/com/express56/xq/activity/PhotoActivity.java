package com.express56.xq.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.express56.xq.R;
import com.express56.xq.adapter.ExpressNotUploadListViewAdapter;
import com.express56.xq.camera.activity.CameraActivity;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.db.PhotoDataHelper;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.IHttpProgressResponse;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.ExpressPhoto;
import com.express56.xq.scan.CaptureActivity;
import com.express56.xq.service.UploadService;
import com.express56.xq.util.DisplayUtil;
import com.express56.xq.util.FileUtil;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.PermissionUtil;
import com.express56.xq.util.SharedPreUtils;
import com.express56.xq.util.StringUtils;
import com.express56.xq.widget.CustomDialog;
import com.express56.xq.widget.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

public class PhotoActivity extends BaseActivity implements IHttpProgressResponse, View.OnClickListener {

    private static final String TAG = PhotoActivity.class.getSimpleName();
    private int expressType;
    private Uri imageUri;

    private ArrayList<ExpressPhoto> expressPhotos = new ArrayList<ExpressPhoto>();

    private ExpressNotUploadListViewAdapter expressAnalyzeFailureListViewAdapter;

    private ListView listView;

    /**
     * 查询结果索引
     */
    private int pageIndex;

    /**
     * 被操作的item对应的记录
     */
    private ExpressPhoto expressPhoto;

    public SharedPreUtils getSharedPreUtils() {
        return sharedPreUtils;
    }

    private SharedPreUtils sharedPreUtils = null;

    public static PhotoActivity photoActivity = null;

    private int lastItem;

    /**
     * 本次查询结果已经全部显示，不在自动请求更多
     */
    private boolean noMoreData;
    private TextView textView_footer;
    private View footView;
    private ToggleButton btn_upload_setting;
    private ExpressPhoto selected_expressPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        photoActivity = this;
        init();//初始化界面
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.photo_back_btn).setOnClickListener(this);
        findViewById(R.id.btn_photo_send).setOnClickListener(this);
        findViewById(R.id.btn_photo_receive).setOnClickListener(this);
        findViewById(R.id.btn_change_to_scan).setOnClickListener(this);


        btn_upload_setting = (ToggleButton) findViewById(R.id.btn_upload_setting);
        btn_upload_setting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sp.setBoolean("is_upload_only_wifi", true);
                } else {
                    sp.setBoolean("is_upload_only_wifi", false);
                }
                ExpressConstant.isOnlyUploadWifi = sp.getBoolean("is_upload_only_wifi", true);
                HttpHelper.sendRequest_updateWifiUploadFlag(context,
                        RequestID.REQ_UPDATE_ISWIFIUPLOAD_FLAG, sp.getUserInfo().token, ExpressConstant.isOnlyUploadWifi, dialog);
            }
        });

        listView = (ListView) findViewById(R.id.listView_express_need_discriminate);
        LayoutInflater inflater = LayoutInflater.from(this);
        footView = inflater.inflate(R.layout.express_listview_footer, null);
        textView_footer = (TextView) footView.findViewById(R.id.listView_footer);
        //listview的addFooterView()添加view到listview底部一定要加在listview.setAdapter(adapter);这代码前面
        listView.addFooterView(footView);

        //禁止头部出现分割线
//        listView.addHeaderView(mHeader, null, true);
//        listView.setHeaderDividersEnabled(false);
    }

    @Override
    protected void initData() {
        super.initData();
        sharedPreUtils = sp;
        if (sp.getBoolean("is_upload_only_wifi", true)) {// only wifi
            btn_upload_setting.setChecked(true);
        } else { //all
            btn_upload_setting.setChecked(false);
        }

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        loadMoreData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount - 1;
            }
        });
//        showNotUploadList();
    }

    private void loadMoreData() {
        if (!noMoreData) {
//            ArrayList<ExpressPhoto> tempPhotos = UploadService.photoDataHelper.getAnlayzeFailExpressPhoto(pageIndex);
            ArrayList<ExpressPhoto> tempPhotos = UploadService.photoDataHelper.getAllExpressPhoto(pageIndex, sp.getUserInfo().phone);
            dealWithData(tempPhotos);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNotUploadList();
    }

    /**
     * 显示未识别列表
     */
    public void showNotUploadList() {
        pageIndex = 1;
        noMoreData = false;
        if (expressPhotos != null) {
            expressPhotos.clear();
        }

        //查询数据库
        if (UploadService.photoDataHelper == null) {
            UploadService.photoDataHelper = new PhotoDataHelper(this);
            UploadService.photoDataHelper.open();
        }
//        ArrayList<ExpressPhoto> tempPhotos = UploadService.photoDataHelper.getAnlayzeFailExpressPhoto(pageIndex);
        ArrayList<ExpressPhoto> tempPhotos = UploadService.photoDataHelper.getAllExpressPhoto(pageIndex, sp.getUserInfo().phone);
        dealWithData(tempPhotos);
        if (expressAnalyzeFailureListViewAdapter != null) {
            expressAnalyzeFailureListViewAdapter.notifyDataSetChanged();
        }

    }

    private void dealWithData(ArrayList<ExpressPhoto> tempPhotos) {

        if (pageIndex == 1) {//第一次请求
            if (expressPhotos != null) {
                expressPhotos.clear();
            }
            if (tempPhotos.size() == 0) {
                textView_footer.setText("没有未上传的照片");
            } else {
                textView_footer.setText("加载更多");
                expressPhotos.addAll(tempPhotos);
            }
        } else {//加载更多
            if (expressPhotos != null && tempPhotos != null && tempPhotos.size() > 0) {
                expressPhotos.addAll(tempPhotos);
            }
        }
        if (tempPhotos != null && tempPhotos.size() > 0) {
            pageIndex += 1;
            showResultList();
        } else {
            if (pageIndex > 1) {
                textView_footer.setText("已全部加载");
                ToastUtil.showMessageLong(this, "没有更多数据");
            } else {
//                ToastUtil.showMessageLong(this, "没有相关记录");
            }
            noMoreData = true;
        }
    }

    /**
     * 显示列表
     */
    private void showResultList() {
        if (expressAnalyzeFailureListViewAdapter == null || expressPhotos.size() == 0) {
            float imgShowWidth = DisplayUtil.screenWidth - (listView.getPaddingLeft() + listView.getPaddingRight());
            expressAnalyzeFailureListViewAdapter = new ExpressNotUploadListViewAdapter(this, expressPhotos, imgShowWidth);
            listView.setAdapter(expressAnalyzeFailureListViewAdapter);

//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//                                        long id) {
//                    ExpressInfo expressInfo = expressPhotos.get(position - 1);
//                    if (expressInfo != null) {
//                        Object object = arg1.getTag(R.id.tag_imageview);
//                        if (object instanceof ImageView) {
//                            Bitmap bitmap = BitmapUtils.getBitmapFromDrawable(((ImageView) object).getDrawable());
//                            startActivity(new Intent(context, ZoomImageActivity.class).putExtra("bitmap", bitmap));
//                        }
//                    }
//                }
//
//            });
        } else {
            expressAnalyzeFailureListViewAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 删除 上传 成功后调用
     * @param photo_id
     */
    public void refreshAfterDelete(String photo_id) {
        for (ExpressPhoto expressPhoto : expressPhotos) {
            if (expressPhoto.photo_id != null && expressPhoto.photo_id.equals(photo_id)) {
                expressPhotos.remove(expressPhoto);
                expressAnalyzeFailureListViewAdapter.notifyDataSetChanged();
                showNotUploadList();
                if (expressPhotos.size() == 0) {
                    textView_footer.setText("没有未上传的照片");
                }
                break;
            }
        }
    }

    /**
     * wifi 网络环境下 上传图片
     *
     * @throws InterruptedException
     */
    public void uploadPhoto(ExpressPhoto expressPhoto) {
        this.expressPhoto = expressPhoto;
//        LogUtil.d(TAG, "---------uploadPhotos -----------");
        if (sp.getUserInfo() != null && !StringUtils.isEmpty(sp.getUserInfo().token)) {
            UploadService.photoDataHelper.updatePhotoUploadStatus(expressPhoto.photo_id, sp.getUserInfo().phone, ExpressConstant.UPLOAD_STATUS_UPLOADING);//更新数据库记录上传状态
            if (expressPhoto.photoFilePath == null
                    || expressPhoto.photoFilePath.length() == 0
                    || expressPhoto.upload_status == ExpressConstant.UPLOAD_STATUS_UPLOADING
                    || StringUtils.isEmpty(expressPhoto.photo_express_no)
                    || (expressPhoto.photo_type != 1 && expressPhoto.photo_type != 2)) {
                return;
            }
            File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "photo_1.jpeg");
            if (file.exists()) {
                file.delete();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            file = FileUtil.createFileWithByte(expressPhoto.photoFilePath, "photo_1.jpeg");
            file = new File(expressPhoto.photoFilePath);

            if (FileUtil.getFileSize(file) > 0) {
                selected_expressPhoto = expressPhoto;
                HttpHelper.sendRequest_uploadPic(this, file, sp.getUserInfo().token,
                        RequestID.REQ_UPLOAD_PICTURE, expressPhoto.photo_type, expressPhoto.photo_express_no, expressPhoto.sender, expressPhoto.receiver, dialog);
            }

        }
    }

    @Override
    public void onClick(View v) {
        if (checkFastClick()) {
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.btn_photo_receive:
                expressType = ExpressConstant.EXPRESS_TYPE_RECEIVE;
                doUploadExpressPic();
                break;
            case R.id.btn_photo_send:
                expressType = ExpressConstant.EXPRESS_TYPE_SEND;
                doUploadExpressPic();
                break;
            case R.id.btn_change_to_scan:
                openCameraToScan();
                break;
            case R.id.photo_back_btn:
                finish();
                break;
            case R.id.list_refresh_btn:
                showNotUploadList();
                break;
//            case R.id.btn_upload_setting:
//                if (sp.getBoolean("is_upload_only_wifi", true)) {
//                    sp.setBoolean("is_upload_only_wifi", false);
//                    btn_upload_setting.setText("打开");
//                } else {
//                    sp.setBoolean("is_upload_only_wifi", true);
//                    btn_upload_setting.setText("关闭");
//                }
//                ExpressConstant.IS_UPLOAD_ONLY_WIFI = sp.getBoolean("is_upload_only_wifi", true);
//                break;
            default:
                break;
        }
    }

    protected void doUploadExpressPic() {
        InvokeStaticMethod.makeFiles();
        if (Build.VERSION.SDK_INT >= 23) { //android.os.Build.VERSION_CODES.M
            showPermissionQuest();
        } else {
            showSelectPictureDialogByType();
        }
    }

    protected void showPermissionQuest() {
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
            requestContactsPermissions();
        } else {
            // Contact permissions have been granted. Show the contacts fragment.
            LogUtil.i(TAG, "Contact permissions have already been granted. Displaying contact details.");
            showSelectPictureDialogByType();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    public void showInputDialog(String uid, final int position) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        final CustomDialog customDialog = new CustomDialog(this, uid, CustomDialog.DIALOG_INPUT_EXPRESS_NO);
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickInputListenerInterface() {
            @Override
            public void doConfirm(String uid, String express_no) {
                customDialog.dismiss();
                boolean success = UploadService.photoDataHelper.updateExpressPhotoExpressNoByUID(uid, express_no);
                if (success) {
                    ExpressConstant.photoNumber += 1;
                }
//                showNotUploadList();
                expressPhotos.remove(position);
                expressAnalyzeFailureListViewAdapter.notifyDataSetChanged();
                if (expressPhotos.size() == 0) {
                    textView_footer.setText("没有未上传的照片");
                }
            }

            @Override
            public void doCancel() {
                customDialog.dismiss();
            }
        });
    }


    protected void requestContactsPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                ) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            LogUtil.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            ActivityCompat
                    .requestPermissions(this, ExpressConstant.PERMISSIONS_STORAGE_AND_CAMERA,
                            ExpressConstant.REQUEST_PERMISSION_ID);
        } else {
            ActivityCompat.requestPermissions(this, ExpressConstant.PERMISSIONS_STORAGE_AND_CAMERA, ExpressConstant.REQUEST_PERMISSION_ID);
        }
    }

    private void showSelectPictureDialogByType() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, getString(R.string.str_get_pic_add_sdcard), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            File file = new File(ExpressConstant.IMAGE_FILE_LOCATION_PATH);
            if (file != null && file.exists()) {
                file.delete();
            }
            file.createNewFile();
            imageUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent = new Intent(this, CameraActivity.class).putExtra("expressType", expressType);
        startActivity(intent);
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
        startActivity(intent);
//        finish();
    }

    @Override
    public void doHttpResponse(Object... param) {
        if (this.isFinishing()) {
            return;
        }
        if (param[1] != null
                && param[0] != null
                && param[0] instanceof Bitmap
                && (Integer.parseInt(param[1].toString()) == RequestID.REQ_DOWNLOAD_PICTURE)) {
            return;
        }
        int requestID = Integer.parseInt(param[1].toString());
        String result = (String) param[0];
        LogUtil.d(TAG, "response str=" + result);
        if (StringUtils.isEmpty(result)) {
            if (requestID == RequestID.REQ_UPLOAD_PICTURE) {
                UploadService.photoDataHelper.updatePhotoUploadStatus(expressPhoto.photo_id, sp.getUserInfo().phone, ExpressConstant.UPLOAD_STATUS_NORMAL);//更新数据库记录上传状态
            }
            String errMsg = getString(R.string.str_net_request_fail);
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
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
        switch (requestID) {
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
            case RequestID.REQ_UPLOAD_PICTURE:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            UploadService.photoDataHelper.deletePhotoByID(selected_expressPhoto.photo_id, sp.getUserInfo().phone);
                            refreshAfterDelete(selected_expressPhoto.photo_id);
                            ToastUtil.showMessage(this, "上传成功");
                            if (ExpressConstant.photoNumber > 0) {
                                ExpressConstant.photoNumber -= 1;
                            }
                        }
                    } else if (code == 0) {
                        UploadService.photoDataHelper.updatePhotoUploadStatus(expressPhoto.photo_id, sp.getUserInfo().phone, ExpressConstant.UPLOAD_STATUS_NORMAL);//更新数据库记录上传状态
                        //token 过期
                        showReloginDialog();
                    } else {
                        UploadService.photoDataHelper.updatePhotoUploadStatus(expressPhoto.photo_id, sp.getUserInfo().phone, ExpressConstant.UPLOAD_STATUS_NORMAL);//更新数据库记录上传状态
                        showErrorMsg(object);
                    }
                }
                break;
            case RequestID.REQ_UPDATE_ISWIFIUPLOAD_FLAG:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            ToastUtil.showMessage(context, object.getString("result"));
                        }
                    } else if (code == 0) {
                        //token 过期
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
    protected void onDestroy() {
        super.onDestroy();
        photoActivity = null;
    }

    @Override
    public void doHttpProgressResponse(float progress) {

    }
}
