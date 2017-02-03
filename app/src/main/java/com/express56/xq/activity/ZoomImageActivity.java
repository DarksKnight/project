
package com.express56.xq.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.util.BitmapUtils;
import com.express56.xq.util.DisplayUtil;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.StringUtils;

import java.io.File;
import java.io.IOException;


public class ZoomImageActivity extends Activity implements OnTouchListener, OnClickListener {
    private static final String TAG = "ZoomImageActivity";

    private static final int NONE = 0;// 初始状态

    private static final int DRAG = 1;// 拖动

    private static final int ZOOM = 2;// 缩放

    private int mode = NONE;

    private PointF prev = new PointF();

    private PointF mid = new PointF();

    private float dist = 1f;

    private Matrix matrix = new Matrix();

    private Matrix savedMatrix = new Matrix();

    private DisplayMetrics dm;

    private TextView returnBtn = null;

    private TextView rotateBtn = null;

    protected ImageView imageView;

    private GestureDetector gestureScanner = null;

    private int title_bar_height = 0;// 保存、返回一栏的高度

    /**
     * 可控变量
     **/
    private float min_scale = 1;// 最小缩放比例

    private static final float MAX_SCALE = 20f;// 最大缩放比例

    private static final float DOUBLE_CLICK_SCALE_SIZE = 2.1f;// 双击方法的倍数

    private Bitmap bitmap = null;
    private String bitmap_FilePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        // 去掉Activity上面的状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 获取分辨率
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        title_bar_height = (int) getResources().getDimension(R.dimen.zoom_image_title_height);
        setContentView(R.layout.activity_zoom_image);
        imageView = (ImageView) findViewById(R.id.imag);// 图片控件
        imageView.setOnTouchListener(this);// 设置触屏监听
        gestureScanner = new GestureDetector(new MySimpleGesture());
        // 返回和保存按钮
        returnBtn = (TextView) findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(this);
        rotateBtn = (TextView) findViewById(R.id.rotateBtn);
        rotateBtn.setOnClickListener(this);

        // 显示默认图片
        bitmap_FilePath = getIntent().getStringExtra("the_bitmap_path");
        byte[] data = getIntent().getByteArrayExtra("bitmap");
        Bitmap tempBitmap = null;
        if (StringUtils.isEmpty(bitmap_FilePath) && data != null && data.length > 0) {
            tempBitmap = BitmapUtils.BytesToBimap(data);
        } else {
            tempBitmap = BitmapUtils.getNotScaledBitmapFromFile(bitmap_FilePath);
        }
        if (tempBitmap == null) return;

        //图片旋转
        tempBitmap = rotaingImageView(45, tempBitmap);

        bitmap = BitmapUtils.getZoomBitmap(tempBitmap, DisplayUtil.screenHeight / (tempBitmap.getWidth() * 1.0f));
//        bitmap = BitmapUtils.getScaleBmpByWidth(bitmap, DisplayUtil.screenWidth);

        showImage();

    }

    /*
 * 旋转图片
 *
 * @param angle
 *
 * @param bitmap
 *
 * @return Bitmap
 */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
