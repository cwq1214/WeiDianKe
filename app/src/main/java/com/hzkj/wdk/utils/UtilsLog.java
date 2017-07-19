package com.hzkj.wdk.utils;

import android.app.ActionBar.Tab;
import android.util.Log;

/**
 * 打印log
 * 测试�? isTest 改为true
 * 正式上线改为false
 * false 不打�?
 * true 打印--
 * @author peng
 *
 */
public class UtilsLog {

	public static boolean isTest = true;
	private static final String TAG="====";
	
	public static void d(String str){
		if (isTest) {
			d(TAG, str);
		}
	}
	public static void d(String s1, String s2) {
		if (isTest)
			Log.d(s1, s2);
	}
	
	

	public static void e(String paramString1, String paramString2) {
		if (isTest)
			Log.e(paramString1, paramString2);
	}

	public static void i(String paramString1, String paramString2) {
		if (isTest)
			Log.i(paramString1, paramString2);
	}

	public static void log(String paramString1, String paramString2) {
		StackTraceElement[] arrayOfStackTraceElement = new Throwable()
				.getStackTrace();
		if (isTest) {
			StackTraceElement localStackTraceElement = arrayOfStackTraceElement[1];
			Object[] arrayOfObject = new Object[4];
			arrayOfObject[0] = localStackTraceElement.getClassName();
			arrayOfObject[1] = Integer.valueOf(localStackTraceElement
					.getLineNumber());
			arrayOfObject[2] = localStackTraceElement.getMethodName();
			arrayOfObject[3] = paramString2;
			Log.e(paramString1,
					String.format("======[%s][%s][%s]=====%s", arrayOfObject));
		}
	}

	public static void w(String paramString1, String paramString2) {
		if (isTest)
			Log.w(paramString1, paramString2);
	}

	public static void w(String paramString1, String paramString2,
			Throwable paramThrowable) {
		if (isTest)
			Log.w(paramString1, paramString2, paramThrowable);
	}

	public static void w(String paramString, Throwable paramThrowable) {
		if (isTest)
			Log.w(paramString, paramThrowable);
	}
}
