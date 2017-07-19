package com.hzkj.wdk.utils;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkj.wdk.model.ConfigModel;
import com.lidroid.xutils.http.RequestParams;
import com.hzkj.wdk.R;
import com.hzkj.wdk.model.MobileModel;
import com.hzkj.wdk.model.UserModel;

import org.apache.http.conn.util.InetAddressUtils;


public class Utils implements  JiaFenConstants{
	private static String phoneReg = "^0{0,1}(13[0-9]|14[0-9]|15[0-9]|18[0-9]|17[0-9])[0-9]{8}$";
	private static String identifyReg = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";
	public static int deviceWidth = 0;
	public static int deviceHeight = 0;
	public static float deviceDensity=1;
	private static String digitReg = "^[1-9]\\d*$";
	private static String digitReg2 = "^\\d+(\\.\\d+)?$";
	private static String starReg = "[`~!@#$%^&*()+=|{}':;'\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。、？]";
	public static  String gender="",industry="";
	public static UserModel userModel;
	public static ConfigModel configModel;
	/**
	 * 网络连接, 1:网络不可用 2:wifi状态 3:手机网络
	 * 
	 * @return
	 */
	public static int checkNetworkInfo(Context c) {
		ConnectivityManager con = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = con.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			// 当前网络不可用
			return 1;
		}
		boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();
		if (wifi) {
			// 使用wifi上网
			return 2;
		}
		return 3;
	}

	// 获取本地ip地址
	public static String getLocAddress() {
		String ipaddress = "";
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			// 遍历所用的网络接口
			while (en.hasMoreElements()) {
				NetworkInterface networks = en.nextElement();
				// 得到每一个网络接口绑定的所有ip
				Enumeration<InetAddress> address = networks.getInetAddresses();
				// 遍历每一个接口绑定的所有ip
				while (address.hasMoreElements()) {
					InetAddress ip = address.nextElement();
					if (!ip.isLoopbackAddress()&& InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
						ipaddress = ip.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			Log.e("====", "获取本地ip地址失败");
			e.printStackTrace();
		}
		//System.out.println("本机IP:" + ipaddress);
		return ipaddress;
	}

	/**
	 * 非空判断（"",null,"null"）
	 * 
	 * @param value
	 *            要验证字符串
	 * @return
	 */
	public static boolean isNull(String value) {
		if (value == null || value.trim().equals("")
				|| value.trim().equals("null")) {
			return true;
		} else
			return false;
	}

	/**
	 * toast
	 * 
	 * @param context
	 * @param text
	 * @return
	 */
	public static Toast toastShow(Context context, String text) {
		Toast toast;
		TextView tv;
		View v;
		v = LayoutInflater.from(context).inflate(R.layout.toast, null);
		tv = ((TextView) v.findViewById(R.id.tv_toast));
		toast = new Toast(context);
		toast.setView(v);
		tv.setText(text);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 100);
		toast.show();
		return toast;
	}
	
	public static Toast toastShowLong(Context context, String text) {
		Toast toast;
		TextView tv;
		View v;
		v = LayoutInflater.from(context).inflate(R.layout.toast, null);
		tv = ((TextView) v.findViewById(R.id.tv_toast));
		toast = new Toast(context);
		toast.setView(v);
		tv.setText(text);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 100);
		toast.show();
		return toast;
	}

	/**
	 * 检查手机号是否填写正确
	 * 
	 * @param phonenumber
	 * @return
	 */
	public static boolean checkIsCellphone(String phonenumber) {
		Pattern p = Pattern.compile(phoneReg);
		Matcher m = p.matcher(phonenumber);
		if (m.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 检测sd卡是否存在
	 */
	public static boolean checkSDCard() {
		try {
			return Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
		} catch (Exception e) {
			return false;
		}
	}

	public static int formatFloat(float ft) {
		BigDecimal bd = new BigDecimal((double) ft);
		int scale = 2;
		bd = bd.setScale(scale, BigDecimal.ROUND_UP);
		int in = (int) (bd.floatValue() * 100);
		return in;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	/**
	 * 时间戳转换为时间格式
	 * 
	 * @param timestampString
	 *            linux时间戳 type:y-m-d 格式
	 * @return
	 */
	public static String TimeStampDate(String timestampString, String type) {
		if (Utils.isNull(timestampString) || timestampString.length()<10)
			return "";
		Long timestamp;
		if (timestampString.length() == 10)
			timestamp = Long.parseLong(timestampString) * 1000;
		else
			timestamp = Long.parseLong(timestampString);
		String date = new SimpleDateFormat(type)
				.format(new Date(timestamp));
		return date;
	}

	// 比较两个时间戳相差天数
	public static int dateDiff(long fromDate, long toDate) {
		int days = 0;
		try {
			days = (int) Math.abs((fromDate - toDate) / (24 * 60 * 60 * 1000)) + 1;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return days;
	}

//	public static String getDateString(Date paramDate) {
//		Calendar localCalendar = Calendar.getInstance();
//		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
//				"yyyy-MM-dd");
//		if (localCalendar.get(1) - (1900 + paramDate.getYear()) > 0)
//			return localSimpleDateFormat.format(paramDate);
//		if (localCalendar.get(2) - paramDate.getMonth() > 0)
//			return localSimpleDateFormat.format(paramDate);
//		if (localCalendar.get(5) - paramDate.getDate() > 6)
//			return localSimpleDateFormat.format(paramDate);
//		if ((localCalendar.get(5) - paramDate.getDate() > 0)
//				&& (localCalendar.get(5) - paramDate.getDate() < 6))
//			return localCalendar.get(11) - paramDate.getHours() + "天前";
//		if (localCalendar.get(11) - paramDate.getHours() > 0)
//			return localCalendar.get(11) - paramDate.getHours() + "小时前";
//		if (localCalendar.get(12) - paramDate.getMinutes() > 0)
//			return localCalendar.get(12) - paramDate.getMinutes() + "分钟前";
//		if (localCalendar.get(13) - paramDate.getSeconds() > 0)
//			return localCalendar.get(13) - paramDate.getSeconds() + "秒前";
//		if (localCalendar.get(13) - paramDate.getSeconds() == 0)
//			return "刚刚";
//		return localSimpleDateFormat.format(paramDate);
//	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param view
	 */
	public static void hideInputMethod(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制
	}
	/**
	 * 隐藏软键盘
	 * 
	 * @param
	 */
	public static void hideInputMethod(Context context){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.toggleSoftInput(0, InputMethodManager.RESULT_HIDDEN);
	}
	/**
	 * 
	 * 功能：判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		} else {
			return false;
		}
		/*
		 * 判断一个字符时候为数字 if(Character.isDigit(str.charAt(0))) { return true; }
		 * else { return false; }
		 */
	}

	/**
	 * 邮箱验证
	 */
	public static boolean checkIsEmail(String strEmail) {
		String strPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean checkIdentify(String identify) {
		Pattern pattern = Pattern.compile(identifyReg);
		Matcher matcher = pattern.matcher(identify);
		return matcher.matches();
	}

	// 简单验证身份证
	public static boolean checkUserId(String id) {
		if (isNull(id)) {
			return false;
		} else if (id.length() != 15 && id.length() != 18) {
			return false;
		} else if (id.length() == 18 && !isNumeric(id.substring(0, 17))) {// 1-17位必须是数字（如果身份证长度是18位）
			return false;
		} else if (id.length() == 15 && !isNumeric(id)) {// 必须是数字（如果身份证长度是15位）
			return false;
		}
		return true;
	}

	/**
	 * 校验银行卡卡号
	 * 
	 * @param cardId
	 * @return
	 */
	public static boolean checkBankCard(String cardId) {
		char bit = getBankCardCheckCode(cardId
				.substring(0, cardId.length() - 1));
		if (bit == 'N') {
			return false;
		}
		return cardId.charAt(cardId.length() - 1) == bit;
	}

	/**
	 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	 * 
	 * @param nonCheckCodeCardId
	 * @return
	 */
	public static char getBankCardCheckCode(String nonCheckCodeCardId) {
		if (nonCheckCodeCardId == null
				|| nonCheckCodeCardId.trim().length() == 0
				|| !nonCheckCodeCardId.matches("\\d+")) {
			// 如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}

	public static boolean validateMoney(String money) {
		Pattern p = Pattern.compile(digitReg);
		Matcher m = p.matcher(money);
		return m.matches();
	}

	// 解决换行
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	public static String formatDouble(double double_value) {
		DecimalFormat df = new DecimalFormat("###.00");
		String number = df.format(double_value);
		return number;
	}

	/**
	 * 格式化金额
	 * 
	 * @param s
	 * @param len
	 * @return
	 */
	public static String formatMoney(String s, int len) {
		if (s == null || s.length() < 1) {
			return "";
		}
		NumberFormat formater = null;
		double num = Double.parseDouble(s);
		if (len == 0) {
			formater = new DecimalFormat("###,###");

		} else {
			StringBuffer buff = new StringBuffer();
			buff.append("###,###.");
			for (int i = 0; i < len; i++) {
				buff.append("#");
			}
			formater = new DecimalFormat(buff.toString());
		}
		String result = formater.format(num);
		if (result.indexOf(".") == -1) {
			result = "￥" + result + ".00";
		} else {
			result = "￥" + result;
		}
		return result;
	}

	public static Spanned formatText(String text) {
		return Html.fromHtml(String.format("<font color=\"#04abf8\">%s</font>",
				text));
	}

	/**
	 * 特殊字符验证
	 * 
	 * @param text
	 * @return
	 */
	public static boolean checkSpecialCharacters(String text) {
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(text);
		if (m.find()) {
			return true;
		}
		return false;
	}

	public static void formatSpannaleTextColor(Context context,
			SpannableString spannable, String reg) {
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(spannable);

		while (m.find()) {
			int start = m.start();
			int end = m.end();
			spannable.setSpan(new ForegroundColorSpan(context.getResources()
					.getColor(R.color.item_orange)), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

	}

	public static void formatSpannaleTextColor(Context context,
			SpannableString spannable) {
		Pattern p = Pattern.compile(starReg);
		Matcher m = p.matcher(spannable);

		while (m.find()) {
			int start = m.start();
			int end = m.end();
			spannable.setSpan(new ForegroundColorSpan(context.getResources()
					.getColor(R.color.item_orange)), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

	}

	public static void formatSpannaleTextSize(SpannableString spannableString,
			int fontSize, String reg) {
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(spannableString);

		while (m.find()) {
			int start = m.start();
			int end = m.end();
			spannableString.setSpan(new AbsoluteSizeSpan(fontSize), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	public static void formatSpannaleFont(Context context,
			SpannableString spannable, int fontSize, String reg) {
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(spannable);

		while (m.find()) {
			int start = m.start();
			int end = m.end();
			spannable.setSpan(new ForegroundColorSpan(context.getResources()
					.getColor(R.color.item_orange)), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannable.setSpan(new AbsoluteSizeSpan(fontSize, true), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

	}

	public static void formatSpannaleFont(Context context,
			SpannableString spannable, int color, int fontSize, String reg) {
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(spannable);

		while (m.find()) {
			int start = m.start();
			int end = m.end();
			spannable.setSpan(new ForegroundColorSpan(context.getResources()
					.getColor(color)), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannable.setSpan(new AbsoluteSizeSpan(fontSize, true), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

	}

	public static void formatSpannaleFont(Context context,
			SpannableString spannable, int fontSize) {
		Pattern p = Pattern.compile(starReg);
		Matcher m = p.matcher(spannable);

		while (m.find()) {
			int start = m.start();
			int end = m.end();
			spannable.setSpan(new ForegroundColorSpan(context.getResources()
					.getColor(R.color.item_orange)), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannable.setSpan(new AbsoluteSizeSpan(fontSize, true), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

	}

	public static void formatSpannalTextColor(Context context,
			SpannableString spannable) {
		Pattern p = Pattern.compile(digitReg2);
		Matcher m = p.matcher(spannable);

		while (m.find()) {
			int start = m.start();
			int end = m.end();
			spannable.setSpan(new ForegroundColorSpan(context.getResources()
					.getColor(R.color.item_orange)), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	public static void formatSpannalTextStar(SpannableString spannable,
			String reg) {
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(spannable);

		while (m.find()) {
			int start = m.start();
			int end = m.end();
			spannable.setSpan(new SpannableString("*"), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	
	/**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

	public static RequestParams addParams(RequestParams rp) {
		rp.addQueryStringParameter("act", "appapi");
		rp.addQueryStringParameter("app_key", "12605981");
		rp.addQueryStringParameter("sign_method", "md5");
		rp.addQueryStringParameter("v", "v1");
		rp.addQueryStringParameter("client", "android");
		rp.addQueryStringParameter("app_v", "1.0");
		return rp;
	}

	public static Map<String, String> getParamsMap(Map<String, String> map) {
		//map.put("act", "appapi");
		map.put("app_key", "12605981");
		map.put("sign_method", "md5");
		map.put("v", "v1");
		map.put("client", "android");
		map.put("app_v", "1.0");
		return map;
	}

	/**
	 *  处理json特殊字符
	 * @param s
	 * @return
	 */
	public static String stringToJson(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '/':
				sb.append("\\/");
				break;
			case '\b': // 退格
				sb.append("\\b");
				break;
			case '\f': // 走纸换页
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");// 换行
				break;
			case '\r': // 回车
				sb.append("\\r");
				break;
			case '\t': // 横向跳格
				sb.append("\\t");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}
	/**
	 * 获取版本号
	 * @param context
	 * @return
	 */
	public static int getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			int version = info.versionCode;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return 1000000;
		}
	}

	/**
	 * 添加联系人
	 * 在同一个事务中完成联系人各项数据的添加
	 * 使用ArrayList<ContentProviderOperation>，把每步操作放在它的对象中执行
	 * */
	public static void AddContacts(Context c,String number,int size){
		// 第一个参数：内容提供者的主机名
		// 第二个参数：要执行的操作
		for(int i=0;i<size;i++){
			Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
			ContentResolver resolver = c.getContentResolver();
			ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
			int rawContactInsertIndex=operations.size();
			Random rand = new Random();
			int rd=rand.nextInt(8999)+1000;
			// 操作1.添加Google账号，这里值为null，表示不添加
			ContentProviderOperation operation = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)// account_name:Google账号
				.build();

			// 操作2.添加data表中name字段
			uri = Uri.parse("content://com.android.contacts/data");
			ContentProviderOperation operation2 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					// 第二个参数int previousResult:表示上一个操作的位于operations的第0个索引，
					// 所以能够将上一个操作返回的raw_contact_id作为该方法的参数
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
					.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, number+"-"+rd)
					.build();

			// 操作3.添加data表中phone字段
			uri = Uri.parse("content://com.android.contacts/data");
			ContentProviderOperation operation3 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
					.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, ""+number+rd)
					.withValue(ContactsContract.CommonDataKinds.Phone.LABEL, "手机号")
					.build();

			// 操作4.添加data表中的Email字段
			uri = Uri.parse("content://com.android.contacts/data");
			ContentProviderOperation operation4 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
				.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
				.withValue(ContactsContract.CommonDataKinds.Email.DATA, "jiafen@qq.com").build();

			operations.add(operation);
			operations.add(operation2);
			operations.add(operation3);
			operations.add(operation4);
			try {
				resolver.applyBatch("com.android.contacts", operations);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 添加联系人
	 * 在同一个事务中完成联系人各项数据的添加
	 * 使用ArrayList<ContentProviderOperation>，把每步操作放在它的对象中执行
	 * */
	public static void AddContacts(Context c,String number){
		// 第一个参数：内容提供者的主机名
		// 第二个参数：要执行的操作
			Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
			ContentResolver resolver = c.getContentResolver();
			ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
			int rawContactInsertIndex=operations.size();

			// 操作1.添加Google账号，这里值为null，表示不添加
			ContentProviderOperation operation = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
					.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)// account_name:Google账号
					.build();

			// 操作2.添加data表中name字段
			uri = Uri.parse("content://com.android.contacts/data");
			ContentProviderOperation operation2 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					// 第二个参数int previousResult:表示上一个操作的位于operations的第0个索引，
					// 所以能够将上一个操作返回的raw_contact_id作为该方法的参数
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
					.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, number)
					.build();

			// 操作3.添加data表中phone字段
			uri = Uri.parse("content://com.android.contacts/data");
			ContentProviderOperation operation3 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
					.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, ""+number)
					.withValue(ContactsContract.CommonDataKinds.Phone.LABEL, "手机号")
					.build();

			// 操作4.添加data表中的Email字段
			uri = Uri.parse("content://com.android.contacts/data");
			ContentProviderOperation operation4 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
					.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
					.withValue(ContactsContract.CommonDataKinds.Email.DATA, "jiafen@qq.com").build();

			operations.add(operation);
			operations.add(operation2);
			operations.add(operation3);
			operations.add(operation4);
			try {
				resolver.applyBatch("com.android.contacts", operations);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	//根据区号查询电话号码
	public static List<MobileModel> queryData(SQLiteDatabase sqLite,String zipcode) {
		List<MobileModel> listD=new ArrayList<MobileModel>();
		String[] cloums={"id","MobileNumber","MobileArea","MobileType","AreaCode","PostCode"};
		String[] args={zipcode};
		Cursor cursor = sqLite.query("mobile", cloums, "PostCode=?", args, null, null, null, null);
		while (cursor.moveToNext()) {
			//String rowid = cursor.getString(cursor.getColumnIndex("rowid"));
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String MobileNumber=cursor.getString(cursor.getColumnIndex("MobileNumber"));
			String MobileArea=cursor.getString(cursor.getColumnIndex("MobileArea"));
			String MobileType=cursor.getString(cursor.getColumnIndex("MobileType"));
			String AreaCode=cursor.getString(cursor.getColumnIndex("AreaCode"));
			String PostCode=cursor.getString(cursor.getColumnIndex("PostCode"));
			MobileModel ma=new MobileModel();
			//ma.setRowid(rowid);
			ma.setId(id);
			ma.setMobileNumber(MobileNumber);
			ma.setMobileArea(MobileArea);
			ma.setMobileType(MobileType);
			ma.setAreaCode(AreaCode);
			ma.setPostCode(PostCode);
			listD.add(ma);
		}
		sqLite.close();
		return listD;
	}
	//随机查询电话号码
	public static List<MobileModel> queryData(SQLiteDatabase sqLite,int num) {
		List<MobileModel> listD=new ArrayList<MobileModel>();
		String[] cloums={"id","MobileNumber","MobileArea","MobileType","AreaCode","PostCode"};
		String[] nums={""+num};
		//Cursor cursor = sqLite.query("mobile", cloums, " LIMIT 200 OFFSET 0", null, null, null, null, null);
		Cursor cursor = sqLite.rawQuery("SELECT * FROM 'mobile' LIMIT ? OFFSET 0", nums);
		while (cursor.moveToNext()) {
			//String rowid = cursor.getString(cursor.getColumnIndex("rowid"));
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String MobileNumber=cursor.getString(cursor.getColumnIndex("MobileNumber"));
			String MobileArea=cursor.getString(cursor.getColumnIndex("MobileArea"));
			String MobileType=cursor.getString(cursor.getColumnIndex("MobileType"));
			String AreaCode=cursor.getString(cursor.getColumnIndex("AreaCode"));
			String PostCode=cursor.getString(cursor.getColumnIndex("PostCode"));
			MobileModel ma=new MobileModel();
			//ma.setRowid(rowid);
			ma.setId(id);
			ma.setMobileNumber(MobileNumber);
			ma.setMobileArea(MobileArea);
			ma.setMobileType(MobileType);
			ma.setAreaCode(AreaCode);
			ma.setPostCode(PostCode);
			listD.add(ma);
		}
		sqLite.close();
		return listD;
	}


	public static void deleteData3(Context c) {
		String id,id1;
		String mimetype;
		ContentResolver contentResolver = c.getContentResolver();
		//只需要从Contacts中获取ID，其他的都可以不要，通过查看上面编译后的SQL语句，可以看出将第二个参数
		//设置成null，默认返回的列非常多，是一种资源浪费。
		Cursor cursor = contentResolver.query(android.provider.ContactsContract.Contacts.CONTENT_URI,
				new String[]{android.provider.ContactsContract.Contacts._ID}, null, null, null);
		while(cursor.moveToNext()) {
			id=cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID));
			id1=cursor.getString(cursor.getColumnIndex(ContactsContract.Data._ID));
			//从一个Cursor获取所有的信息
			Cursor contactInfoCursor = contentResolver.query(
					android.provider.ContactsContract.Data.CONTENT_URI,
					new String[]{android.provider.ContactsContract.Data.CONTACT_ID,
							android.provider.ContactsContract.Data.MIMETYPE,
							android.provider.ContactsContract.Data.DATA1
					},
					android.provider.ContactsContract.Data.CONTACT_ID+"="+id, null, null);

			Cursor contactInfoCursor1 = contentResolver.query(
					android.provider.ContactsContract.Data.CONTENT_URI,
					new String[]{android.provider.ContactsContract.Data.CONTACT_ID,
							android.provider.ContactsContract.Data.MIMETYPE,
							android.provider.ContactsContract.Data.DATA1
					},
					android.provider.ContactsContract.Data.CONTACT_ID+"="+id1, null, null);

			while(contactInfoCursor.moveToNext()) {//查询列，匹配(jiafen@qq.com)
				mimetype = contactInfoCursor.getString(contactInfoCursor.getColumnIndex(android.provider.ContactsContract.Data.MIMETYPE));
				String value = contactInfoCursor.getString(contactInfoCursor.getColumnIndex(android.provider.ContactsContract.Data.DATA1));
				if(mimetype.contains("/email")&&value.contains("jiafen@qq.com")) {
					String where = ContactsContract.Data._ID  + " =?";
					String[] whereparams = new String[]{id};
					contentResolver.delete(ContactsContract.RawContacts.CONTENT_URI, where, whereparams);
				}
			}
			while(contactInfoCursor1.moveToNext()) {//查询列，匹配(jiafen@qq.com)
				mimetype = contactInfoCursor1.getString(contactInfoCursor1.getColumnIndex(android.provider.ContactsContract.Data.MIMETYPE));
				String value = contactInfoCursor1.getString(contactInfoCursor1.getColumnIndex(android.provider.ContactsContract.Data.DATA1));
				if(mimetype.contains("/email")&&value.contains("jiafen@qq.com")) {
					String where = ContactsContract.Data._ID  + " =?";
					String[] whereparams = new String[]{id1};
					contentResolver.delete(ContactsContract.RawContacts.CONTENT_URI, where, whereparams);
				}
			}

			contactInfoCursor.close();
		}

//		Cursor contactsCur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//		while(contactsCur.moveToNext()){
//			mimetype = contactInfoCursor.getString(contactInfoCursor.getColumnIndex(android.provider.ContactsContract.Data.MIMETYPE));
//			//获取ID
//			String rawId = contactsCur.getString(contactsCur.getColumnIndex(ContactsContract.Contacts._ID));
//			//删除
//			String where = ContactsContract.Data._ID  + " =?";
//			String[] whereparams = new String[]{rawId};
//			contentResolver.delete(ContactsContract.RawContacts.CONTENT_URI, where, whereparams);
//		}

		cursor.close();
	}

	public static void initV(Activity act){
		// Here, thisActivity is the current activity
		if(android.os.Build.VERSION.SDK_INT >= 23)
		if (ContextCompat.checkSelfPermission(act,
				Manifest.permission.READ_CONTACTS)
				!= PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(act,
					new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
					MY_PERMISSIONS_REQUEST_CALL_PHONE);
		}
	}

	public static String initJiangPin(String id){
		String title="";
		if("11".equals(id)){
			title="iPhone6";
		}else if("12".equals(id)){
			title="iPhone7";
		}else if("21".equals(id)){
			title="钻石会员1天";
		}else if("22".equals(id)){
			title="钻石会员30天";
		}else if("23".equals(id)){
			title="钻石会员永久";
		}else if("24".equals(id)){
			title="钻石会员永久";
		}else if("25".equals(id)){
			title="黄金会员1天";
		}else if("26".equals(id)){
			title="黄金会员永久";
		}else if("31".equals(id)){
			title="50个加粉名额";
		}else if("32".equals(id)){
			title="100个加粉名额";
		}else if("33".equals(id)){
			title="500个加粉名额";
		}else if("34".equals(id)){
			title="10000个加粉名额";
		}
		return title;
	}

}
