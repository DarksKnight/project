package com.express56.xq.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

import com.express56.xq.constant.ExpressConstant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class PicUtil {
    private static final String TAG = PicUtil.class.getSimpleName();
    public static Map<Integer, SoftReference<Bitmap>> caches = new HashMap<Integer, SoftReference<Bitmap>>();
    private static int screenWidth = 0;
    private static int screenHeight = 0;

    /**
     * Android从相册中获取图片以及路径 并且存储到自己的目录中
     */
    public static String getPicturePath(Intent data, Context context) {
        // Intent intent = new Intent(Intent.ACTION_PICK, null);
        // intent.setDataAndType(
        // MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        // startActivityForResult(intent, 1);

        // 插入系统相册 MediaStore.Images.Media.insertImage(getContentResolver(),
        // mBitmap, "title", "description");
        // 插入后要去扫描sd卡，不然打开相册会找不到，因为没有刷新
        // Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 路径的获取方法和下面的ContentResolver 方法一样 insertImage 返回的uri
        // Uri uri = Uri.fromFile(new File("/sdcard/image.jpg"));
        // intent.setData(uri);
        // mContext.sendBroadcast(intent);

        Bitmap bm = null;
        String path = "";
        // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = context.getContentResolver();
        // 此处的用于判断接收的Activity是不是你想要的那个
        try {
            Uri originalUri = data.getData(); // 获得图片的uri
            // 防止oom
            bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); //
            // 显得到bitmap图片
            // 这里开始的第二部分，获取图片的路径：
            String[] proj = {MediaStore.Images.Media.DATA};

            if (originalUri.toString().indexOf("file") == 0) {
                path = originalUri.getPath();
            } else {
                // 好像是android多媒体数据库的封装接口，具体的看Android文档
                Cursor cursor = resolver.query(originalUri, proj, null, null, null);
                // 按我个人理解 这个是获得用户选择的图片的索引值
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                cursor.moveToFirst();
                // 最后根据索引值获取图片路径
                path = cursor.getString(column_index);
            }
            LogUtil.d(TAG, "album pic path:" + path);
            // if (bm != null) {
            // bm.recycle();
            // bm = null;
            // }
            return path;
        } catch (Exception e) {
            LogUtil.e(TAG, "get pic " + e.toString());
            return path;
        }

    }

    public static String getPicSizeJson(String picPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picPath, options);
        // 此时返回的bitmap为null
        /**
         * options.outHeight为原始图片的高
         */
        LogUtil.v("getPicSizeJson", "Bitmap Width == " + options.outWidth);
        LogUtil.v("getPicSizeJson", "Bitmap Height == " + options.outHeight);
        int angle = getExifOrientation(picPath);
        if (angle != 0) { // 如果照片出现了 旋转 那么 就更改旋转度数
            if (angle == 90 || angle == 270) {
                return "{" + options.outHeight + "," + options.outWidth + "}";
            } else {
                return "{" + options.outWidth + "," + options.outHeight + "}";
            }
        } else {
            return "{" + options.outWidth + "," + options.outHeight + "}";
        }

    }

    public static void scanImages(Context context, String filePath) {
        try {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(filePath));
            intent.setData(uri);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 压缩图片并旋转后，保存为temp文件 此方法，虽然对文件压缩了，但是读取到bitmap的内存没有减少，像素比例没有变化 弃用
     *
     * @param srcPath
     * @return
     *
     *         public static String compressImageFromFileAndRotaing(String
     *         srcPath) { Bitmap bitmap = null; bitmap =
     *         BitmapFactory.decodeFile(srcPath);
     *
     *         ByteArrayOutputStream baos = new ByteArrayOutputStream(); //
     *         质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中 int angle =
     *         getExifOrientation(srcPath); LogUtil.e("angle", "angle = " + angle);
     *         int options = 100; if (angle != 0) { options = 60; }
     *         bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos); //
     *         循环判断如果压缩后图片是否大于300kb,大于继续压缩 while (baos.toByteArray().length /
     *         1024 > 500) { baos.reset();// 重置baos即清空baos options -= 20;//
     *         每次都减少20 // 这里压缩options%，把压缩后的数据存放到baos中
     *         bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos); }
     *
     *         try { baos.close(); } catch (IOException e) { // TODO
     *         Auto-generated catch block e.printStackTrace(); }
     *
     *         Bitmap bt = null; if (angle != 0) { Bitmap zoomBitmap =
     *         zoomImage(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
     *         bt = rotaingImageView(angle, zoomBitmap); } else { bt = bitmap; }
     *         String path = saveBitmap(srcPath, bt, options);
     *
     *         return path; }
     */

    /**
     * 此方法较之上面的 按比列读取bitmap 减少了对内存的使用
     *
     * @param srcPath
     * @return
     */
    public static String getSmallImageFromFileAndRotaing1(String srcPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, options);
        options.inJustDecodeBounds = false;
