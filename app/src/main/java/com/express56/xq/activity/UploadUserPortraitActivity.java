package com.express56.xq.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;
import android.widget.Toast;

import com.express56.xq.R;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.IHttpProgressResponse;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.PersonalInfo;
import com.express56.xq.model.User;
import com.express56.xq.util.BitmapUtils;
import com.express56.xq.util.FileImport;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.PermissionUtil;
import com.express56.xq.util.PicUtil;
import com.express56.xq.util.StringUtils;
import com.express56.xq.widget.CustomDialog;
import com.express56.xq.widget.CustomImageView;
import com.express56.xq.widget.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2016/5/22.
 */
public class UploadUserPortraitActivity extends BaseActivity implements IHttpProgressResponse {

    protected static final String TAG = UploadUserPortraitActivity.class.getSimpleName();

    protected String uploadIsCrop = "1";// 1表示要裁剪
    protected String picturePath;
    protected final int SELECT_CAMERA = 0;
    protected final int SELECT_PICTURE = 1;
    protected final int CROP_CAMERA = 2;
    protected final int CROP_PICTURE = 3;
    protected String finalPicturePath;
    protected Uri imageUri;

    protected String bikeID = "";
    protected ImageView imageView;
    protected String uploadImageUrl = "";

//    /**
//     * 操作类型 ： 1 - 上传车辆图片
//     * 2 - 上传学生证
//     */
//    protected int actionType = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void doUploadPic() {
        InvokeStaticMethod.makeFiles();
        if (Build.VERSION.SDK_INT >= 23) { //android.os.Build.VERSION_CODES.M
            showPermissionQuest();
        } else {
            showSelectPictureDialog();
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
            showSelectPictureDialog();
        }
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

            // Display a SnackBar with an explanation and a button to trigger the request.
//            Snackbar.make(v, "permission_contacts_rationale",
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction("ok", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            ActivityCompat
//                                    .requestPermissions(MapViewActivity.this, BikeConstant.PERMISSIONS_LOCATION,
//                                            BikeConstant.REQUEST_PERMISSION_ID);
//                        }
//                    })
//                    .show();
            ActivityCompat
                    .requestPermissions(this, ExpressConstant.PERMISSIONS_STORAGE_AND_CAMERA,
                            ExpressConstant.REQUEST_PERMISSION_ID);
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, ExpressConstant.PERMISSIONS_STORAGE_AND_CAMERA, ExpressConstant.REQUEST_PERMISSION_ID);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ExpressConstant.REQUEST_PERMISSION_ID) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                showSelectPictureDialog();
            } else {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        if (permissions[i].equals(Manifest.permission.CAMERA)) {
                            showOpenPermissionPromptDialogCamera();
                            break;
                        }
                    }
                }
