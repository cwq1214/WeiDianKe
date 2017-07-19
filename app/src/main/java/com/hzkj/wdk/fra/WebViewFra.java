package com.hzkj.wdk.fra;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.HttpAuthHandler;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.UserModel;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.Utils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.hzkj.wdk.MyApplication;
import com.hzkj.wdk.R;
import com.hzkj.wdk.utils.JiaFenConstants;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class WebViewFra extends BaseFragment implements JiaFenConstants,OnClickListener{
	private MainActivity mActivity;
	private ImageView back;
	private IResponseCallback<String> cb;
	private SimpleProtocol pro;
	private UserModel userModel=null;
	private String token=null;
	private WebView webview;
	private String url,title;
	private TextView tv_title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mActivity=(MainActivity)getActivity();
		url=getArguments().getString("url");
		title=getArguments().getString("title");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.fra_webview,null);
		mActivity.FrameLayoutVisible(true);
		userModel= Utils.userModel;
		if(userModel!=null)
		token =userModel.getToken();
		initView(v);
		initData();
		return v;
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==back){
			mActivity.backFragment();
		}
	}

	private void initView(View v){
		back=(ImageView)v.findViewById(R.id.iv_left);
		back.setOnClickListener(this);
		tv_title=(TextView)v.findViewById(R.id.tv_title);
		tv_title.setText(title);
		webview = (WebView) v.findViewById(R.id.webview);
		WebSettings settings = webview.getSettings();
		settings.setSupportZoom(true);
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
		settings.setLoadWithOverviewMode(true);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存
		// settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//缓存模式
		settings.setBuiltInZoomControls(true);// 显示缩放大小
		settings.setSupportZoom(true);// 设置可以缩放
		// /////
		settings.setDatabaseEnabled(true);
		String dir = mActivity.getApplicationContext()
				.getDir("database", Context.MODE_PRIVATE).getPath();
		settings.setGeolocationEnabled(true);
		settings.setGeolocationDatabasePath(dir);
		settings.setDomStorageEnabled(true);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(false);
		// ///
		webview.setInitialScale(100);// 默认缩放大小
		//webview.setWebViewClient(new MWebViewClient());
		webview.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
									 JsResult result) {
				// TODO Auto-generated method stub
				return onJsConfirm(view, url, message, result);
			}

			/**
			 * 处理confirm弹出框
			 */
			@Override
			public boolean onJsConfirm(WebView view, String url,
									   String message, JsResult result) {
				Utils.toastShow(mActivity, message);
				result.confirm();
				return true;
			}

		});
		webview.setWebViewClient(new MWebViewClient());
		webview.loadUrl(url);
	}

	private void initData(){
		pro=new SimpleProtocol(mActivity);
		cb=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				LoadingD.hideDialog();
			}

			@Override
			public void onFailure(ErrorModel errorModel) {
				LoadingD.hideDialog();
			}

			@Override
			public void onStart() {
				LoadingD.showDialog(mActivity);
			}
		};
	}

	private void getData(){
		RequestParams params = new RequestParams();
		Map<String, String> map = new HashMap<String, String>();
		map = Utils.getParamsMap(map);

		params = Utils.addParams(params);
		String time = System.currentTimeMillis()+"";
		//String pwd = CipherUtil.generateMD5(et_pwd.getText().toString());
		params.addQueryStringParameter("token", token);
		params.addQueryStringParameter("method", "3d.user_center.editPayPassword.post");
		params.addQueryStringParameter("timestamp", time);
		map.put("token", token);
		map.put("method", "3d.user_center.editPayPassword.post");
		map.put("timestamp",time);
		//String sign = Utils.getMd5(map);
		//params.addQueryStringParameter("sign",sign);
		pro.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, cb);
	}

	// 在当前Web显示视图
	private class MWebViewClient extends WebViewClient {
		// 打开连接前事件
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		// 载人页面开始事件
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
		}

		// 载人页面完成事件
		@Override
		public void onPageFinished(WebView view, String url) {
			// LoadingD.hideDialog();
			super.onPageFinished(view, url);
		}

		// 接收到http请求的事件
		@Override
		public void onReceivedHttpAuthRequest(WebView view,
											  HttpAuthHandler handler, String host, String realm) {
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
		}
	}

}
