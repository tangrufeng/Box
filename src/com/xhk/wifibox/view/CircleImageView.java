/* 
 * @Title:  CircleHeaderImageView.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-7-30 下午6:13:44 
 * @version:  V1.0 
 */
package com.xhk.wifibox.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xhk.wifibox.R;

/**
 * 
 * 圆形ImageView，可设置最多两个宽度不同且颜色不同的圆形边框。
 * 
 * 设置颜色在xml布局文件中由自定义属性配置参数指定
 */

public class CircleImageView extends ImageView {

	private int mBorderThickness = 0;

	private Context mContext;

	private int defaultColor = 0xFFFFFFFF;

	// 如果只有其中一个有值，则只画一个圆形边框

	private int mBorderOutsideColor = 0;

	private int mBorderInsideColor = 0;

	private int mSex = 0;

	// 控件默认长、宽

	private int defaultWidth = 0;

	private int defaultHeight = 0;

	/**
	 * @return the mBorderThickness
	 */
	public int getmBorderThickness() {
		return mBorderThickness;
	}

	/**
	 * @param mBorderThickness
	 *            the mBorderThickness to set
	 */
	public void setmBorderThickness(int mBorderThickness) {
		this.mBorderThickness = mBorderThickness;
	}

	/**
	 * @return the defaultColor
	 */
	public int getDefaultColor() {
		return defaultColor;
	}

	/**
	 * @param defaultColor
	 *            the defaultColor to set
	 */
	public void setDefaultColor(int defaultColor) {
		this.defaultColor = defaultColor;
	}

	/**
	 * @return the mBorderOutsideColor
	 */
	public int getmBorderOutsideColor() {
		return mBorderOutsideColor;
	}

	/**
	 * @param mBorderOutsideColor
	 *            the mBorderOutsideColor to set
	 */
	public void setmBorderOutsideColor(int mBorderOutsideColor) {
		this.mBorderOutsideColor = mBorderOutsideColor;
	}

	/**
	 * @return the mBorderInsideColor
	 */
	public int getmBorderInsideColor() {
		return mBorderInsideColor;
	}

	/**
	 * @param mBorderInsideColor
	 *            the mBorderInsideColor to set
	 */
	public void setmBorderInsideColor(int mBorderInsideColor) {
		this.mBorderInsideColor = mBorderInsideColor;
	}

	/**
	 * @return the mSex
	 */
	public int getmSex() {
		return mSex;
	}

	/**
	 * @param mSex
	 *            the mSex to set
	 */
	public void setmSex(int mSex) {
		this.mSex = mSex;
	}

	/**
	 * @return the defaultWidth
	 */
	public int getDefaultWidth() {
		return defaultWidth;
	}

	/**
	 * @param defaultWidth
	 *            the defaultWidth to set
	 */
	public void setDefaultWidth(int defaultWidth) {
		this.defaultWidth = defaultWidth;
	}

	/**
	 * @return the defaultHeight
	 */
	public int getDefaultHeight() {
		return defaultHeight;
	}

	/**
	 * @param defaultHeight
	 *            the defaultHeight to set
	 */
	public void setDefaultHeight(int defaultHeight) {
		this.defaultHeight = defaultHeight;
	}

	public CircleImageView(Context context) {

		super(context);

		mContext = context;

	}

	public CircleImageView(Context context, AttributeSet attrs) {

		super(context, attrs);

		mContext = context;

		setCustomAttributes(attrs);

	}

	public CircleImageView(Context context, AttributeSet attrs,
			int defStyle) {

		super(context, attrs, defStyle);

		mContext = context;

		setCustomAttributes(attrs);

	}

	private void setCustomAttributes(AttributeSet attrs) {

		TypedArray a = mContext.obtainStyledAttributes(attrs,
				R.styleable.CircleImageView);

		try {
			mBorderThickness = a.getDimensionPixelSize(
					R.styleable.CircleImageView_border_thickness, 0);

			mBorderOutsideColor = a.getColor(
					R.styleable.CircleImageView_border_outside_color,
					defaultColor);

			mBorderInsideColor = a.getColor(
					R.styleable.CircleImageView_border_inside_color,
					defaultColor);
		} finally {
			a.recycle();
		}
	}