//		options.inSampleSize = calculateInSampleSize(options, 640, 800);
        options.inSampleSize = calculateInSampleSizeByWidth(options, screenWidth);

        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, options);
        if (bitmap == null) {
            return null;
        }
        int angle = getExifOrientation(srcPath);
        if (angle != 0) {
            bitmap = rotaingImageView(angle, bitmap);
        }
        // 有人说下面的压缩方法不管用 经实验 确实如此
        // ByteArrayOutputStream baos = null;
        // try {
        // baos = new ByteArrayOutputStream();
        // bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        // } finally {
        // try {
        // if (baos != null)
        // baos.close();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // }
        int optionsQ = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // 循环判断如果压缩后图片是否大于512kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 512) {
            baos.reset();// 重置baos即清空baos
            // 这里压缩options%，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, optionsQ, baos);
            optionsQ -= 10;// 每次都减少10
        }
        String path = saveBitmap(srcPath, bitmap, optionsQ);
        return path;
    }

    /**
     * @param srcPath
     * @return
     */
    public static String getSmallImageFromFileAndRotaing(String srcPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, options);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Config.ARGB_8888;
//        options.inPreferredConfig = Config.ARGB_8888;
//        options.inSampleSize = calculateInSampleSizeByWidth(options, 1024);
        options.inSampleSize = 1;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, options);
        if (bitmap == null) {
            return null;
        }
        LogUtil.i(TAG, "bitmap.getWidth() = " + bitmap.getWidth() + " bitmap.getHeight() = " + bitmap.getHeight());
        LogUtil.i(TAG, "length decodeFile 1=" + bitmap.getByteCount());
//        bitmap = BitmapUtils.getZoomBitmap(bitmap, 0.8f); // 0.8
//        while (bitmap.getByteCount() > 1024 * 1024 * 1) {
//            bitmap = BitmapUtils.getZoomBitmap(bitmap, ExpressConstant.UPLOAD_PIC_SIZE_WIDTH, ExpressConstant.UPLOAD_PIC_SIZE_HEIGHT);
//            bitmap = BitmapUtils.zoomOutBitmap(bitmap, 0.5f);
//            LogUtil.i(TAG, "length decodeFile 2=" + bitmap.getByteCount());
//        }
//        while (bitmap != null && bitmap.getByteCount() >= 1024 * 1024 * 4) {
//            bitmap = BitmapUtils.getZoomBitmap(bitmap, 0.9f);
//            LogUtil.i(TAG, "length decodeFile 2=" + bitmap.getByteCount());
//            LogUtil.i(TAG, "bitmap.getWidth() = " + bitmap.getWidth() + " bitmap.getHeight() = " + bitmap.getHeight());
//        }

//        //图片旋转到横屏
        int angle = getExifOrientation(srcPath);
        if (angle != 0) {
            bitmap = rotaingImageView(angle, bitmap);
        }


