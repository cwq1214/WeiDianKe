package com.hzkj.wdk.utils;

import android.os.Environment;

/**
 * 常量、接口等存放
 * 
 * @author admin
 * 
 */
public interface JiaFenConstants {
	String PACKAGE_NAME = "com.simida.jiafen";
	String SD_PATH = Environment.getExternalStorageDirectory().toString();

	String FILE_PATH = SD_PATH + "/" + PACKAGE_NAME;

	String FILE_CACHE = FILE_PATH + "/dataCache";// 数据

	String IMAGE_CACHE = FILE_PATH + "/imgCache/";// 图片

	String ALITAIL_FOLDER = "JiaFen";// 本地文件目录

	String DBNAME = "xinbiao.sqlite";

	String DBPATH="data/data/com.simida.jiafen/files/" + DBNAME;
	String action_refresh = "refresh";
	String GET_DATA = "getdata";
	String LOAD_BAOFEN = "baofen";
	final int PHOTO_PICKED_WITH_DATA = 3021;
	final int CAMERA_WITH_DATA = 3023;
	final int CAMERA_WITH_DATA_ZOOM = 3025;// 图片截取

	int NETWORK_NONE = 1;// 无网
	int NETWORK_WIFI = 2;// wifi
	int NETWORK_MOBILE = 3;// 移动网络
	final String CALLUNITYJSON="CALLUNITYJSON";
	final String TOKEN="token";
	final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;


	/************************berton test********************/
	//String url = "http://192.168.30.31/soap/work.php";
	/******************************************************/
	//	 String SERVER_URL="http://192.168.30.33/";
	/**
	 * 测试环境接口
	 */
	//http://api.3dshiyi.com/api.php?act=appapi
	 //String SERVER_URL = "http://api.3dshiyi.com/api.php";//?act=appapi
	 //String SERVER_URL = "http://jiafen.liekecrm.com/";//?act=appapi
//	String SERVER_URL="http://zfjiafen.heizitech.com/index.php";
//	String SERVER_URL="http://119.23.66.37/zhuanfa_jiafen/index.php";
	String SERVER_URL="http://weike.qb1611.cn/zhuanfa_jiafen/index.php";

}
