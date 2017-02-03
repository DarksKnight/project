package com.express56.xq.scan;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.express56.xq.activity.LoginActivity;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.util.DisplayUtil;
import com.express56.xq.util.SharedPreUtils;
import com.express56.xq.widget.ToastUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.express56.xq.R;
import com.express56.xq.activity.BaseActivity;
import com.express56.xq.activity.InvokeStaticMethod;
import com.express56.xq.model.RentInfo;
import com.express56.xq.scan.camera.CameraManager;
import com.express56.xq.scan.view.ViewfinderView;
import com.express56.xq.service.UploadService;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.StringUtils;
import com.express56.xq.widget.AllCapTransformationMethod;
import com.express56.xq.widget.CustomDialog;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONObject;

/**
 * 这个activity打开相机，在后台线程做常规的扫描；它绘制了一个结果view来帮助正确地显示条形码，在扫描的时候显示反馈信息，
 * 然后在扫描成功的时候覆盖扫描结果
 */
public final class CaptureActivity extends BaseActivity implements
        SurfaceHolder.Callback, View.OnClickListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    // 相机控制
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private IntentSource source;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;
    // 电量控制
    private InactivityTimer inactivityTimer;
    // 声音、震动控制
    private BeepManager beepManager;

    private ImageView imageButton_back;
    private SurfaceHolder mSurfaceHolder;
    private Camera camera;
    private TextView textView_Btn_Flashing;

    /**
     * 是否手动输入车牌号
     */
    private boolean isInputMode = false;
    private RelativeLayout bottomScan;
    private RelativeLayout bottomInput;
    private EditText serial_EditText;
    private RentInfo rentInfo;
    private ImageView btn_FlashLightControl;

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    /**
     * OnCreate中初始化一些辅助类，如InactivityTimer（休眠）、Beep（声音）以及AmbientLight（闪光灯）
     */
    @Override
    public void onCreate(Bundle icicle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(icicle);
        // 保持Activity处于唤醒状态
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_capture);

        hasSurface = false;

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

        imageButton_back = (ImageView) findViewById(R.id.capture_imageview_back);
        imageButton_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_FlashLightControl = (ImageView) findViewById(R.id.btn_control_flash_light);
        btn_FlashLightControl.setOnClickListener(this);

        textView_Btn_Flashing = (TextView) findViewById(R.id.btn_light_text);

        isInputMode = false;
        bottomScan = (RelativeLayout) findViewById(R.id.bottom_scan);
        bottomInput = (RelativeLayout) findViewById(R.id.bottom_input);
        showBottom();
        init();
    }

    /**
     * 加载底部view
     */
    private void showBottom () {
        if (isInputMode) {
            bottomScan.setVisibility(View.GONE);
            bottomInput.setVisibility(View.VISIBLE);
            findViewById(R.id.btn_scan_back_scan).setOnClickListener(this);
            serial_EditText = (EditText) findViewById(R.id.bike_serial_et);
            serial_EditText.setTransformationMethod(new AllCapTransformationMethod());
        } else {
            bottomScan.setVisibility(View.VISIBLE);
            bottomInput.setVisibility(View.GONE);
            findViewById(R.id.btn_change_input_mode).setOnClickListener(this);
            findViewById(R.id.btn_scan_rent).setOnClickListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // CameraManager必须在这里初始化，而不是在onCreate()中。
        // 这是必须的，因为当我们第一次进入时需要显示帮助页，我们并不想打开Camera,测量屏幕大小
        // 当扫描框的尺寸不正确时会出现bug
        cameraManager = new CameraManager(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);

        handler = null;

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            // activity在paused时但不会stopped,因此surface仍旧存在；
            // surfaceCreated()不会调用，因此在这里初始化camera
            initCamera(surfaceHolder);
        } else {
            // 重置callback，等待surfaceCreated()来初始化camera
            surfaceHolder.addCallback(this);
        }

        beepManager.updatePrefs();
        inactivityTimer.onResume();

        source = IntentSource.NONE;
        decodeFormats = null;
        characterSet = null;
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    /**
     * 扫描成功，处理反馈信息
     *
     * @param rawResult
     * @param barcode
     * @param scaleFactor
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();

        boolean fromLiveScan = barcode != null;
        //这里处理解码完成后的结果，此处将参数回传到Activity处理
        if (fromLiveScan) {
            beepManager.playBeepSoundAndVibrate();
            int actionID = getIntent().getIntExtra("actionID", 0);
            switch (actionID) {
                case ExpressConstant.ACTION_ID_SETTING_SCAN:
                    scanSettingAction(rawResult);
                    break;
                default:
                    scanExpressBarCode(rawResult);
                    break;
            }

        }

    }

    private void scanSettingAction(Result rawResult) {
        if (sp == null) sp = new SharedPreUtils(context);
        HttpHelper.sendRequest_introduce(context, RequestID.REQ_SETTING_SCAN, sp.getUserInfo().token, rawResult.getText(), dialog);
    }

    private void scanExpressBarCode(Result rawResult) {
        // 扫描结果存入数据库
        UploadService.barcodeDataHelper.insertBarcode(rawResult.getText());
//            Toast.makeText(this, getString(R.string.str_scan_barcode_success), Toast.LENGTH_SHORT).show();
        ToastUtil.showMessageTop(this, rawResult.getText(), Toast.LENGTH_SHORT, (int) (52 * DisplayUtil.density));
        Intent intent = getIntent();
        intent.putExtra("codedContent", rawResult.getText());
//			intent.putExtra("codedBitmap", barcode);
//            setResult(RESULT_OK, intent);
//            finish();
        camera.startPreview();
//            Message message = new Message();
//            message.what = R.id.restart_preview;
        handler.sendEmptyMessageDelayed(R.id.restart_preview, ExpressConstant.SCAN_BARCODE_FREQUENCY);
    }

    /**
     * 初始化Camera
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        openCamera(surfaceHolder);
    }

    private void openCamera(SurfaceHolder surfaceHolder) {
        try {
            // 打开Camera硬件设备
            cameraManager.openDriver(surfaceHolder);
            // 创建一个handler来打开预览，并抛出一个运行时异常
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats,
                        decodeHints, characterSet, cameraManager);
            }
            camera = cameraManager.getCamera();
            Camera.Parameters parameters = camera.getParameters();
//            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            parameters.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);

            camera.setParameters(parameters);
            freshBtnText();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            showDialog();
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected error initializing camera", e);
            showDialog();
        }
    }

    private void freshBtnText() {
        String flashMode = camera.getParameters().getFlashMode();
        if (flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
            textView_Btn_Flashing.setText(getString(R.string.str_close_flashing_light));
        } else {
            textView_Btn_Flashing.setText(getString(R.string.str_open_flashing_light));
        }
    }

    /**
     * 显示底层错误信息并退出应用
     */
    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.str_please_grant_permission_of_camera));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    private void showDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        String prompt = getString(R.string.str_please_grant_permission_of_camera);
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
     * 打开闪光灯
     */
    void openFlashlight() {

        try {
            camera = cameraManager.getCamera();
            if (camera == null) return;
            int textureId = 0;

            Camera.Parameters mParameters = camera.getParameters();

            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(mParameters);

        } catch (Exception e) {
        }
    }

    /**
     * 关闭闪光灯
     */
    void closeFlashlight() {
        if (camera != null) {
            Camera.Parameters mParameters = camera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(mParameters);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_control_flash_light:
                camera = cameraManager.getCamera();
                //闪光灯开关操作
                if (checkFlashlight() && camera != null) {
                    String flashMode = camera.getParameters().getFlashMode();
                    if (flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                        textView_Btn_Flashing.setText(getString(R.string.str_open_flashing_light));
                        btn_FlashLightControl.setImageResource(R.drawable.btn_scan_open_light);
                        closeFlashlight();
                    } else {
                        textView_Btn_Flashing.setText(getString(R.string.str_close_flashing_light));
                        btn_FlashLightControl.setImageResource(R.drawable.btn_scan_close_light);
                        openFlashlight();
                    }
                }
                break;
            case R.id.btn_change_input_mode:
                isInputMode = true;
                showBottom();
                break;
            case R.id.btn_scan_back_scan:
                isInputMode = false;
                showBottom();
                break;
            default:
                break;
        }
    }

    @Override
    public void doHttpResponse(Object... param) {
        if (this.isFinishing()) {
            return;
        }
        String result = (String) param[0];
        LogUtil.d(TAG, TAG + "xxx result =" + result);
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
        int requestID = Integer.parseInt(param[1].toString());
        switch (requestID) {
            case RequestID.REQ_SETTING_SCAN:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            ToastUtil.showMessage(context, object.getString("result"));
                        }
                    } else if (code == 0) {
                        showReloginDialog();
                    } else {
                        showErrorMsg(object);
                    }
                    finish();
                }
                break;
            default:
                break;
        }
    }

}