//                showOpenPermissionPromptDialogStorage();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 弹出提示用户开启存储卡读写权限的对话框
     */
    private void showOpenPermissionPromptDialogStorage() {
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

    private void showSelectPictureDialog() {
        CharSequence[] items = {getString(R.string.str_get_pic_from_camera), getString(R.string.str_get_pic_from_ablum)};
        final AlertDialog pictureDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.str_get_pic_choose_from_where)).setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkFastClick()) {
                            return;
                        }
                        if (which == SELECT_CAMERA) {
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
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, SELECT_CAMERA);
                        } else if (which == SELECT_PICTURE) {
                            Intent intent = new Intent(Intent.ACTION_PICK, null);
                            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            startActivityForResult(Intent.createChooser(intent, getString(R.string.str_get_pic_select_photo)), SELECT_PICTURE);
                        }
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_CAMERA:
                    if ("0".equals(this.uploadIsCrop)) {
                        selectCamera();
                    } else {
                        cropImageUri(imageUri);
                    }
                    break;
                case SELECT_PICTURE:
                    if ("0".equals(this.uploadIsCrop)) {
                        Uri selectedImage = data.getData();
                        selectPicture(selectedImage);
                    } else {
                        // 从相册中直接获取文件的真是路径，然后上传
//                        final String picPath = PicUtil.getPicturePath(data, this);
//                        LogUtil.d("picPath", "===" + picPath);
//                        if (picPath == null) {
//                            Toast.makeText(context, "图片未找到", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        File picFile = new File(BikeConstant.IMAGE_FILE_LOCATION_PATH);
//                        if (picFile.exists()) {
//                            picFile.delete();
//                        }
//                        try {
//                            picFile.createNewFile();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        InvokeStaticMethod.copyFileToSDcard(picPath, BikeConstant.IMAGE_FILE_LOCATION_PATH);
//                        finalPicturePath = PicUtil.getHeadPortraitFromFileAndRotaing(BikeConstant.IMAGE_FILE_LOCATION_PATH);
//                        Uri selectedImageUri = Uri.parse(finalPicturePath);
//                        selectPicture(selectedImageUri);
                        File picFile = new File(ExpressConstant.IMAGE_FILE_LOCATION_PATH);
                        if (picFile.exists()) {
                            picFile.delete();
                        }
                        try {
                            picFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String picPath = PicUtil.getPicturePath(data, this);
                        InvokeStaticMethod.copyFileToSDcard(picPath, ExpressConstant.IMAGE_FILE_LOCATION_PATH);
                        imageUri = Uri.fromFile(new File(ExpressConstant.IMAGE_FILE_LOCATION_PATH));
                        cropImageUri(imageUri);
                    }
                    break;
                case CROP_CAMERA:
                    selectCamera();
                    break;
                case CROP_PICTURE:
                    finalPicturePath = PicUtil.getHeadPortraitFromFileAndRotaing(ExpressConstant.IMAGE_FILE_LOCATION_PATH);
                    Uri selectedImageUri = Uri.parse(finalPicturePath);
                    selectPicture(selectedImageUri);

                    break;
                default:
                    break;
            }
        }
    }

    private void selectPicture(Uri selectedImg) {
        final int lastPointIndex = selectedImg.getPath().lastIndexOf(".");
        if (lastPointIndex != -1) {
            picturePath = selectedImg.getPath();
            if (picturePath.equals("")) {
                importImgFromGallery(selectedImg);
            } else {
                // final String fileType = FileType.getFileType(picturePath);
                final String fileType = picturePath.substring(picturePath
                        .lastIndexOf(".") + 1);
                if (!"jpg".equalsIgnoreCase(fileType)
                        && !"jpeg".equalsIgnoreCase(fileType)
                        && !"png".equalsIgnoreCase(fileType)
                        && !"gif".equalsIgnoreCase(fileType)
                        && !"tif".equalsIgnoreCase(fileType)
                        && !"bmp".equalsIgnoreCase(fileType)) {
                    importImgFromGallery(selectedImg);
                }
            }
        } else {
            importImgFromGallery(selectedImg);
        }
        if (picturePath == null) {
            picturePath = "";
        }
        if (!picturePath.equals("")) {
            final String fileType = picturePath.substring(picturePath
                    .lastIndexOf(".") + 1);
            if ("jpg".equalsIgnoreCase(fileType)
                    || "jpeg".equalsIgnoreCase(fileType)
                    || "png".equalsIgnoreCase(fileType)
                    || "gif".equalsIgnoreCase(fileType)
                    || "tif".equalsIgnoreCase(fileType)
                    || "bmp".equalsIgnoreCase(fileType)) {
                startUpload();
            } else {
                Toast.makeText(getApplicationContext(), "请选择有效图片文件",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "请选择有效图片文件",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void importImgFromGallery(Uri selectedImg) {
        picturePath = "";
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImg, filePathColumn,
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();// 必须关闭游标
        } else {
            picturePath = "";
        }
        if (picturePath == null) {
            picturePath = FileImport.getPath(this, selectedImg);
        }
    }

    public void selectCamera() {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        String fileName = ExpressConstant.IMAGE_FILE_LOCATION_PATH;
        try {
            LogUtil.e(TAG, "file is: " + new FileInputStream(new File(fileName)).available());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        PicUtil.getSmallImageFromFileAndRotaing(fileName);
        finalPicturePath = PicUtil.getHeadPortraitFromFileAndRotaing(fileName);
        startUpload();
    }

    protected void startUpload() {
        HttpHelper.sendRequest_uploadPortraitPic(context,
                new File(finalPicturePath), sp.getUserInfo().token, RequestID.REQ_UPLOAD_PICTURE, dialog);
    }

    private void cropImageUri(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽
        intent.putExtra("outputX", ExpressConstant.USER_HEAD_PORTRAIT_PIC_SIZE_WIDTH);
        intent.putExtra("outputY", ExpressConstant.USER_HEAD_PORTRAIT_PIC_SIZE_HEIGHT);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, CROP_CAMERA);
    }

    private void cropImageFromAlbum(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("outputX", ExpressConstant.USER_HEAD_PORTRAIT_PIC_SIZE_WIDTH);
        intent.putExtra("outputY", ExpressConstant.USER_HEAD_PORTRAIT_PIC_SIZE_HEIGHT);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, CROP_CAMERA);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        //可以选择图片类型，如果是*表明所有类型的图片
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        // aspectX aspectY 是宽高的比例，这里设置的是正方形（长宽比为1:1）
//        intent.putExtra("aspectX", BikeConstant.UPLOAD_PIC_SIZE_WIDTH / 2);
//        intent.putExtra("aspectY", BikeConstant.UPLOAD_PIC_SIZE_HEIGHT / 2);
//        // outputX outputY 是裁剪图片宽高
//        intent.putExtra("outputX", BikeConstant.UPLOAD_PIC_SIZE_WIDTH);
//        intent.putExtra("outputY", BikeConstant.UPLOAD_PIC_SIZE_HEIGHT);
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 300);

        //将剪切的图片保存到目标Uri中
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //是否是圆形裁剪区域，设置了也不一定有效
        //intent.putExtra("circleCrop", true);
        //设置输出的格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //是否将数据保留在Bitmap中返回
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);

        startActivityForResult(intent, CROP_PICTURE);
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = 0;
        if (height > width) {
            scaleWidth = ((float) newHeight) / height;
        } else {
            scaleWidth = ((float) newWidth) / width;
        }
        matrix.postScale(scaleWidth, scaleWidth);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    protected void updateLocalImageFile() {
        String filePath = ExpressConstant.PROTRAIT_PIC_PATH;
        sp.setString(ExpressConstant.USER_HEAD_PORTRAIT_PIC_URL_KEY, uploadImageUrl);//保存url

        Boolean ok = InvokeStaticMethod.copySDcardFile(finalPicturePath, filePath);//上传成功后替换本地图片
        if (ok) {
            sp.setString(ExpressConstant.USER_HEAD_PORTRAIT_PIC_LOCAL_PATH_KEY, ExpressConstant.PROTRAIT_PIC_PATH);//对应的本地图片

        }
    }

    @Override
    public void doHttpResponse(Object... param) {
        if (this.isFinishing()) {
            return;
        }
        if (param[0] instanceof String) {
            String result = (String) param[0];
            LogUtil.d(TAG, "result=" + result);
            if (result == null) {
                String errMsg = (String) param[2];
                if (StringUtils.isEmpty(errMsg)) {//没有错误信息  弹出默认错误提示
                    errMsg = getString(R.string.str_net_request_fail);
                }
                Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
                return;
            }
            if (InvokeStaticMethod.isNotJSONstring(this, result)) {
                finish();
                return;
            }
            JSONObject object = JSON.parseObject(result);
            switch (Integer.parseInt(param[1].toString())) {
                case RequestID.REQ_UPLOAD_PICTURE:
                    if (object.containsKey("code")) {
                        int code = object.getIntValue("code");
                        if (code == 9) {
                            if (object != null && object.containsKey("result")) {
                                uploadImageUrl = object.getString("result");
                                LogUtil.d(TAG, "upload uploadImageUrl = " + uploadImageUrl);
                                String url = HttpHelper.HTTP + HttpHelper.IP.substring(0, HttpHelper.IP.length() - 0)
                                        + "/images/" + uploadImageUrl.replace("\\", "/");
                                personalInfo.userPhoto = url;
                            }
                            ToastUtil.showMessageLong(context, getString(R.string.str_upload_head_portrait_pic_success));
                            updateLocalImageFile();
                            updateBikeImage();
                            MainActivity.mainActivity.hadRefreshHeadPortrait = true; //refresh menu view head protrait
                        } else if (code == 0) {
                            showReloginDialog();
                        } else {
                            showErrorMsg(object, getString(R.string.str_upload_pic_fail));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void updateBikeImage() {
        imageView.setImageBitmap(BitmapUtils.getBitmapFromFile(new File(finalPicturePath)));
//        imageView.setSrcDrawable(BitmapUtils.getBitmapFromFile(new File(finalPicturePath)));
        imageView.postInvalidate();
    }


    @Override
    public void doHttpProgressResponse(float progress) {

    }
}
