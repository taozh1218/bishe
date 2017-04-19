package com.taozhang.filetransition.util;

import android.app.Activity;
import android.util.DisplayMetrics;

public class MyViewUtil {
	/**
	 * ��ȡ�ֻ��ֱ���
	 * 
	 * @param mActivity
	 * @return
	 */
	public static int getScreenDPI(Activity mActivity) {
		DisplayMetrics metric = getScreenMetrics(mActivity);
		int dpi = metric.densityDpi;
		return dpi;
	}

	public static int getScreenWidthPixels(Activity mActivity) {
		DisplayMetrics metric = getScreenMetrics(mActivity);
		int widthPixels = metric.widthPixels; // ��Ļ��ȣ����أ�
		return widthPixels;
	}

	public static int getScreenHeightPixels(Activity mActivity) {
		DisplayMetrics metric = getScreenMetrics(mActivity);
		int heightPixels = metric.heightPixels; // ��Ļ�߶ȣ����أ�
		return heightPixels;
	}

	/**
	 * ��Activity��ȡ��Ļ����
	 * 
	 * @param mActivity
	 * @return
	 */
	private static DisplayMetrics getScreenMetrics(Activity mActivity) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		return displayMetrics;
	}
}
