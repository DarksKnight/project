package com.express56.xq.camera.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.express56.xq.R;
import com.express56.xq.activity.BaseActivity;
import com.express56.xq.camera.camera.CameraInterface;
import com.express56.xq.camera.camera.CameraInterface.CamOpenOverCallback;
import com.express56.xq.camera.camera.preview.CameraSurfaceView;
import com.express56.xq.camera.ui.MaskView;
import com.express56.xq.camera.util.DisplayUtil;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.widget.CustomDialog;

import java.util.List;

public class CameraActivity extends BaseActivity implements CamOpenOverCallback {
    private static final String TAG = CameraActivity.class.getSimpleName();
    CameraSurfaceView surfaceView = null;
    ImageButton shutterBtn;
    MaskView maskView = null;
    float previewRate = -1f;
    public static int DST_CENTER_RECT_WIDTH = 400; //单位是dip
    public static int DST_CENTER_RECT_HEIGHT = 240;//单位是dip
    //	public static final int DST_CENTER_RECT_WIDTH = (int) (com.test.qzy.util.DisplayUtil.screenHeight * ExpressConstant.PHOTO_PREVIEW_RATIO); //单位是dip
//	public static final int DST_CENTER_RECT_HEIGHT = (int) (com.test.qzy.util.DisplayUtil.screenWidth * ExpressConstant.PHOTO_PREVIEW_RATIO);//单位是dip
    int picWidth = 0;
    int picHeight = 0;
    Point rectPictureSize = null;
    public static CameraActivity cameraActivity;
    private LinearLayout flash_light_control;
    private ImageView flashLightBtn;
    private Camera camera;
    private TextView lightTextBtn;
    /**
     * 拍照类型
     */
    public int expressType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		DST_CENTER_RECT_WIDTH = (int) (com.test.qzy.util.DisplayUtil.screenHeight * ExpressConstant.PHOTO_PREVIEW_RATIO); //单位是dip
//		DST_CENTER_RECT_HEIGHT = (int) (com.test.qzy.util.DisplayUtil.screenWidth * ExpressConstant.PHOTO_PREVIEW_RATIO);//单位是dip
        expressType = getIntent().getIntExtra("expressType", 0);
        this.cameraActivity = this;
        openCamera();

    }

    private void openCamera() {
        Thread openThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                CameraInterface.getInstance().doOpenCamera(CameraActivity.this);
            }
        };
        openThread.start();
        setContentView(R.layout.activity_camera);
        initUI();
        initViewParams();
    }

    private int[] getPreviewFrameSize() {
        Camera.Parameters parameters = CameraInterface.getInstance().getmCamera().getParameters();
        // 选择合适的预览尺寸
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        Camera.Size largerSize = null;
        if (sizeList.size() > 0) {
            Camera.Size cameraSize = sizeList.get(0);
            for (Camera.Size size : sizeList) {
                if (size.width > cameraSize.width) {
                    cameraSize = size;
                }
            }
            Log.i(TAG, "preview cameraSize.width = " + cameraSize.width
                    + "preview cameraSize.height = " + cameraSize.height);
            Log.i(TAG, "preview cameraSize.width = " + cameraSize.width * ExpressConstant.PHOTO_PREVIEW_RATIO
                    + "preview cameraSize.height = " + cameraSize.height * ExpressConstant.PHOTO_PREVIEW_RATIO_HEIGHT);
            return new int[]{(int) (cameraSize.width * ExpressConstant.PHOTO_PREVIEW_RATIO), (int) (cameraSize.height * ExpressConstant.PHOTO_PREVIEW_RATIO_HEIGHT)};
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initUI() {
        surfaceView = (CameraSurfaceView) findViewById(R.id.camera_surfaceview);
        shutterBtn = (ImageButton) findViewById(R.id.btn_shutter);
        flash_light_control = (LinearLayout) findViewById(R.id.flash_light_control);
        flashLightBtn = (ImageView) findViewById(R.id.btn_control_flash_light);
        lightTextBtn = (TextView) findViewById(R.id.btn_light_text);
        maskView = (MaskView) findViewById(R.id.view_mask);
    }

    private void initViewParams() {
        LayoutParams params = surfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        Log.i(TAG, "screen: w = " + p.x + " y = " + p.y);
        previewRate = DisplayUtil.getScreenRate(this); //默认全屏的比例预览
        surfaceView.setLayoutParams(params);

        //手动设置拍照ImageButton的大小为120dip×120dip,原图片大小是64×64
        RelativeLayout.LayoutParams p2 = (RelativeLayout.LayoutParams) shutterBtn.getLayoutParams();
        p2.width = DisplayUtil.dip2px(this, 56);
        p2.height = DisplayUtil.dip2px(this, 56);

        int x1 = (int) ((DisplayUtil.getScreenMetrics(this).x * (1 - ExpressConstant.PHOTO_PREVIEW_RATIO)) / 2);
        x1 = (x1 - p2.width) / 2;
        p2.setMargins(0, 0, x1 / 2, 0);
        shutterBtn.setLayoutParams(p2);
        shutterBtn.setOnClickListener(new BtnListeners());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) flash_light_control.getLayoutParams();
        lp.setMargins(0, lp.topMargin, x1 / 2, 0);
        flash_light_control.setLayoutParams(lp);
        flashLightBtn.setOnClickListener(new BtnListeners());

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void cameraHasOpened() {
        if (surfaceView == null) {
            showDialog();
            return;
        }
        SurfaceHolder holder = surfaceView.getSurfaceHolder();
        CameraInterface.getInstance().doStartPreview(holder, previewRate);
        if (maskView != null) {
            int[] frameSize = getPreviewFrameSize();
            DST_CENTER_RECT_WIDTH = frameSize[0];
            DST_CENTER_RECT_HEIGHT = frameSize[1];
            Rect screenCenterRect = createCenterScreenRect(DST_CENTER_RECT_WIDTH, DST_CENTER_RECT_HEIGHT);
            maskView.setCenterRect(screenCenterRect);
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
                finish();
            }

            @Override
            public void doCancel() {
                customDialog.dismiss();
            }
        });
    }

    @Override
    protected boolean checkFastClick() {
        long current_time = System.currentTimeMillis();
        if (current_time - first_click_time < ExpressConstant.DOUBLE_CLICK_TIME_INTERVAL_2) {
            return true;
        }
        first_click_time = current_time;
        return false;
    }

    private class BtnListeners implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (checkFastClick()) {
                return;
            }
            switch (v.getId()) {
                case R.id.btn_shutter:
                    //设置图片大小
                    Camera.Parameters parameters = CameraInterface.getInstance().getmCamera().getParameters();
                    List<Camera.Size> sizeList = parameters.getSupportedPictureSizes();
                    if (sizeList.size() > 0) {
                        Camera.Size cameraSize = sizeList.get(0);
                        for (Camera.Size size : sizeList) {
                            if (size.width > cameraSize.width) {
                                cameraSize = size;
                            }
                        }
                        CameraInterface.getInstance().doTakePicture(cameraSize.width, cameraSize.height);
                    }
                    break;
                case R.id.btn_control_flash_light:
                    camera = CameraInterface.getInstance().getmCamera();
                    //闪光灯开关操作
                    if (checkFlashlight() && camera != null) {
                        String flashMode = camera.getParameters().getFlashMode();
                        if (flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                            lightTextBtn.setText(getString(R.string.str_open_flashing_light));
                            flashLightBtn.setImageResource(R.drawable.btn_scan_open_light);
                            closeFlashlight(camera);
                        } else {
                            lightTextBtn.setText(getString(R.string.str_close_flashing_light));
                            flashLightBtn.setImageResource(R.drawable.btn_scan_close_light);
                            openFlashlight(camera);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 打开闪光灯
     * @param camera
     */
    void openFlashlight(Camera camera) {

        try {
            if (camera == null) return;
            int textureId = 0;

            Camera.Parameters mParameters = this.camera.getParameters();

            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            this.camera.setParameters(mParameters);

        } catch (Exception e) {
        }
    }

    /**
     * 关闭闪光灯
     * @param camera
     */
    void closeFlashlight(Camera camera) {
        if (camera != null) {
            Camera.Parameters mParameters = this.camera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            this.camera.setParameters(mParameters);
        }
    }


    /**
     * 检测当前设备是否配置闪光灯
     *
     * @return
     */
    boolean checkFlashlight() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "当前设备没有闪光灯", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * 生成拍照后图片的中间矩形的宽度和高度
     *
     * @param w 屏幕上的矩形宽度，单位px
     * @param h 屏幕上的矩形高度，单位px
     * @return
     */
    private Point createCenterPictureRect(int w, int h) {

        int wScreen = DisplayUtil.getScreenMetrics(this).x;
        int hScreen = DisplayUtil.getScreenMetrics(this).y;
        int wSavePicture = CameraInterface.getInstance().doGetPrictureSize().x; //因为图片旋转了，所以此处宽高换位
        int hSavePicture = CameraInterface.getInstance().doGetPrictureSize().y; //因为图片旋转了，所以此处宽高换位
        float wRate = (float) (wSavePicture) / (float) (wScreen);
        float hRate = (float) (hSavePicture) / (float) (hScreen);
        float rate = (wRate <= hRate) ? wRate : hRate;//也可以按照最小比率计算

        int wRectPicture = (int) (w * wRate);
        int hRectPicture = (int) (h * hRate);
        return new Point(wRectPicture, hRectPicture);

    }

    /**
     * 生成屏幕中间的矩形
     *
     * @param w 目标矩形的宽度,单位px
     * @param h 目标矩形的高度,单位px
     * @return
     */
    private Rect createCenterScreenRect(int w, int h) {
        int x1 = (DisplayUtil.getScreenMetrics(this).x - w) / 2;
        int y1 = (DisplayUtil.getScreenMetrics(this).y - h) / 2;
        int x2 = x1 + w;
        int y2 = y1 + h;
        return new Rect(x1, y1, x2, y2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraInterface.getInstance().doStopCamera();
        cameraActivity = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ExpressConstant.REQUEST_CODE_START_PREVIEW) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        openCamera();
//                    }
//                }, 3000);
//                CameraInterface.getInstance().startPreview();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putString("filePath", filePath);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        if (TextUtils.isEmpty(filePath)) {
//            filePath = savedInstanceState.getString("filePath");
//        }
        Log.d(TAG, "onRestoreInstanceState");
    }

}