//        bitmap = getScaleBmpByWidth(bitmap, ExpressConstant.UPLOAD_PIC_SIZE_WIDTH, ExpressConstant.UPLOAD_PIC_SIZE_HEIGHT);
//        bitmap = getScaleBmpByWidth(bitmap, ExpressConstant.UPLOAD_PIC_SIZE_WIDTH);
        int optionsQ = 100; // 85
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String path = saveBitmap(srcPath, bitmap, optionsQ);
        return path;
    }

    /**
     * @param srcPath
     * @return
     */
    public static String getHeadPortraitFromFileAndRotaing(String srcPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, options);
        options.inJustDecodeBounds = false;
//        options.inSampleSize = calculateInSampleSizeByWidth(options, 1024);
        options.inSampleSize = 1;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, options);
        if (bitmap == null) {
            return null;
        }

        LogUtil.i(TAG, "head portrait length decodeFile original=" + bitmap.getByteCount());
        if (bitmap.getByteCount() > 1024 * 1024 * 1) {
            bitmap = BitmapUtils.getZoomBitmap(bitmap,
                    ExpressConstant.USER_HEAD_PORTRAIT_PIC_SIZE_WIDTH, ExpressConstant.USER_HEAD_PORTRAIT_PIC_SIZE_WIDTH);
            LogUtil.i(TAG, "head portrait length decodeFile scale =" + bitmap.getByteCount());
        }
