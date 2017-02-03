package com.express56.xq.camera.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.express56.xq.activity.InvokeStaticMethod;
import com.express56.xq.activity.MainActivity;
import com.express56.xq.activity.SavePhotoActivity;
import com.express56.xq.camera.activity.CameraActivity;
import com.express56.xq.camera.util.FileUtil;
import com.express56.xq.camera.util.ImageUtil;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.service.UploadService;
import com.express56.xq.util.BitmapUtils;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.PicUtil;
import com.express56.xq.util.StringUtils;
import com.express56.xq.widget.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class CameraInterface {
    private static final String TAG = CameraInterface.class.getSimpleName();
    private float ratioHW;

    public Camera getmCamera() {
        return mCamera;
    }

    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    private float mPreviwRate = -1f;
    private static CameraInterface mCameraInterface;
    private boolean hadStartPreview = false;
//    private float ratioW;
//    private float ratioH;

    public interface CamOpenOverCallback {
        public void cameraHasOpened();
    }

    private CameraInterface() {
        hadStartPreview = false;
    }

    public static synchronized CameraInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }
        return mCameraInterface;
    }

    /**
     * 打开Camera
     *
     * @param callback
     */
    public void doOpenCamera(CamOpenOverCallback callback) {
        Log.i(TAG, "Camera open....");
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            if (mCamera == null) {
                CameraActivity.cameraActivity.setResult(Activity.RESULT_OK, new Intent().putExtra("camera_forbidden", true));
                CameraActivity.cameraActivity.finish();
                return;
            }
            e.printStackTrace();
        }

        Log.i(TAG, "Camera open over....");
        callback.cameraHasOpened();
    }

    /**
     * 使用Surfaceview开启预览
     *
     * @param holder
     * @param previewRate
     */
    public void doStartPreview(SurfaceHolder holder, final float previewRate) {
        Log.i(TAG, "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                //实现自动对焦
                if (mCamera == null) return;
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            initCamera(previewRate);//实现相机的参数初始化
                            camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
                        }
                    }

                });
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            initCamera(previewRate);
        }

    }

    /**
     * 使用TextureView预览Camera
     *
     * @param surface
     * @param previewRate
     */
    public void doStartPreview(SurfaceTexture surface, float previewRate) {
        Log.i(TAG, "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(surface);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            initCamera(previewRate);
        }

    }

    /**
     * 停止预览，释放Camera
     */
    public void doStopCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mPreviwRate = -1f;
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 拍照
     */
    public void doTakePicture() {
        if (isPreviewing && (mCamera != null)) {
            mCamera.takePicture(null, null, mJpegPictureCallback);
        }
    }

    public void doTakePicture(int w, int h) {
        if (isPreviewing && (mCamera != null)) {
            try {
                mCamera.autoFocus(null);
                LogUtil.d(TAG, "--------click-to---doTakePicture----------" + System.currentTimeMillis());
                mCamera.takePicture(null, null, mRectJpegPictureCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setWH(int w, int h) {
        Log.i(TAG, "矩形拍照尺寸:width = " + w + " h = " + h);
    }

    public Point doGetPrictureSize() {
        Size s = mCamera.getParameters().getPictureSize();
        return new Point(s.width, s.height);
    }


    private void initCamera(float previewRate) {
        if (mCamera != null) {
            mParams = setCameraParameters();
            mCamera.setParameters(mParams);
            mCamera.startPreview();//开启预览
            hadStartPreview = true;
            mCamera.cancelAutoFocus();
            isPreviewing = true;
            mPreviwRate = previewRate;
            mParams = mCamera.getParameters(); //重新get一次
//            Log.i(TAG, "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width
//                    + "Height = " + mParams.getPreviewSize().height);
//            Log.i(TAG, "最终设置:PictureSize--With = " + mParams.getPictureSize().width
//                    + "Height = " + mParams.getPictureSize().height);
        }
    }

    /**
     * 常规拍照
     */
    PictureCallback mJpegPictureCallback = new PictureCallback()
            //对jpeg图像数据的回调,最重要的一个回调
    {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if (null != data) {
                b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
                mCamera.stopPreview();
                isPreviewing = false;
            }
            //保存图片到sdcard
            if (null != b) {
                //设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。
                //图片竟然不能旋转了，故这里要旋转下
//                Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
//                FileUtil.saveBitmap(rotaBitmap);
                FileUtil.saveBitmap(b);

            }
            //再次进入预览
            mCamera.startPreview();
            isPreviewing = true;
        }
    };

    /**
     * 拍摄指定区域的Rect
     */
    PictureCallback mRectJpegPictureCallback = new PictureCallback()
            //对jpeg图像数据的回调,最重要的一个回调
    {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myJpegCallback:onPictureTaken..." + System.currentTimeMillis());
            Bitmap b = null;
            if (null != data) {
                b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
                mCamera.stopPreview();
                isPreviewing = false;
            }
            //保存图片到sdcard
            if (null != b) {
                //设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。
                //图片竟然不能旋转了，故这里要旋转下
//                Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
                Bitmap rotaBitmap = b;
                Log.i(TAG, "rotaBitmap.getWidth() = " + rotaBitmap.getWidth()
                        + " rotaBitmap.getHeight() = " + rotaBitmap.getHeight());
                Log.i(TAG, "rotaBitmap.ratioW = " + ExpressConstant.PHOTO_PREVIEW_RATIO
                        + " rotaBitmap.ratioH = " + ExpressConstant.PHOTO_PREVIEW_RATIO_HEIGHT);

//                int x = (int) (rotaBitmap.getWidth() / 2 - DST_RECT_WIDTH / 2);
//                int y = (int) (rotaBitmap.getHeight() / 2 - DST_RECT_HEIGHT / 2);
//                Bitmap rectBitmap = Bitmap.createBitmap(rotaBitmap, x, y, (int)DST_RECT_WIDTH, (int)DST_RECT_HEIGHT);
//                BitmapUtils.saveBitmapToFile(rectBitmap, new File(ExpressConstant.IMAGE_FILE_LOCATION_PATH));

                int x = (int) ((rotaBitmap.getWidth() - rotaBitmap.getWidth() * ExpressConstant.PHOTO_PREVIEW_RATIO) / 2);
                int y = (int) ((rotaBitmap.getHeight() - rotaBitmap.getHeight() * ExpressConstant.PHOTO_PREVIEW_RATIO_HEIGHT) / 2);
                int bitmapHeight = (int) (rotaBitmap.getWidth() * ratioHW * ExpressConstant.PHOTO_PREVIEW_RATIO_HEIGHT);
                Bitmap rectBitmap = Bitmap.createBitmap(rotaBitmap, x, y, (int) (rotaBitmap.getWidth() * ExpressConstant.PHOTO_PREVIEW_RATIO), bitmapHeight);
                BitmapUtils.saveBitmapToFile(rectBitmap, new File(ExpressConstant.IMAGE_FILE_LOCATION_PATH));
                BitmapUtils.saveBitmapToFile(rectBitmap, new File(ExpressConstant.ORIGINAL_PHOTO_LOCATION_PATH + File.separator + System.currentTimeMillis() + "_o.jpeg"));

                if (rotaBitmap.isRecycled()) {
                    rotaBitmap.recycle();
                    rotaBitmap = null;
                }

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        selectCamera();//存储图片到数据库
                    }
                });
            }
//            CameraActivity.cameraActivity.setResult(Activity.RESULT_OK, new Intent().putExtra("has_pic", true));
//            CameraActivity.cameraActivity.finish();

//            ToastUtil.showMessage(CameraActivity.cameraActivity, "拍摄成功");
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    //再次进入预览
//                    mCamera.startPreview();
//                    isPreviewing = true;
//                }
//            }, 1000);


//            //再次进入预览
//            mCamera.startPreview();
//            isPreviewing = true;

        }
    };

    public void startPreview() {
        if (mCamera == null)  {
//            try {
//                mCamera = Camera.open();
//            } catch (Exception e) {
//                if (mCamera == null) {
//                    CameraActivity.cameraActivity.setResult(Activity.RESULT_OK, new Intent().putExtra("camera_forbidden", true));
//                    CameraActivity.cameraActivity.finish();
//                    return;
//                }
//                e.printStackTrace();
//            }
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //再次进入预览
                mCamera.startPreview();
                isPreviewing = true;
            }
        }, 0);
    }

    private void selectCamera() {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        String fileName = ExpressConstant.IMAGE_FILE_LOCATION_PATH;
//        try {
//            LogUtil.e(TAG, "file is: " + new FileInputStream(new File(fileName)).available());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        String finalPicturePath = PicUtil.getSmallImageFromFileAndRotaing(fileName);
        CameraActivity.cameraActivity.startActivityForResult(
                new Intent(CameraActivity.cameraActivity, SavePhotoActivity.class).putExtra("file_path", finalPicturePath)
                , ExpressConstant.REQUEST_CODE_START_PREVIEW);
//        startUpload(finalPicturePath);
    }

    /**
     * 设置照相机参数
     */
    private Camera.Parameters setCameraParameters() {
        Camera.Parameters parameters = mCamera.getParameters();
        // 选择合适的预览尺寸
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        if (sizeList.size() > 0) {
            Size cameraSize = sizeList.get(0);
            for (Size size : sizeList) {
                if (size.width > cameraSize.width) {
                    cameraSize = size;
                }
            }
            Log.i(TAG, "rotaBitmap.cameraSize.width = " + cameraSize.width
                    + " rotaBitmap.cameraSize.height = " + cameraSize.height);
            //预览图片大小
            ratioHW = ((float) cameraSize.height) / (cameraSize.width + 0.0f);
            parameters.setPreviewSize(cameraSize.width, cameraSize.height);
//            parameters.setPreviewSize(1280, 720);
//            ratioW = DST_RECT_WIDTH / 1280;
//            ratioH = DST_RECT_HEIGHT / 720;
        }

        //设置生成的图片大小
        sizeList = parameters.getSupportedPictureSizes();
        if (sizeList.size() > 0) {
            Size cameraSize = sizeList.get(0);
            for (Size size : sizeList) {
                //小于1MB
                if (size.width * size.height < 1 * 1024 * 1024) {
                    cameraSize = size;
                    break;
                }

//                if (size.width > cameraSize.width) {
//                    cameraSize = size;
//                }
            }
            parameters.setPictureSize(cameraSize.width, cameraSize.height);
        }
        //设置图片格式
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setJpegQuality(100);
        parameters.setJpegThumbnailQuality(100);
        //自动聚焦模式
//        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
        mCamera.setParameters(parameters);
//        //设置闪光灯模式。此处主要是用于在相机摧毁后又重建，保持之前的状态
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
//        //设置缩放级别
//        setZoom(mZoom);
//        //开启屏幕朝向监听
//        startOrientationChangeListener();
        return parameters;
    }


}
