package com.handmark.pulltorefresh.library.internal;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

	static final String LOG_TAG = "PullToRefresh";

	public static void warnDeprecation(String depreacted, String replacement) {
		Log.w(LOG_TAG, "You're using the deprecated " + depreacted + " attr, please switch over to " + replacement);
	}

//	支持格式为 yyyy.MM.dd G 'at' hh:mm:ss z 如 '2002-1-1 AD at 22:10:59 PSD'
//			* yy/MM/dd HH:mm:ss 如 '2002/1/1 17:55:00'
//			* yy/MM/dd HH:mm:ss pm 如 '2002/1/1 17:55:00 pm'
//			* yy-MM-dd HH:mm:ss 如 '2002-1-1 17:55:00'
//			* yy-MM-dd HH:mm:ss am 如 '2002-1-1 17:55:00 am'



	public static String getFormatedDateTime(String pattern, long dateTime) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
		return sDateFormat.format(new Date(dateTime + 0));
	}

//	pattern代表要转换成的格式，dateTime代表时间的毫秒表示

}
