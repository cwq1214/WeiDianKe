package com.hzkj.wdk.http;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.utils.SharePreferenceUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.http.client.util.URLEncodedUtils;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.UtilsLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public abstract class BaseProtocol<T> implements JiaFenConstants,
		ErrorConstants {
	protected WeakReference<IResponseCallback<T>> mTarget; //
	protected String cacheUrl;
	protected Context context;
	protected HttpUtils http;
	private IResponseCallback<T> target;
	private SharePreferenceUtil spu;
	public BaseProtocol(Context context) {
		this.context = context;
		spu=new SharePreferenceUtil(context);
	}

	public void getData(HttpMethod method, String url, RequestParams params,
			IResponseCallback<T> callback) {
		this.mTarget = new WeakReference<IResponseCallback<T>>(callback);
		if (params != null) {
				cacheUrl = url + "?" + URLEncodedUtils.format(params.getQueryStringParams(), "UTF-8");
		}
		UtilsLog.d("====", "url==" + cacheUrl);
		target = mTarget.get();
		if (target == null)
			return;
		http = new HttpUtils(15000);
		http.configCurrentHttpCacheExpiry(1000);
		http.send(method, url, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				if (target == null)
					return;
				try {
					JSONObject jsonResult = new JSONObject(responseInfo.result);
					// 失败
					if (jsonResult.getInt("code") != 1) {
						UtilsLog.d("====", "error==失败");
						ErrorModel errorModel = new Gson().fromJson(
								jsonResult.toString(), ErrorModel.class);
						if(errorModel.getCode()==100){
							sendBroadcast();
						}
						target.onFailure(errorModel);
					}else {
						T t = parseJson(jsonResult.getString("result"));
						if (t != null) {
							UtilsLog.d("====", "新数据=result数据==" + t);
							// 成功
							target.onSuccess(t);
						} else {
							// 解析失败
							UtilsLog.d("====", "error==解析失败=");
							ErrorModel errorModel = new ErrorModel();
							errorModel.setCode(JSONException);
							errorModel.setMsg(JSONException_MSG);
							target.onFailure(errorModel);
						}
					}
				} catch (JSONException e) {
					UtilsLog.d("====", "error==="+e.toString());
					ErrorModel errorModel = new ErrorModel();
					errorModel.setCode(JSONException);
					errorModel.setMsg(JSONException_MSG);
					target.onFailure(errorModel);
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				UtilsLog.d("====", "error==="+error+"==errormsg=="+msg);
				if (target == null)
					return;
				ErrorModel errorModel = new ErrorModel();
				errorModel.setCode(error.getExceptionCode());
				errorModel.setMsg("连接服务器超时");
				target.onFailure(errorModel);
			}

			@Override
			public void onStart() {
				super.onStart();
				if (target == null)
					return;
				target.onStart();
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				super.onLoading(total, current, isUploading);
				if (target == null)
					return;
				target.onLoading(total, current, isUploading);
			}
		});
	}

	protected abstract T parseJson(String jsonString);
	
	// 广播发送(未登录时广播)
	private void sendBroadcast() {
			Intent intent = new Intent("no_login");
			intent.putExtra("no_login", "no_login");
			context.sendBroadcast(intent);
	}
}
