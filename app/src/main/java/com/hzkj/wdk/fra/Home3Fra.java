package com.hzkj.wdk.fra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.hzkj.wdk.utils.Utils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.R;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.UserModel;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.UtilsLog;

import java.util.HashMap;
import java.util.Map;

/**
 *新手指南
 */
public class Home3Fra extends BaseFragment implements JiaFenConstants,OnClickListener
		,SwipeRefreshLayout.OnRefreshListener{
	private MainActivity mActivity;
	private ImageView back;
	private IResponseCallback<String> cb;
	private SimpleProtocol pro;
	private UserModel userModel=null;
	private String token=null;
	private WebView webview;
	private SwipeRefreshLayout swipe_container;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mActivity=(MainActivity)getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.fragment_home3,null);
		userModel= Utils.userModel;
		if(userModel!=null)
		token =userModel.getToken();
		mActivity.FrameLayoutVisible(false);
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
			//mActivity.backFragment();
		}
	}

	@Override
	public void onRefresh() {
		userModel= Utils.userModel;
		if(userModel!=null)
			token =userModel.getToken();
		if(userModel!=null)
			webview.loadUrl(userModel.getInstruction_url());
		swipe_container.setRefreshing(false);
	}

	private void initView(View v){
		swipe_container=(SwipeRefreshLayout)v.findViewById(R.id.swipe_container);
		swipe_container.setOnRefreshListener(this);
		swipe_container.setColorSchemeResources(android.R.color.holo_orange_light,
				android.R.color.holo_orange_dark,
				android.R.color.holo_red_light);
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
		// ///
		webview.setInitialScale(100);// 默认缩放大小
		//webview.setWebViewClient(new MWebViewClient());
		if(userModel!=null)
		webview.loadUrl(userModel.getInstruction_url());
		registerBoradcastReceiver();
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

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(LOAD_BAOFEN);
		// 注册广播
		mActivity.registerReceiver(mBroadcastReceiver, myIntentFilter);
	}
	/**
	 * 刷新用户信息的广播
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(LOAD_BAOFEN)) {
				userModel=Utils.userModel;
				if(userModel!=null)
					webview.loadUrl(userModel.getInstruction_url());
			}
		}
	};

}
