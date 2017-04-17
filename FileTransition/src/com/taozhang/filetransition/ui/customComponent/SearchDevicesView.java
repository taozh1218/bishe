package com.taozhang.filetransition.ui.customComponent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.taozhang.filetransition.BuildConfig;
import com.taozhang.filetransition.R;

public class SearchDevicesView extends BaseView {

	public static final String TAG = "SearchDevicesView";
	public static final boolean D = BuildConfig.DEBUG;

	// int[] lineColor = new int[]{0x7B, 0x7B, 0x7B};
	// int[] innerCircle0 = new int[]{0xb9, 0xff, 0xFF};
	// int[] innerCircle1 = new int[]{0xdf, 0xff, 0xFF};
	// int[] innerCircle2 = new int[]{0xec, 0xff, 0xFF};

	// int[] argColor = new int[]{0xF3, 0xf3, 0xfa};

	private boolean isSearching = true;
	private Bitmap bitmap;
	// private Bitmap bitmap1;
	private Paint paint;

	public boolean isSearching() {
		return isSearching;
	}

	public void setSearching(boolean isSearching) {
		this.isSearching = isSearching;
		// offsetArgs = 0;
		invalidate();
	}

	public SearchDevicesView(Context context) {
		super(context);
		initBitmap();
	}

	public SearchDevicesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initBitmap();
	}

	public SearchDevicesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initBitmap();
	}

	@SuppressLint("NewApi")
	private void initBitmap() {
//		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//		Point point = new Point();
//		wm.getDefaultDisplay().getSize(point);
//		screenWidth = point.x;
//		screenHeight = point.y;
		if (bitmap == null) {
			bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.gplus_search_bg));
			paint = new Paint();
			paint.setStrokeWidth((float) 6.0);

			coordinateY = bitmap.getHeight() / 2;// ����
		}
		// if(bitmap1 == null){
		// // bitmap1 =
		// Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.locus_round_click));
		// }
//		if (bitmap2 == null) {
//			bitmap2 = Bitmap.createBitmap(BitmapFactory.decodeResource(
//					context.getResources(), R.drawable.gplus_search_args));
//		}
	}

	private int coordinateY;
	private int speed = 10;

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		// ˫�����ͼ
		// android�������ڴ���ֱ���޸�ͼƬ�����и���һ��
//		Bitmap bufferBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//		Canvas bufferCanvas = new Canvas(bufferBitmap);
		canvas.drawLine(0, coordinateY, bitmap.getWidth(), coordinateY,
				paint);
		canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2,
				getHeight() / 2 - bitmap.getHeight() / 2, null);

		// �����ƶ�����
//		Rect rMoon = new Rect(getWidth() / 2 - bitmap2.getWidth(),
//				getHeight() / 2, getWidth() / 2, getHeight() / 2
//						+ bitmap2.getHeight());
		// canvas.rotate(offsetArgs, getWidth() / 2, getHeight() / 2);// ��ת�Ƕ�
		// canvas.drawBitmap(bitmap2, null, rMoon, null);
		if (isSearching) {
			// offsetArgs = offsetArgs + 3;
			coordinateY = coordinateY + speed;
			if (coordinateY >= bitmap.getHeight() - 300 || coordinateY <= 5) {
				speed = -(speed);
			}

		}
		// }else{
		// canvas.drawBitmap(bitmap2, getWidth() / 2 - bitmap2.getWidth() ,
		// getHeight() / 2, null);
		// }

		// canvas.drawBitmap(bitmap1, getWidth() / 2 - bitmap1.getWidth() / 2,
		// getHeight() / 2 - bitmap1.getHeight() / 2, null);

		if (isSearching){
			invalidate();// ���� �ػ棬�ﵽѭ��ɨ���Ŀ��
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			handleActionDownEvenet(event);
			return true;
		case MotionEvent.ACTION_MOVE:
			return true;
		case MotionEvent.ACTION_UP:
			return true;
		}
		return super.onTouchEvent(event);
	}

	private void handleActionDownEvenet(MotionEvent event) {
		RectF rectF = new RectF(getWidth() / 2 - 60, getHeight() / 2 - 60,
				getWidth() / 2 + 60, getHeight() / 2 + 60);

		if (rectF.contains(event.getX(), event.getY())) {// �жϵ����λ���Ƿ�������
			if (D)
				Log.d(TAG, "click search device button");
			if (!isSearching()) {
				setSearching(true);
			} else {
				setSearching(false);
			}
		}
	}
}