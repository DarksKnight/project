package com.express56.xq.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;

import com.express56.xq.constant.ExpressConstant;

/**
 * BitmapUtils
 * Bitmap操作类
 *
 * @author Guofeng Huang
 */
public class BitmapUtils {
    private final static String TAG = "BitmapUtils";

    public static void blur(Bitmap bkg, View view, Context context) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 8;
        float radius = 2;

        Bitmap overlay = Bitmap.createBitmap(
                (int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop()
                / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = doBlur(overlay, (int) radius, true);
        view.setBackground(new BitmapDrawable(context.getResources(), overlay));
        System.out.println(System.currentTimeMillis() - startMs + "ms");
    }

    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at="" quasimondo.com="">
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at="" kayenko.com="">
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    public static Bitmap fastBlur(Context context, Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int temp = 256 * divsum;
        int dv[] = new int[temp];
        for (i = 0; i < temp; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);

    }

    /**
     * 回收ImageView的Bitmap
     *
     * @param bitmap
     */
    public static void recyleBitmap(Bitmap bitmap) {
        if ((bitmap != null) && (!bitmap.isRecycled())) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    /**
     * Drawable转Bitmap
     *
     * @param d
     * @return
     */
    public static Bitmap getBitmapFromDrawable(Drawable d) {
        BitmapDrawable bd = (BitmapDrawable) d;
        Bitmap bmp = bd.getBitmap();
        return bmp;
    }

    /**
     * 获得压缩过的图片
     *
     * @param bitmap
     * @param quality
     * @return
     */
    public static Bitmap getCompressedBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Config.RGB_565;
            options.inPurgeable = true;
            options.inInputShareable = true;
            baos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, quality, baos);
            bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos
                    .toByteArray().length, options);
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally { //qiuzy 关闭流
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 获得缩放的图片
     *
     * @param bmp
     * @param pScale 缩放的比例
     * @return
     */
    public static Bitmap getZoomBitmap(Bitmap bmp, float pScale) {
        if (bmp == null) {
            return null;
        }
        int bmpW = bmp.getWidth();
        int bmpH = bmp.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(pScale, pScale);
        Bitmap b = null;
        try {
            b = Bitmap.createBitmap(bmp, 0, 0, bmpW, bmpH, matrix, true);
        } catch (OutOfMemoryError e) {
            LogUtil.e(TAG, e.getMessage());
        }
        if (b != bmp) {
            bmp.recycle();
            bmp = null;
            LogUtil.i("getZoomBitmap", "--------recycle----");
        } else {
            LogUtil.i("getZoomBitmap", "--------No !!!----");
        }
        return b;
    }

    /**
     * 从byte[]中获取压缩后的Bitmap (等比例以2的倍数压缩，有可能比指定的宽高稍大一点，不会丢失)
     *
     * @param b
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getScaledFromBytes(byte[] b, int width, int height) {
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new ByteArrayInputStream(b), null, o);

            //Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < width || height_tmp / 2 < height)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap newBitMap = BitmapFactory.decodeStream(new ByteArrayInputStream(b), null, o2);
            return newBitMap;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e1) {
            LogUtil.e(TAG, e1.getMessage());
        }
        return null;
    }

    /**
     * 获得缩放的Bitmap ,保持比例
     *
     * @param bmp
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getZoomBitmap(Bitmap bmp, int width, int height) {
        if (bmp == null) {
            return null;
        }

        float scaleW = 1, scaleH = 1;// 宽、高缩放的比例
        int bmpW = bmp.getWidth();
        int bmpH = bmp.getHeight();

        scaleW = (scaleW * width) / bmpW;
        scaleH = (scaleH * height) / bmpH;
        float scale = Math.min(scaleW, scaleH);
        if (scale > 1) {
            return bmp;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
//		Bitmap scaledBmp = Bitmap.createBitmap(bmp, 0, 0, bmpW, bmpH, matrix, true);
//		bmp = null;

        Bitmap b = null;
        try {
            b = Bitmap.createBitmap(bmp, 0, 0, bmpW, bmpH, matrix, true);
        } catch (OutOfMemoryError e) {
            LogUtil.e(TAG, e.getMessage());
        }
        //销毁临时bitmap
        if (b != bmp) {
            bmp.recycle();
            bmp = null;
            LogUtil.i("getZoomBitmap", "--------recycle----");
        } else {
            LogUtil.i("getZoomBitmap", "--------No !!!----");
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
     * 保存bitmap 到文件
     *
     * @param bitmap
     * @param file
     */
    public static void saveBitmapToFile(Bitmap bitmap, File file) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 缩小图片到指定的大小
     *
     * @param context
     * @param uri
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getScaleBitmapFromUri(Context context, Uri uri, int width, int height) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            InputStream is = context.getContentResolver().openInputStream(
                    Uri.parse(uri.toString()));
            BitmapFactory.decodeStream(is, null, o);
            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            int scaleW = width_tmp / width;
            int scalH = height_tmp / height;
            if (scaleW == 0 || scalH == 0) {
                scale = 1;
            } else if (scaleW <= scalH) {
                scale = scalH;
            } else {
                scale = scaleW;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            is = context.getContentResolver()
                    .openInputStream(Uri.parse(uri.toString()));
            Bitmap newBitMap = BitmapFactory.decodeStream(is, null, o2);
            return newBitMap;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e1) {
            LogUtil.e(TAG, e1.getMessage());
        }
        return null;
    }