//        int angle = getExifOrientation(srcPath);
//        if (angle != 0) {
//            bitmap = rotaingImageView(angle, bitmap);
//        }
//        bitmap = getScaleBmpByWidth(bitmap, ExpressConstant.UPLOAD_PIC_SIZE_WIDTH, ExpressConstant.UPLOAD_PIC_SIZE_HEIGHT);
//        bitmap = getScaleBmpByWidth(bitmap, ExpressConstant.UPLOAD_PIC_SIZE_WIDTH);
        int optionsQ = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String path = saveBitmap(srcPath, bitmap, optionsQ);
        return path;
    }

    /**
     * 获取设备屏幕尺寸
     *
     * @param activity
     * @return
     */
    public static int[] getScreenSize(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(
                displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
//        float density =displayMetrics.density;
        return new int[]{screenWidth, screenHeight};
    }

    /**
     * 获得缩放的Bitmap ,保持宽的比例，高按照宽的比例缩放
     *
     * @param bmp
     * @param width
     * @return
     */
    public static Bitmap getScaleBmpByWidth(Bitmap bmp, float width) {
        if (bmp == null) {
            return null;
        }
        int realWidth = bmp.getWidth();
        int realHeight = bmp.getHeight();

        float scale = width / realWidth;
        if (scale >= 1.0f) {
            return bmp;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap b = null;
        try {
            b = Bitmap.createBitmap(bmp, 0, 0, realWidth, realHeight, matrix, true);
        } catch (OutOfMemoryError e) {
            LogUtil.e(TAG, e.getMessage());
        }
        if (b != bmp) {
            bmp.recycle();
            bmp = null;
            LogUtil.i("getScaleBmpByWidth", "--------recycle----");
        } else {
            LogUtil.i("getScaleBmpByWidth", "--------No !!!----");
        }
        return b;
    }

    /**
     * 获得缩放的Bitmap ,保持宽的比例，高按照宽的比例缩放
     *
     * @param bmp
     * @param width
     * @return
     */
    public static Bitmap getScaleBmpByWidth(Bitmap bmp, float width, float height) {
        if (bmp == null) {
            return null;
        }
        int realWidth = bmp.getWidth();
        int realHeight = bmp.getHeight();

        float scaleW = width / realWidth;
        float scaleH = height / realHeight;
        if (scaleW >= 1.0f && height >= 1.0f) {
            return bmp;
        }
        Matrix matrix = new Matrix();
        float scale = Math.min(scaleW, scaleH);
        matrix.postScale(scale, scale);
        Bitmap b = null;
        try {
            b = Bitmap.createBitmap(bmp, 0, 0, realWidth, realHeight, matrix, true);
            LogUtil.d(TAG, "W:" + b.getWidth() + "H:" + b.getHeight());
        } catch (OutOfMemoryError e) {
            LogUtil.e(TAG, e.getMessage());
        }
        if (b != bmp) {
            bmp.recycle();
            bmp = null;
            LogUtil.i("getScaleBmpByWidth", "--------recycle----");
        } else {
            LogUtil.i("getScaleBmpByWidth", "--------No !!!----");
        }
        return b;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }
        return inSampleSize;
    }

    private static int calculateInSampleSizeByWidth(BitmapFactory.Options options, int reqWidth) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > reqWidth) {
            final int widthRatio = Math.round(width / reqWidth);
            inSampleSize = widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 拍照裁剪后，进行压缩保存。裁剪不压缩比例
     *
     * @param path
     * @param image
     * @param size
     * @return
     */
    public static String compressImageAndSave(String path, Bitmap image, int size) {
        int options = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // 循环判断如果压缩后图片是否大于300kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > size) {
            baos.reset();// 重置baos即清空baos
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 20;// 每次都减少20
        }
        return saveBitmap(path, image, options);
    }

    /***
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {
        // 获取这个图片的宽和高
        int width = bgimage.getWidth();
        int height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放率，新尺寸除原始尺寸
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix, true);
        return bitmap;
    }

    /**
     * 将Drawable转化为Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }

    /**
     * 获得圆角图片的方法
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        if (bitmap == null) {
            return null;
        }
        if (roundPx == 0) {
            return bitmap;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffbcbcbc;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        // if (bitmap != null) {
        // bitmap.recycle();
        // bitmap = null;
        // }
        return output;
    }

    /**
     * 获得圆角图片的方法
     */
    public static Bitmap getRoundedCornerBitmap(Context context, int resource, float roundPx) {
        Bitmap bitmap = null;
        if (caches.containsKey(resource)) {
            if (caches.get(resource).get() == null) {
                caches.remove(resource);
                bitmap = BitmapFactory.decodeResource(context.getResources(), resource);
            } else {
                return caches.get(resource).get();
            }
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), resource);
        }
        if (bitmap == null) {
            return null;
        }
        if (roundPx == 0) {
            return bitmap;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffbcbcbc;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        caches.put(resource, new SoftReference<Bitmap>(output));

        // if (bitmap != null) {
        // bitmap.recycle();
        // bitmap = null;
        // }
        return output;
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
                }
            }
        }
        return degree;
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
        matrix.postRotate(angle);
        // 创建新的图片
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }

    /**
     * 保存方法
     */
    public static String saveBitmap(String srcPath, Bitmap bitmap, int options) {
        LogUtil.e(TAG, "保存图片");
//		String newPath = getTempPicPath(srcPath);
        File f = new File(srcPath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, out);
            out.flush();
            out.close();
            LogUtil.e(TAG, f.getAbsolutePath());
            return f.getAbsolutePath();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    public static String getTempPicPath(String path) {
        int index = path.lastIndexOf(".");
        String temp = path.substring(0, index);
        String postfix = path.substring(index);
        String newPath = temp + "_temp" + postfix;
        return newPath;
    }

    /**
     * 图片转灰度
     *
     * @param bmSrc
     * @return
     */
    public static Bitmap bitmap2Gray(Bitmap bmSrc) {
        Bitmap faceIconGreyBitmap = Bitmap.createBitmap(bmSrc.getWidth(), bmSrc.getHeight(), Config.ARGB_8888);

        Canvas canvas = new Canvas(faceIconGreyBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(bmSrc, 0, 0, paint);
        return faceIconGreyBitmap;
    }

}