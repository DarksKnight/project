package com.express56.xq.camera.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.express56.xq.R;
import com.express56.xq.camera.util.DisplayUtil;

public class MaskView extends ImageView {
//	private static final String TAG = "YanZi";
	private Paint mLinePaint;
	private Paint mAreaPaint;
	private Rect mCenterRect = null;
	private Context mContext;


	public MaskView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initPaint();
		mContext = context;
		Point p	= DisplayUtil.getScreenMetrics(mContext);
		widthScreen = p.x;
		heightScreen = p.y;
	}

	private void initPaint(){
		//绘制中间透明区域矩形边界的Paint
		mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLinePaint.setColor(Color.WHITE);
		mLinePaint.setStyle(Style.STROKE);
		mLinePaint.setStrokeWidth(5f);
		mLinePaint.setAlpha(30);

		//绘制四周阴影区域
		mAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		//mAreaPaint.setColor(Color.GRAY);
		mAreaPaint.setColor(Color.BLACK);
		mAreaPaint.setStyle(Style.FILL);
		mAreaPaint.setAlpha(180);



	}
	public void setCenterRect(Rect r){
//		Log.i(TAG, "setCenterRect...");
		this.mCenterRect = r;
		postInvalidate();
	}
	public void clearCenterRect(Rect r){
		this.mCenterRect = null;
	}

	int widthScreen, heightScreen;
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
//		Log.i(TAG, "onDraw...");
		if(mCenterRect == null)
			return;
		//绘制四周阴影区域
		canvas.drawRect(0, 0, widthScreen, mCenterRect.top, mAreaPaint);
		canvas.drawRect(0, mCenterRect.bottom + 1, widthScreen, heightScreen, mAreaPaint);
		canvas.drawRect(0, mCenterRect.top, mCenterRect.left - 1, mCenterRect.bottom  + 1, mAreaPaint);
		canvas.drawRect(mCenterRect.right + 1, mCenterRect.top, widthScreen, mCenterRect.bottom + 1, mAreaPaint);

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		Bitmap borderBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.scan_area_corner);
		canvas.drawBitmap(borderBitmap, mCenterRect.left - 14, mCenterRect.top - 14, paint);
		// 定义矩阵对象
		Matrix matrix = new Matrix();
		// 缩放原图
		matrix.postScale(1f, 1f);
		// 向左旋转45度，参数为正则向右旋转
		matrix.postRotate(90);
		//bmp.getWidth(), 500分别表示重绘后的位图宽高
		Bitmap dstbmp = Bitmap.createBitmap(borderBitmap, 0, 0, borderBitmap.getWidth(), borderBitmap.getHeight(),
				matrix, true);
		canvas.drawBitmap(dstbmp, mCenterRect.right + 14 - borderBitmap.getWidth(), mCenterRect.top - 14, paint);
		matrix = new Matrix();
		matrix.postRotate(180);
		dstbmp = Bitmap.createBitmap(borderBitmap, 0, 0, borderBitmap.getWidth(), borderBitmap.getHeight(),
				matrix, true);
		canvas.drawBitmap(dstbmp, mCenterRect.right + 14 - borderBitmap.getWidth(), mCenterRect.bottom + 14 - borderBitmap.getHeight(), paint);
		matrix = new Matrix();
		matrix.postRotate(-90);
		dstbmp = Bitmap.createBitmap(borderBitmap, 0, 0, borderBitmap.getWidth(), borderBitmap.getHeight(),
				matrix, true);
		canvas.drawBitmap(dstbmp, mCenterRect.left - 14, mCenterRect.bottom + 14 - borderBitmap.getHeight(), paint);

		//绘制目标透明区域
		canvas.drawRect(mCenterRect, mLinePaint);
		super.onDraw(canvas);
	}



}