	private  Bitmap drawableToBitmap(Drawable drawable) {
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		Drawable drawable = getDrawable();
		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}

		this.measure(0, 0);
		if (drawable.getClass() == NinePatchDrawable.class)
			return;
//		BitmapFactory.Options opts=new BitmapFactory.Options();
//		opts.inTempStorage=new byte[100*1024];
//		opts.inPreferredConfig = Bitmap.Config.RGB_565;
//		opts.inPurgeable = true;
//		opts.inSampleSize = 4;
//		Bitmap b = ((BitmapDrawable) drawable).
//		Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
		Bitmap b=drawableToBitmap(drawable);
		if (defaultWidth == 0) {
			defaultWidth = getWidth();
		}
		if (defaultHeight == 0) {
			defaultHeight = getHeight();
		}

		int radius = 0;
		if (mBorderInsideColor != defaultColor
				&& mBorderOutsideColor != defaultColor) {// 定义画两个边框，分别为外圆边框和内圆边框
			radius = (defaultWidth < defaultHeight ? defaultWidth
					: defaultHeight) / 2 - 2 * mBorderThickness;
			// 画内圆
			drawCircleBorder(canvas, radius + mBorderThickness / 2,
					mBorderInsideColor);
			// 画外圆
			drawCircleBorder(canvas, radius + mBorderThickness
					+ mBorderThickness / 2, mBorderOutsideColor);
		} else if (mBorderInsideColor != defaultColor
				&& mBorderOutsideColor == defaultColor) {// 定义画一个边框
			radius = (defaultWidth < defaultHeight ? defaultWidth
					: defaultHeight) / 2 - mBorderThickness;
			drawCircleBorder(canvas, radius + mBorderThickness / 2,
					mBorderInsideColor);
		} else if (mBorderInsideColor == defaultColor
				&& mBorderOutsideColor != defaultColor) {// 定义画一个边框
			radius = (defaultWidth < defaultHeight ? defaultWidth
					: defaultHeight) / 2 - mBorderThickness;
			drawCircleBorder(canvas, radius + mBorderThickness / 2,
					mBorderOutsideColor);
		} else {// 没有边框
			radius = (defaultWidth < defaultHeight ? defaultWidth
					: defaultHeight) / 2;
		}
		Bitmap roundBitmap = getCroppedRoundBitmap(b, radius);
		canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight
				/ 2 - radius, null);

		b.recycle();
		roundBitmap.recycle();
	}


	/**
	 * 
	 * 获取裁剪后的圆形图片
	 * 
	 * @param radius半径
	 */

	public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
		Bitmap scaledSrcBmp;
		int diameter = radius * 2;
		// 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		int squareWidth = 0, squareHeight = 0;
		int x = 0, y = 0;
		Bitmap squareBitmap;
		if (bmpHeight > bmpWidth) {// 高大于宽
			squareWidth = squareHeight = bmpWidth;
			x = 0;
			y = (bmpHeight - bmpWidth) / 2;
			// 截取正方形图片
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
					squareHeight);
		} else if (bmpHeight < bmpWidth) {// 宽大于高
			squareWidth = squareHeight = bmpHeight;
			x = (bmpWidth - bmpHeight) / 2;
			y = 0;
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
					squareHeight);
		} else {
			squareBitmap = bmp;
		}
		if (squareBitmap.getWidth() != diameter
				|| squareBitmap.getHeight() != diameter) {
			scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
					diameter, true);
		} else {
			scaledSrcBmp = squareBitmap;
		}

		Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
				scaledSrcBmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
				scaledSrcBmp.getHeight());
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
				scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
				paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
		bmp = null;
		squareBitmap = null;
		scaledSrcBmp = null;
		return output;

	}

	/**
	 * 
	 * 边缘画圆
	 */

	private void drawCircleBorder(Canvas canvas, int radius, int color) {
		Paint paint = new Paint();
		/* 去锯齿 */
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		paint.setColor(color);
		/* 设置paint的　style　为STROKE：空心 */
		paint.setStyle(Paint.Style.STROKE);
		/* 设置paint的外框宽度 */
		paint.setStrokeWidth(mBorderThickness);
		canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);
	}

}
