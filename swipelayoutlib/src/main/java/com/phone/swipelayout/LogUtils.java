package com.phone.swipelayout;

import android.util.Log;

public class LogUtils {

	private static final String TAG = "phone";
	private static boolean isShow = true;

	public static void showV(String msg, boolean show) {
		if (isShow && show) {
			Log.v(TAG, msg);
		}
	}

	public static void showD(String msg, boolean show) {
		if (isShow && show) {
			Log.d(TAG, msg);
		}
	}

	public static void showI(String msg, boolean show) {
		if (isShow && show) {
			Log.i(TAG, msg);
		}
	}

	public static void showW(String msg, boolean show) {
		if (isShow && show) {
			Log.w(TAG, msg);
		}
	}

	public static void showE(String msg) {
		Log.e(TAG, msg);
	}

	public static void showV(String tag, String msg, boolean show) {
		if (isShow && show) {
			Log.v(tag, msg);
		}
	}

	public static void showD(String tag, String msg, boolean show) {
		if (isShow && show) {
			Log.d(tag, msg);
		}
	}

	public static void showI(String tag, String msg, boolean show) {
		if (isShow && show) {
			Log.i(tag, msg);
		}
	}

	public static void showW(String tag, String msg, boolean show) {
		if (isShow && show) {
			Log.w(tag, msg);
		}
	}

	public static void showE(String tag, String msg) {
		Log.e(tag, msg);
	}
}
