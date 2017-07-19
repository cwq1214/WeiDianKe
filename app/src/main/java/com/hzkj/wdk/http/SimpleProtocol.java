package com.hzkj.wdk.http;

import android.content.Context;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 添加新地址
 * 
 * @author admin
 *
 */
public class SimpleProtocol extends BaseProtocol<String> {
	public SimpleProtocol(Context context) {
		super(context);
	}

	public void getData(HttpMethod method, String url, RequestParams params,
			IResponseCallback<String> callback) {
		super.getData(method, url, params, callback);
	}

	/**
	 * 解析json串为对象
	 * 
	 * @param jsonString
	 */
	protected String parseJson(String jsonString) {

		return jsonString;
	}
}
