package com.express56.xq.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.camera.activity.CameraActivity;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.service.UploadService;
import com.express56.xq.util.BitmapUtils;
import com.express56.xq.util.DialogUtils;
import com.express56.xq.util.DisplayUtil;
import com.express56.xq.util.FileUtil;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.SharedPreUtils;
import com.express56.xq.util.StringUtils;
import com.express56.xq.widget.ToastUtil;

import java.io.File;

public class SavePhotoActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = SavePhotoActivity.class.getSimpleName();

    private String filePath;

    private EditText editText_express_no;
    private EditText editText_sender_phone;
    private EditText editText_receiver_phone;
    private ImageView imageView;
    private String express_No;
    private TextView textView_express_no;
    private String sender;
    private String receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_photo);
        filePath = getIntent().getStringExtra("file_path");
        setResult(Activity.RESULT_OK);
        init();
    }

    @Override
    protected void initView() {
        super.initView();
        editText_express_no = (EditText) findViewById(R.id.save_photo_editText_express_no);
        textView_express_no = (TextView) findViewById(R.id.save_photo_textView_express_no);
        editText_sender_phone = (EditText) findViewById(R.id.save_photo_editText_sender_phone);
        editText_receiver_phone = (EditText) findViewById(R.id.save_photo_editText_receiver_phone);

        editText_express_no.setLongClickable(false);
        editText_express_no.setTextIsSelectable(false);

        imageView = (ImageView) findViewById(R.id.save_photo_imageView_photo);

        findViewById(R.id.save_photo_btn_cancel).setOnClickListener(this);
        findViewById(R.id.save_photo_btn_confirm).setOnClickListener(this);

    }

    @Override
    protected void initData() {
        super.initData();

        if (sp == null) sp = new SharedPreUtils(context);
        String lastSenderPhone = sp.getString("sender_phone");
        if (!StringUtils.isEmpty(lastSenderPhone)) {
            editText_sender_phone.setText(lastSenderPhone);
        }
        String lastReceiverPhone = sp.getString("receiver_phone");
        if (!StringUtils.isEmpty(lastReceiverPhone)) {
            editText_receiver_phone.setText(lastReceiverPhone);
        }

        express_No = InvokeStaticMethod.decode2(filePath);
        setExpressNo();


        Bitmap tempBitmap = BitmapUtils.getCompressBitmapFromFile(filePath);//从文件中读取图片
        Bitmap bitmap = BitmapUtils.getZoomBitmap(tempBitmap, DisplayUtil.screenWidth / (tempBitmap.getWidth() * 1.0f));
        imageView.setImageBitmap(bitmap);

    }

    private void setExpressNo() {
        editText_express_no.setVisibility(View.VISIBLE);
        if (StringUtils.isEmpty(express_No)) {//识别不了的图片 要将此标记在数据库对应的字段中 analyze_status 0: 解析成功， 1：解析失败
            //设置可编辑状态：
            editText_express_no.setFocusableInTouchMode(true);
            editText_express_no.setFocusable(true);
            editText_express_no.requestFocus();
        } else {
            editText_express_no.setText(express_No);
            editText_express_no.setFocusable(false);
            editText_express_no.setFocusableInTouchMode(false);
        }
    }

/*
    private void setExpressNo() {
        //        int analyzeStatus = ExpressConstant.ANALYZE_SUCCESS;
        if (StringUtils.isEmpty(express_No)) {//识别不了的图片 要将此标记在数据库对应的字段中 analyze_status 0: 解析成功， 1：解析失败
//            analyzeStatus = ExpressConstant.ANALYZE_FAIL;
            textView_express_no.setVisibility(View.GONE);
            editText_express_no.setVisibility(View.VISIBLE);
        } else {
            editText_express_no.setVisibility(View.GONE);
            textView_express_no.setVisibility(View.VISIBLE);
            textView_express_no.setText(express_No);
        }
    }
*/


    @Override
    public void onClick(View v) {
        if (checkFastClick()) {
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.save_photo_btn_cancel:
                finish();
                break;
            case R.id.save_photo_btn_confirm:
                LogUtil.d(TAG, "show loading " + System.currentTimeMillis());
                DialogUtils.showLoadingDialog(dialog);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        savePhotoToDB();
                        DialogUtils.closeLoadingDialog(dialog);
                        LogUtil.d(TAG, "close loading " + System.currentTimeMillis());
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    private boolean canSavePhoto() {
        if (StringUtils.isEmpty(express_No)) {//单号解析失败
            if (StringUtils.isEmpty(editText_express_no.getText().toString())) {
                ToastUtil.showMessage(context, "请填写快递单号");
                return false;
            }
            express_No = editText_express_no.getText().toString();
        }
        sender = editText_sender_phone.getText().toString().trim();
        receiver = editText_receiver_phone.getText().toString().trim();
        return true;
    }

/*    private boolean canSavePhoto() {
        if (StringUtils.isEmpty(express_No)) {//单号解析失败
            express_No = editText_express_no.getText().toString().trim();
            if (StringUtils.isEmpty(express_No)) {
                ToastUtil.showMessage(context, "请填写快递单号");
                return false;
            }
        }
        sender = editText_sender_phone.getText().toString().trim();
        receiver = editText_receiver_phone.getText().toString().trim();
        return true;
    }*/

    private void savePhotoToDB() {
        if (canSavePhoto()) {
            if (CameraActivity.cameraActivity == null) {
                ToastUtil.showMessage(MainActivity.mainActivity, "CameraActivity.cameraActivity is null !!");
                return;
            }
//            Bitmap bitmap = BitmapUtils.getCompressBitmapFromFile(filePath);//从文件中读取图片
//            LogUtil.d(TAG, "save bitmap size :" + bitmap.getByteCount());
            double size = FileUtil.getFileOrFilesSize(filePath, FileUtil.SIZETYPE_KB);
            String saveFilePath = ExpressConstant.IMAGE_FILE_LOCATION_FOLDER_PATH + File.separator + express_No + "_o.jpeg";
            InvokeStaticMethod.copyFileToSDcard(filePath, saveFilePath);
            boolean success = UploadService.photoDataHelper.insertPhoto(saveFilePath, sp.getUserInfo().phone,
                    CameraActivity.cameraActivity.expressType, express_No, sender, receiver, size + "KB", ExpressConstant.ANALYZE_SUCCESS);
            if (success) {
                ToastUtil.showMessage(context, "保存成功");
                ExpressConstant.photoNumber += 1;
            }
            String lastSenderPhone = sp.getString("sender_phone");
            if (!StringUtils.isEmpty(lastSenderPhone)) {
                sp.setString("sender_phone", editText_sender_phone.getText().toString());
            }
            String lastReceiverPhone = sp.getString("receiver_phone");
            if (!StringUtils.isEmpty(lastReceiverPhone)) {
                sp.setString("receiver_phone", editText_receiver_phone.getText().toString());
            }
            finish();
        }
    }
}
