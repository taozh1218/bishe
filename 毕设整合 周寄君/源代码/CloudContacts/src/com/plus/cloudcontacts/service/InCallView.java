package com.plus.cloudcontacts.service;

import com.plus.cloudcontacts.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class InCallView extends View {

	/**
	 * �ı�
	 */
	private String mTitleText;
	/**
	 * �ı�����ɫ
	 */
	private int mTitleTextColor;
	/**
	 * �ı��Ĵ�С
	 */
	private int mTitleTextSize;

	/**
	 * ����ʱ�����ı����Ƶķ�Χ
	 */
	private Rect mBound;
	private Paint mPaint;

	public InCallView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public InCallView(Context context) {
		this(context, null);
	}

	/**
	 * ������Զ������ʽ����
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public InCallView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		/**
		 * ���������������Զ�����ʽ����
		 */
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.InCallView, defStyle, 0);
		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.InCallView_titleText:
				mTitleText = a.getString(attr);
				break;
			case R.styleable.InCallView_titleTextColor:
				// Ĭ����ɫ����Ϊ��ɫ
				mTitleTextColor = a.getColor(attr, Color.BLACK);
				break;
			case R.styleable.InCallView_titleTextSize:
				// Ĭ������Ϊ16sp��TypeValueҲ���԰�spת��Ϊpx
				mTitleTextSize = a.getDimensionPixelSize(attr, (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16,
								getResources().getDisplayMetrics()));
				break;

			}

		}
		a.recycle();

		/**
		 * ��û����ı��Ŀ�͸�
		 */
		mPaint = new Paint();
		mPaint.setTextSize(mTitleTextSize);
		// mPaint.setColor(mTitleTextColor);
		mBound = new Rect();
		mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBound);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    int widthMode = MeasureSpec.getMode(widthMeasureSpec);  
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);  
	    int heightMode = MeasureSpec.getMode(heightMeasureSpec);  
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);  
	    int width;  
	    int height ;  
	    if (widthMode == MeasureSpec.EXACTLY)  
	    {  
	        width = widthSize;  
	    } else  
	    {  
	        mPaint.setTextSize(mTitleTextSize);  
	        mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBound);  
	        float textWidth = mBound.width();  
	        int desired = (int) (getPaddingLeft() + textWidth + getPaddingRight());  
	        width = desired;  
	    }  
	  
	    if (heightMode == MeasureSpec.EXACTLY)  
	    {  
	        height = heightSize;  
	    } else  
	    {  
	        mPaint.setTextSize(mTitleTextSize);  
	        mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBound);  
	        float textHeight = mBound.height();  
	        int desired = (int) (getPaddingTop() + textHeight + getPaddingBottom());  
	        height = desired;  
	    }  
	    setMeasuredDimension(width, height);  
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mPaint.setColor(Color.WHITE);
		mPaint.setAlpha(120);
		RectF rect = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
		canvas.drawRoundRect(rect, 10, 10,  mPaint);

		mPaint.setColor(mTitleTextColor);
		canvas.drawText(mTitleText, getWidth() / 2 - mBound.width() / 2,
				getHeight() / 2 + mBound.height() / 2, mPaint);
	}
	public String getTitleText() {
		return mTitleText;
	}

	public void setTitleText(String mTitleText) {
		this.mTitleText = mTitleText;
	}
}