    /**
     * 根据Uri获得Bitmap
     *
     * @param context
     * @param uri
     * @return Bitmap
     */
    public static Bitmap getOriginalBitmapFromUri(Context context, Uri uri) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            LogUtil.e("BitmapUtils", "目录为：" + uri);
            e.printStackTrace();
        } catch (OutOfMemoryError e1) {
            LogUtil.e(TAG, e1.getMessage());
        }
        return null;
    }

    public static String getAbsoluteImagePath(Activity activity, Uri uri) {
//        if (Build.VERSION.SDK_INT <= 11) {

        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.managedQuery(uri,
                proj,                 // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null);                 // Order-by clause (ascending by name)

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);

//        }
//        else {
//            String res = null;
//            String[] proj = {MediaStore.Images.Media.DATA};
//            Cursor cursor = activity.getContentResolver().query(uri, proj, null, null, null);
//            if (cursor != null && cursor.moveToFirst()) {
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                res = cursor.getString(column_index);
//            }
//            cursor.close();
//            return res;
//        }
    }


    /**
     * 获得圆角图片
     *
     * @param bitmap
     * @param roundPx
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = Bitmap
                .createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap = null;
        canvas = null;
        paint = null;
        rect = null;
        rectF = null;
        return output;
    }


    /**
     * 获得旋转过的图片
     *
     * @param bmp
     * @param degrees
     * @return
     */
    public static Bitmap getRotatedBitmap(Bitmap bmp, int degrees) {
        if (degrees != 0 && bmp != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bmp.getWidth() / 2, (float) bmp
                    .getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp
                        .getHeight(), m, true);
                if (bmp != b2) {
                    bmp.recycle();
                    bmp = b2;
                }
            } catch (OutOfMemoryError ex) {
                LogUtil.e(TAG, ex.getMessage());
            }
        }
        return bmp;
    }

    /**
     * 获得制定透明度的图片
     *
     * @param sourceImg
     * @param alpha
     * @return
     */
    public static Bitmap getAlphaBitmap(Bitmap sourceImg, int alpha) {
        if (sourceImg == null) {
            return null;
        }
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg
                .getWidth(), sourceImg.getHeight());// 获得图片的ARGB值
        // number = number * 255 / 100;
        for (int i = 0; i < argb.length; i++) {
            argb[i] = (alpha << 24) | (argb[i] & 0x00FFFFFF);// 修改最高2位的值
        }
        Bitmap b = null;
        try {
            b = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg.getHeight(), Config.ARGB_8888);
        } catch (OutOfMemoryError ex) {
            LogUtil.e(TAG, ex.getMessage());
        }
        if (b != sourceImg) {
            sourceImg.recycle();
            sourceImg = null;
        }
        return b;
    }

    /**
     * 得到原图大小的图片
     *
     * @param res
     * @param resID
     * @return
     */
    public static Bitmap getNotScaledBitmap(Resources res, int resID) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inScreenDensity = 0;
        options.inTargetDensity = 0;
        return BitmapFactory.decodeResource(res, resID, options);
    }

    /**
     * 得到原图大小的图片
     *
     * @param is
     * @return
     */
    public static Bitmap getNotScaledBitmap(InputStream is) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inScreenDensity = 0;
        options.inTargetDensity = 0;
        return BitmapFactory.decodeStream(is, null, options);
    }

    /**
     * @param pathName
     * @return
     */
    public static Bitmap getNotScaledBitmapFromFile(String pathName) {
        return BitmapFactory.decodeFile(pathName);
    }

    /**
     * 缩小图片到指定的大小 (等比例以2的倍数压缩，有可能比指定的宽高稍大一点，不会丢失)
     *
     * @param f
     * @param width
     * @param height
     * @return
     */
    public static Bitmap scaleImageFile(File f, int width, int height) {
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < width || height_tmp < height)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap newBitMap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            return newBitMap;
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    /**
     * 缩小图片到指定的大小 (等比例以2的倍数压缩，有可能比指定的宽高稍大一点，不会丢失)
     *
     * @param f
     * @return
     */
    public static Bitmap getCompressedBitmapFromFile(File f) {
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            if (width_tmp * height_tmp > 1024 * 1024) {
                while (true) {
                    if (width_tmp * height_tmp <= 1024 * 1024)
                        break;
                    width_tmp /= 2;
                    height_tmp /= 2;
                    scale *= 2;
                }
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap newBitMap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            return newBitMap;
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    public static Bitmap getCompressBitmapFromFile(String srcPath) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, opts);
        opts.inJustDecodeBounds = false;
        int w = opts.outWidth;
        int h = opts.outHeight;
        int size = 0;
        if (w <= 800 && h <= 480) {
            size = 1;
        } else {
            double scale = w >= h ? w / 800.0f : h / 480.0f;
            double log = Math.log(scale) / Math.log(2);
            double logCeil = Math.ceil(log);
            size = (int) Math.pow(2, logCeil);
        }
        opts.inSampleSize = size;
        opts.inSampleSize = 1;
        bitmap = BitmapFactory.decodeFile(srcPath, opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        LogUtil.d(TAG, "quality = " + quality + " original =" + baos.toByteArray().length);
        while (baos.toByteArray().length > 100 * 1024 * 1) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            LogUtil.d(TAG, "quality = " + quality + " compressing =" + baos.toByteArray().length);
            quality -= 15;
        }
        LogUtil.d(TAG, "quality = " + quality + " compressed =" + baos.toByteArray().length);
        try {
            baos.writeTo(new FileOutputStream(ExpressConstant.IMAGE_FILE_LOCATION_PATH));
            bitmap = BitmapFactory.decodeFile(ExpressConstant.IMAGE_FILE_LOCATION_PATH);
            LogUtil.d(TAG, "bitmap.getByteCount() =" + bitmap.getByteCount());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 缩小图片到指定的大小 (等比例以2的倍数压缩，有可能比指定的宽高稍大一点，不会丢失)
     *
     * @param f
     * @return
     */
    public static Bitmap getBitmapFromFile(File f) {
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

//            options.inSampleSize = options.outWidth / 200; /*图片长宽方向缩小倍数*/
//            另外，为了节约内存我们还可以使用下面的几个字段：
//            options.inDither=false;    /*不进行图片抖动处理*/
//            options.inPreferredConfig=null;  /*设置让解码器以最佳方式解码*/
//
//            /* 下面两个字段需要组合使用 */
//
//            options.inPurgeable = true;
//
//            options.inInputShareable = true;

            Bitmap newBitMap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);

            long size = newBitMap.getByteCount();
            LogUtil.d(TAG, "size =" + size);
            int w = newBitMap.getWidth();
            int h = newBitMap.getHeight();

            LogUtil.d(TAG, "newBitMap.getWidth() =" + newBitMap.getWidth());
            LogUtil.d(TAG, "newBitMap.getHeight() =" + newBitMap.getHeight());
            LogUtil.d(TAG, "newBitMap.getByteCount() =" + newBitMap.getByteCount());


            float ratio = (1024 * 1024.0f) / (w * h);
//            while (newBitMap.getByteCount() > 1024 * 1024 * 1 && (newBitMap.getWidth() > 600 || newBitMap.getHeight() > 600)) {
//            while (newBitMap.getByteCount() > 1024 * 100 * 1) {

                long size1 = newBitMap.getByteCount();
                LogUtil.d(TAG, "size1 =" + size1);
                int w1 = newBitMap.getWidth();
                int h1 = newBitMap.getHeight();
                LogUtil.d(TAG, "newBitMap.getWidth() =" + newBitMap.getWidth());
                LogUtil.d(TAG, "newBitMap.getHeight() =" + newBitMap.getHeight());

                newBitMap = BitmapUtils.zoomOutBitmap(newBitMap, ratio);

                long size2 = newBitMap.getByteCount();
                int w2 = newBitMap.getWidth();
                int h2 = newBitMap.getHeight();
                LogUtil.d(TAG, "newBitMap.getWidth() =" + newBitMap.getWidth());
                LogUtil.d(TAG, "newBitMap.getHeight() =" + newBitMap.getHeight());
                LogUtil.d(TAG, "size2 =" + size2);
//            }
            return newBitMap;
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    public static Bitmap getSDCardBitmap(String pathString)
    {
        Bitmap bitmap = null;
        try
        {
            File file = new File(pathString);
            if(file.exists())
            {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e)
        {
            // TODO: handle exception
        }


        return bitmap;
    }

    /**
     * 缩放图片到指定的大小 (等比例以2的倍数压缩，有可能比指定的宽高稍大一点，不会丢失)
     *
     * @param f
     * @return
     */
    public static Bitmap getBitmapFromFile(File f, float width) {
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = 1;
            Bitmap newBitMap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            long size = newBitMap.getByteCount();
            LogUtil.d(TAG, "size =" + size);

            int w = newBitMap.getWidth();
            int h = newBitMap.getHeight();
            LogUtil.d(TAG, "newBitMap.getWidth() =" + newBitMap.getWidth());
            LogUtil.d(TAG, "newBitMap.getHeight() =" + newBitMap.getHeight());

            while (newBitMap.getByteCount() > 1024 * 1024 * 1) {

                long size1 = newBitMap.getByteCount();
                LogUtil.d(TAG, "size1 =" + size1);
                int w1 = newBitMap.getWidth();
                int h1 = newBitMap.getHeight();
                LogUtil.d(TAG, "newBitMap.getWidth() =" + newBitMap.getWidth());
                LogUtil.d(TAG, "newBitMap.getHeight() =" + newBitMap.getHeight());

                newBitMap = BitmapUtils.zoomOutBitmap(newBitMap, 0.8f);

                long size2 = newBitMap.getByteCount();
                int w2 = newBitMap.getWidth();
                int h2 = newBitMap.getHeight();
                LogUtil.d(TAG, "newBitMap.getWidth() =" + newBitMap.getWidth());
                LogUtil.d(TAG, "newBitMap.getHeight() =" + newBitMap.getHeight());
                LogUtil.d(TAG, "size2 =" + size2);
            }

            if (newBitMap == null) {
                return null;
            }
            Matrix matrix = new Matrix();
            float scale = width / newBitMap.getWidth();
            matrix.postScale(scale, scale);
            Bitmap b = null;
            try {
                b = Bitmap.createBitmap(newBitMap, 0, 0, w, h, matrix, true);
            } catch (OutOfMemoryError e) {
                LogUtil.e(TAG, e.getMessage());
            }
            if (b != newBitMap) {
//            bmp.recycle();
                newBitMap = null;
                LogUtil.i("getZoomBitmap", "--------recycle----");
            } else {
                LogUtil.i("getZoomBitmap", "--------No !!!----");
            }

            return b;
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    /**
     * 将bitmap保存到文件
     *
     * @param bitmap
     * @param photo
     * @param quality
     * @return
     */
    public static File getFileFromBitmap(Bitmap bitmap, File photo, int quality) {
        if (photo != null && photo.exists()) {
            photo.delete();
        }
        try {
            photo.createNewFile();
        } catch (IOException e1) {
            LogUtil.e("bad file", e1.toString());
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(photo);
            bitmap.compress(CompressFormat.JPEG, quality, fos);  //writes and compresses JPEG to file
            fos.flush();
            fos.close();
        } catch (IOException e) {
            LogUtil.e("saving photo to a file", e.toString());
        } finally { // 添加关闭流操作
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return photo;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                                : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }


//    static int* StackBlur(int* pix, int w, int h, int radius) {
//        int wm = w - 1;
//        int hm = h - 1;
//        int wh = w * h;
//        int div = radius + radius + 1;
//
//        int *r = (int *)malloc(wh * sizeof(int));
//        int *g = (int *)malloc(wh * sizeof(int));
//        int *b = (int *)malloc(wh * sizeof(int));
//        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
//
//        int *vmin = (int *)malloc(MAX(w,h) * sizeof(int));
//
//        int divsum = (div + 1) >> 1;
//        divsum *= divsum;
//        int *dv = (int *)malloc(256 * divsum * sizeof(int));
//        for (i = 0; i < 256 * divsum; i++) {
//            dv[i] = (i / divsum);
//        }
//
//        yw = yi = 0;
//
//        int(*stack)[3] = (int(*)[3])malloc(div * 3 * sizeof(int));
//        int stackpointer;
//        int stackstart;
//        int *sir;
//        int rbs;
//        int r1 = radius + 1;
//        int routsum, goutsum, boutsum;
//        int rinsum, ginsum, binsum;
//
//        for (y = 0; y < h; y++) {
//            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
//            for (i = -radius; i <= radius; i++) {
//                p = pix[yi + (MIN(wm, MAX(i, 0)))];
//                sir = stack[i + radius];
//                sir[0] = (p & 0xff0000) >> 16;
//                sir[1] = (p & 0x00ff00) >> 8;
//                sir[2] = (p & 0x0000ff);
//
//                rbs = r1 - ABS(i);
//                rsum += sir[0] * rbs;
//                gsum += sir[1] * rbs;
//                bsum += sir[2] * rbs;
//                if (i > 0) {
//                    rinsum += sir[0];
//                    ginsum += sir[1];
//                    binsum += sir[2];
//                }
//                else {
//                    routsum += sir[0];
//                    goutsum += sir[1];
//                    boutsum += sir[2];
//                }
//            }
//            stackpointer = radius;
//
//            for (x = 0; x < w; x++) {
//
//                r[yi] = dv[rsum];
//                g[yi] = dv[gsum];
//                b[yi] = dv[bsum];
//
//                rsum -= routsum;
//                gsum -= goutsum;
//                bsum -= boutsum;
//
//                stackstart = stackpointer - radius + div;
//                sir = stack[stackstart % div];
//
//                routsum -= sir[0];
//                goutsum -= sir[1];
//                boutsum -= sir[2];
//
//                if (y == 0) {
//                    vmin[x] = MIN(x + radius + 1, wm);
//                }
//                p = pix[yw + vmin[x]];
//
//                sir[0] = (p & 0xff0000) >> 16;
//                sir[1] = (p & 0x00ff00) >> 8;
//                sir[2] = (p & 0x0000ff);
//
//                rinsum += sir[0];
//                ginsum += sir[1];
//                binsum += sir[2];
//
//                rsum += rinsum;
//                gsum += ginsum;
//                bsum += binsum;
//
//                stackpointer = (stackpointer + 1) % div;
//                sir = stack[(stackpointer) % div];
//
//                routsum += sir[0];
//                goutsum += sir[1];
//                boutsum += sir[2];
//
//                rinsum -= sir[0];
//                ginsum -= sir[1];
//                binsum -= sir[2];
//
//                yi++;
//            }
//            yw += w;
//        }
//        for (x = 0; x < w; x++) {
//            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
//            yp = -radius * w;
//            for (i = -radius; i <= radius; i++) {
//                yi = MAX(0, yp) + x;
//
//                sir = stack[i + radius];
//
//                sir[0] = r[yi];
//                sir[1] = g[yi];
//                sir[2] = b[yi];
//
//                rbs = r1 - ABS(i);
//
//                rsum += r[yi] * rbs;
//                gsum += g[yi] * rbs;
//                bsum += b[yi] * rbs;
//
//                if (i > 0) {
//                    rinsum += sir[0];
//                    ginsum += sir[1];
//                    binsum += sir[2];
//                }
//                else {
//                    routsum += sir[0];
//                    goutsum += sir[1];
//                    boutsum += sir[2];
//                }
//
//                if (i < hm) {
//                    yp += w;
//                }
//            }
//            yi = x;
//            stackpointer = radius;
//            for (y = 0; y < h; y++) {
//                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
//                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];
//
//                rsum -= routsum;
//                gsum -= goutsum;
//                bsum -= boutsum;
//
//                stackstart = stackpointer - radius + div;
//                sir = stack[stackstart % div];
//
//                routsum -= sir[0];
//                goutsum -= sir[1];
//                boutsum -= sir[2];
//
//                if (x == 0) {
//                    vmin[y] = MIN(y + r1, hm) * w;
//                }
//                p = x + vmin[y];
//
//                sir[0] = r[p];
//                sir[1] = g[p];
//                sir[2] = b[p];
//
//                rinsum += sir[0];
//                ginsum += sir[1];
//                binsum += sir[2];
//
//                rsum += rinsum;
//                gsum += ginsum;
//                bsum += binsum;
//
//                stackpointer = (stackpointer + 1) % div;
//                sir = stack[stackpointer];
//
//                routsum += sir[0];
//                goutsum += sir[1];
//                boutsum += sir[2];
//
//                rinsum -= sir[0];
//                ginsum -= sir[1];
//                binsum -= sir[2];
//
//                yi += w;
//            }
//        }
//
//        free(r);
//        free(g);
//        free(b);
//        free(vmin);
//        free(dv);
//        free(stack);
//        return(pix);
//    }

    // 缩放图片
    public static Bitmap zoomOutBitmap(String img, int newWidth, int newHeight) {
// 图片源
        Bitmap bm = BitmapFactory.decodeFile(img);
        if (null != bm) {
            return zoomOutBitmap(bm, newWidth, newHeight);
        }
        return null;
    }

    public static Bitmap zoomOutBitmap(Context context, String img, int newWidth, int newHeight) {
        // 图片源
        try {
            Bitmap bm = BitmapFactory.decodeStream(context.getAssets()
                    .open(img));
            if (null != bm) {
                return zoomOutBitmap(bm, newWidth, newHeight);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    // 缩放图片
    public static Bitmap zoomOutBitmap(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }


    /**
     * 缩放图片 按倍数
     *
     * @param bm
     * @param zoomRatio
     * @return
     */
    public static Bitmap zoomOutBitmap(Bitmap bm, float zoomRatio) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(zoomRatio, zoomRatio);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 将图片内容解析成字节数组
     *
     * @param
     * @param inStream
     * @return byte[]
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;

    }

    /**
     * 将字节数组转换为ImageView可调用的Bitmap对象s
     *
     * @param
     * @param bytes
     * @param opts
     * @return Bitmap
     */
    public static Bitmap getPicFromBytes(byte[] bytes,
                                         BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    /**
     * 图片缩放
     *
     * @param
     * @param bitmap 对象
     * @param w      要缩放的宽度
     * @param h      要缩放的高度
     * @return newBmp 新 Bitmap对象
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return newBmp;
    }

    /**
     * 把Bitmap转Byte
     *
     * @Author HEH
     * @EditTime 2010-07-19 上午11:45:56
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

//    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
//        int w = bitmap.getWidth();
//        int h = bitmap.getHeight();
//        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
//        Canvas canvas = new Canvas(output);
//        final int color = 0xff424242;
//        final Paint paint = new Paint();
//        final Rect rect = new Rect(0, 0, w, h);
//        final RectF rectF = new RectF(rect);
//        paint.setAntiAlias(true);
//        canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(color);
//        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//        canvas.drawBitmap(bitmap, rect, rect, paint);
//        return output;
//    }

    /**
     * 获得带倒影的图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w, h / 2, matrix, false);
        Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);
        canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);
        Paint paint = new Paint();

        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, h, w, bitmapWithReflection.getHeight() + reflectionGap, paint);
        return bitmapWithReflection;
    }

    public static Bitmap BytesToBimap(byte[] b) {
        if (b != null && b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

}