//        matrix.postRotate(angle);
        matrix.setRotate(90);
        // 创建新的图片
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }


    /**
     * 得到 图片旋转 的角度
     *
     * @param filepath
     * @return
     */
    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            LogUtil.e("test", "cannot read exif", ex);
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                    default:
                        break;
                }
            }
        }
        return degree;
    }

    private void showImage() {
        if (bitmap == null) {
            LogUtil.i(TAG, "showImage---->>>> bitmap is null");
            return;
        }

        imageView.setImageBitmap(bitmap);// 填充控件

        matrix = new Matrix();// 初始化矩阵

        // 设置最小缩放比例
        minZoom(bitmap.getWidth(), bitmap.getHeight());

        // 设置居中显示
        center();

        imageView.setImageMatrix(matrix);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 重新获取分辨率
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        showImage();
    }

    /**
     * 触屏监听
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureScanner.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 主点按下
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                prev.set(event.getX(), event.getY());
                if (mode == ZOOM) {
                    mode = DRAG;
                }
                break;
            // 副点按下
            case MotionEvent.ACTION_POINTER_DOWN:
                dist = spacing(event);
                // 如果连续两点距离大于10，则判定为多点模式
                if (spacing(event) > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
//                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - prev.x, event.getY() - prev.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float tScale = newDist / dist;
                        matrix.postScale(tScale, tScale, mid.x, mid.y);
                    }
                }
                break;
        }
        imageView.setImageMatrix(matrix);
        CheckView();
        return true;
    }

    /**
     * 限制最大最小缩放比例，自动居中
     */
    private void CheckView() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (mode == ZOOM) {
            if (p[0] < min_scale) {
                matrix.setScale(min_scale, min_scale);
            }
            if (p[0] > MAX_SCALE) {
                matrix.set(savedMatrix);
            }
        }
        center();
    }

    /**
     * 最小缩放比例 (最大为100%)
     */
    private void minZoom(int imgW, int imgH) {
        min_scale = Math.min((float) dm.widthPixels / (float) bitmap.getWidth(),
                (float) dm.heightPixels / (float) bitmap.getHeight());
        if (min_scale < 1.0) {
            matrix.postScale(min_scale, min_scale);
        } else {
            min_scale = 1;
            matrix.postScale(min_scale, min_scale);
        }
        LogUtil.d(TAG, "min_scale=" + min_scale);
    }

    private void center() {
        center(true, true);
    }

    /**
     * 横向、纵向居中
     */
    protected void center(boolean horizontal, boolean vertical) {
        if (bitmap == null) {
            return;
        }
        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            // 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
            int screenHeight = dm.heightPixels - title_bar_height;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = imageView.getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int screenWidth = dm.widthPixels;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 两点的距离
     */
    private float spacing(MotionEvent event) {
        float x = 0;
        float y = 0;
        try {
            x = event.getX(0) - event.getX(1);
            y = event.getY(0) - event.getY(1);
        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
        }
        return (float) Math.sqrt(x * x + y * y);

    }

    /**
     * 两点的中点
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    class MySimpleGesture extends SimpleOnGestureListener {

        // 按两下的第二下Touch down时触发
        @Override
        public boolean onDoubleTap(MotionEvent e) {

            float p[] = new float[9];
            matrix.getValues(p);
            if (mode == NONE) {
                if (p[0] > min_scale) {// 双击变小
                    matrix.setScale(min_scale, min_scale);
                } else {
                    PointF midPointF = new PointF();
                    midPoint(midPointF, e);
                    matrix.setScale(DOUBLE_CLICK_SCALE_SIZE * min_scale, DOUBLE_CLICK_SCALE_SIZE
                            * min_scale, midPointF.x, midPointF.y);
                }
            }
            center();
            return true;
        }// onDoubleTap

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            if ((e1.getX() - e2.getX() > FLING_OFFSET) && Math.abs(velocityX) > FLING_SPEED) {
//                moveNextOrPrevious(1);
//                LogUtil.d("TAG", "[+++++++++++][onFling][Fling left]");
//            } else if ((e2.getX() - e1.getX() > FLING_OFFSET) && Math.abs(velocityX) > FLING_SPEED) {
//                moveNextOrPrevious(-1);
//                LogUtil.d("TAG", "[+++++++++++][onDown][Fling right]");
//            }
            return true;
        }

//        /**
//         * 翻页
//         *
//         * @param id { -1——上翻； +1 ——下翻 }
//         */
//        private void moveNextOrPrevious(final int id) {
//            if (imageCount < 2) {
//                return;
//            }
//            mode = NONE; //翻页后mode置为none
//            switch (id) {
//                case -1:// 左翻
//                    imageIndex--;
//                    if (imageIndex < 0) {
//                        imageIndex = imageCount - 1;
//                    }
//                    updateIndex();
//                    loadImage(imageUrlList.get(imageIndex));
//                    break;
//                case 1:// 右翻
//                    imageIndex++;
//                    if (imageIndex >= imageCount) {
//                        imageIndex = 0;
//                    }
//                    updateIndex();
//                    loadImage(imageUrlList.get(imageIndex));
//                    break;
//
//                default:
//                    break;
//            }
//        }

    }// MySimpleGesture

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.returnBtn:
                ZoomImageActivity.this.finish();
                break;
            case R.id.rotateBtn:
                //旋转
                bitmap = BitmapUtils.getRotatedBitmap(bitmap, 90);
                showImage();
                return;
        }

    }

}
