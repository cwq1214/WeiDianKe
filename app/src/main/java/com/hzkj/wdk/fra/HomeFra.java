package com.hzkj.wdk.fra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.model.ConfigModel;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.FunctionModel;
import com.hzkj.wdk.model.UserInfo;
import com.hzkj.wdk.model.UserModel;
import com.hzkj.wdk.utils.DensityUtil;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.SharePreferenceUtil;
import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.widget.CustomDialog;
import com.hzkj.wdk.widget.DialogSignText;
import com.hzkj.wdk.widget.HorizontalListView;
import com.hzkj.wdk.widget.MarqueeTextView;
import com.hzkj.wdk.widget.PWLogin;
import com.hzkj.wdk.widget.ShareDialog;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.hzkj.wdk.MyApplication;
import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.R;
import com.hzkj.wdk.model.MobileModel;
import com.hzkj.wdk.model.VipModel;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.UtilsLog;
import com.hzkj.wdk.widget.PWRecharge;
import com.hzkj.wdk.widget.PWSign;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *快速加粉
 */
public class HomeFra extends BaseFragment implements JiaFenConstants,OnClickListener
		,SwipeRefreshLayout.OnRefreshListener{
	private MainActivity mActivity;
	private IResponseCallback<String> cbChange,cbAdd,cbInfo,cbSign,cbConfig;
	private SimpleProtocol proChange,proAdd,proInfo,proSign,proConfig;
	private UserModel userModel=null;
	private ConfigModel configModel=null;
	private String token=null;
	private ImageView iv_refresh,img;
	private TextView tv_phone,tv_status,tv_jiafen1,tv_jiafen2,
			tv_jiafen3,tv_jiafen4,tv_num1,tv_num2,tv_balance,tv_zhifubao,tv_shouru,tv_jilu
			,tv_prompt1,tv_prompt2,tv_prompt3,tv_prompt4,tv_login,tv_prompt5,
			tv_prompt6,tv_prompt7,tv_prompt8,tv_open_prompt,tv_invite_prompt;
	private LinearLayout ll_invite1;
	private LinearLayout ll_open,ll_shouru,ll_invite,ll_jiafen,ll_beijia,ll_shaixuan,ll_clear,ll_qiandao,ll_baofen;
	private SharePreferenceUtil spu;
	private static SQLiteDatabase sqLite;
	private CustomDialog cd,cd1,cd2,cd3,cd4,cd5,cd6;
	private AddTask addTask;
	private SelectTask selectTask;
	private ClearTask clearTask;
	private List<MobileModel> lm=new ArrayList<>();
	private PWRecharge pw;
	private SwipeRefreshLayout swipe_container;
	private PWLogin pwLogin;
	//private HorizontalListView listview_h;
	private HAdapter adapterh;
	private DialogSignText dialogSign;
	private GridView gridView;
	private List<FunctionModel> listData=new ArrayList<>();

	private String marqueeText;//跑马灯文字
	private String marqueeLink;//跑马灯链接
	private MarqueeTextView marquee;
//	private TextView marquee;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mActivity=(MainActivity)getActivity();
		pwLogin=new PWLogin(mActivity,(MyApplication)mActivity.getApplication());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.fragment_home,null);
		Utils.initV(mActivity);
		userModel=Utils.userModel;
		if(userModel!=null)
			token =userModel.getToken();
		spu=new SharePreferenceUtil(mActivity);
		mActivity.FrameLayoutVisible(false);

		initView(v);
		initData();
		getConfigData();
		if(TextUtils.isEmpty(token))
			token=spu.getToken();
		if(!TextUtils.isEmpty(token)) {
			registerPush();
			getData();
		}
		registerBoradcastReceiver();
		return v;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ViewGroup.MarginLayoutParams margin1 = new ViewGroup.MarginLayoutParams(
				marquee.getLayoutParams());
		margin1.setMargins(0, 0, 0, 0);//设置滚动区域位置：在左边距400像素，顶边距10像素的位置
		LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(margin1);
		layoutParams1.height = DensityUtil.dip2px(getContext(),36);//设滚动区域高度
		layoutParams1.width = LinearLayout.LayoutParams.MATCH_PARENT; //设置滚动区域宽度
		marquee.setLayoutParams(layoutParams1);
		marquee.setScrollWidth(1000);
		marquee.setCurrentPosition(100 + 1000);//设置滚动信息从滚动区域的右边出来
		marquee.setSpeed(2);
		marquee.setTextColor(Color.RED);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onClick(final View v) {
		// TODO Auto-generated method stub
		if(TextUtils.isEmpty(spu.getLoginName())) {
			pwLogin.showPW(img);
			return;
		}
		if(v==tv_status){//加粉中／已暂停
			if(v.getTag().toString().equals("0")){
				Utils.toastShow(mActivity,"激活成功");
				v.setTag("1");
				tv_status.setText("加粉中");
				requestChange("1");
			}else if(v.getTag().toString().equals("1")){
				final CustomDialog cd=new CustomDialog(mActivity,"","","","");
				cd.setCancelOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						cd.cancel();
						tv_status.setText("已暂停");
						v.setTag("1");
						requestChange("0");
					}
				});
				cd.setConfirmOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						v.setTag("0");
						cd.cancel();
						tv_status.setText("加粉中");
						requestChange("1");
					}
				});
				cd.show();
			}
		}else if (v==ll_qiandao) {//签到
			Calendar localCalendar = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("dd");
			Date paramDate=localCalendar.getTime();
			if(Math.abs(Integer.valueOf(format.format(paramDate))-spu.getForumDay())>0&&
					v.getTag().toString().equals("0")){
				//if(v.getTag().toString().equals("0")) {
					if(!spu.getSignVersion()) {
						dialogSign=new DialogSignText(mActivity);
						dialogSign.setOkclick(new DialogSignText.OkClick() {
							@Override
							public void onClick(int b) {
								sign(true);
							}
						});
						dialogSign.show();
					}else{//验证过后,不再弹出输入验证码框
						sign(true);
					}
				//}
			}else{
				sign(false);
			}
		}else if(v==tv_jiafen1||v==ll_jiafen){//一键加粉
			requestDataJiaFen();
		}else if(v==tv_jiafen2||v==ll_beijia){//被动加粉
			if(userModel.getUser_lv().equals("2")){
				//Utils.toastShow(mActivity,"被动加粉中");
				cd5.show();
			}else{
				cd2.show();
			}
		}else if(v==tv_jiafen3||v==ll_shaixuan){//筛选加粉
			if(userModel.getUser_lv().equals("1")||userModel.getUser_lv().equals("2")){
				mActivity.changeFragment(new AddFilterFra(), false);
			}else{
				cd3.show();
			}
		}else if(v==tv_jiafen4||v==ll_clear){//清理通讯录
			cd1.show();
		}else if(v==ll_invite1){//邀请好友

			ShareDialog shareDialog = new ShareDialog(mActivity);
			shareDialog.show();
//			mActivity.openUmengShare("http://qb1611.cn","被动加粉，好友轻松满5千！","免费下载.免费使用.被动加粉坐等被加为好友！快速建立5千好友朋友圈！","");
		}else if(v==ll_open){//开通vip
			mActivity.changeFragment(new MemberTypeFra(),false);
//			pw=new PWRecharge(mActivity,(MyApplication)mActivity.getApplication());
//			pw.setSuccessClick(new PWRecharge.OnSuccess() {
//				@Override
//				public void onClick(String type, String name, String price, String sn) {
//					pay1(type, name, price, sn);
//				}
//			});
//			pw.showPW(v);
		}else if(v==ll_shouru){//收入明细
			mActivity.changeFragment(new InComeDetailFra(),false);
		}else if(v==ll_invite){//邀请纪录
			mActivity.changeFragment(new InviteRecordFra(),false);
		}else if(v==tv_zhifubao){//支付宝提现
			TiXianFra tf=new TiXianFra();
			Bundle b=new Bundle();
			b.putString("price",userModel.getRemains());
			tf.setArguments(b);
			mActivity.changeFragment(tf,false);
		}else if(v==iv_refresh){//刷新
			if(!TextUtils.isEmpty(token)) {
				getData();
			}else{

			}
			getConfigData();
		}else if(v==img){//
			if(TextUtils.isEmpty(Utils.userModel.getBanner_url()))
				return;
			mActivity.changeFragment(new Home2Fra(),false);
//			Uri uri = Uri.parse(""+Utils.userModel.getBanner_url());
//			Intent it = new Intent(Intent.ACTION_VIEW, uri);
//			mActivity.startActivity(it);
		}else if(v==tv_login){
			if(tv_login.getTag().toString().equals("1")){
				cd6.show();
			}
		}else if(v==ll_baofen){//爆粉
			WebViewFra wf=new WebViewFra();
			Bundle b=new Bundle();
			b.putString("url",Utils.userModel.getWeichat_contacts_url());
			b.putString("title","挂机爆粉");
			wf.setArguments(b);
			mActivity.changeFragment(wf,false);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}
		}
	}

	private void initView(View v){
		marquee = (MarqueeTextView) v.findViewById(R.id.marquee);
//		marquee = (TextView) v.findViewById(R.id.marquee);
		iv_refresh=(ImageView)v.findViewById(R.id.iv_refresh);
		iv_refresh.setOnClickListener(this);
		tv_phone=(TextView)v.findViewById(R.id.tv_phone);
		tv_status=(TextView)v.findViewById(R.id.tv_status);
		tv_status.setOnClickListener(this);
//		tv_jiafen1=(TextView)v.findViewById(R.id.tv_jiafen1);
//		tv_jiafen1.setOnClickListener(this);
//		tv_jiafen2=(TextView)v.findViewById(R.id.tv_jiafen2);
//		tv_jiafen2.setOnClickListener(this);
//		tv_jiafen3=(TextView)v.findViewById(R.id.tv_jiafen3);
//		tv_jiafen3.setOnClickListener(this);
//		tv_jiafen4=(TextView)v.findViewById(R.id.tv_jiafen4);
//		tv_jiafen4.setOnClickListener(this);
		tv_num1=(TextView)v.findViewById(R.id.tv_num1);
		tv_num2=(TextView)v.findViewById(R.id.tv_num2);
		tv_balance=(TextView)v.findViewById(R.id.tv_balance);
		tv_zhifubao=(TextView)v.findViewById(R.id.tv_zhifubao);
		tv_zhifubao.setOnClickListener(this);
		ll_invite1=(LinearLayout) v.findViewById(R.id.ll_invite1);
		ll_invite1.setOnClickListener(this);
		ll_open=(LinearLayout)v.findViewById(R.id.ll_open);
		ll_open.setOnClickListener(this);
		ll_shouru=(LinearLayout)v.findViewById(R.id.ll_shouru);
		ll_shouru.setOnClickListener(this);
		ll_invite=(LinearLayout)v.findViewById(R.id.ll_invite);
		ll_invite.setOnClickListener(this);
		tv_jilu=(TextView)v.findViewById(R.id.tv_jilu);
		tv_shouru=(TextView)v.findViewById(R.id.tv_shouru);
		img=(ImageView)v.findViewById(R.id.img);
		img.setOnClickListener(this);
		tv_prompt1=(TextView)v.findViewById(R.id.tv_prompt1);
		tv_prompt2=(TextView)v.findViewById(R.id.tv_prompt2);
		tv_prompt3=(TextView)v.findViewById(R.id.tv_prompt3);
		tv_prompt4=(TextView)v.findViewById(R.id.tv_prompt4);
		tv_prompt5=(TextView)v.findViewById(R.id.tv_prompt5);
		tv_prompt6=(TextView)v.findViewById(R.id.tv_prompt6);
		tv_prompt7=(TextView)v.findViewById(R.id.tv_prompt7);
		tv_prompt8=(TextView)v.findViewById(R.id.tv_prompt8);
		tv_login=(TextView)v.findViewById(R.id.tv_login);
		gridView=(GridView)v.findViewById(R.id.gridview);
		tv_login.setOnClickListener(this);
		swipe_container=(SwipeRefreshLayout)v.findViewById(R.id.swipe_container);
		swipe_container.setOnRefreshListener(this);
		swipe_container.setColorSchemeResources(android.R.color.holo_orange_light,
				android.R.color.holo_orange_dark,
				android.R.color.holo_red_light);
//		ll_jiafen=(LinearLayout)v.findViewById(R.id.ll_jiafen);
//		ll_jiafen.setOnClickListener(this);
//		ll_beijia=(LinearLayout)v.findViewById(R.id.ll_beijia);
//		ll_beijia.setOnClickListener(this);
//		ll_shaixuan=(LinearLayout)v.findViewById(R.id.ll_shaixuan);
//		ll_shaixuan.setOnClickListener(this);
//		ll_clear=(LinearLayout)v.findViewById(R.id.ll_clear);
//		ll_clear.setOnClickListener(this);
//		ll_qiandao=(LinearLayout)v.findViewById(R.id.ll_qiandao);
//		ll_qiandao.setOnClickListener(this);
//		ll_baofen=(LinearLayout)v.findViewById(R.id.ll_baofen);
//		ll_baofen.setOnClickListener(this);
		tv_invite_prompt=(TextView)v.findViewById(R.id.tv_invite_prompt);
		tv_open_prompt=(TextView)v.findViewById(R.id.tv_open_prompt);
		tv_prompt1.setText(Html.fromHtml(String.format("1、每邀请一个好友，奖励<font color='#E2333C' >%s</font>个客源名", "100")));
		tv_prompt2.setText(Html.fromHtml(String.format("2、每邀请一个好友，奖励<font color='#E2333C' >%s</font>元", "0.2")));
		tv_prompt3.setText(Html.fromHtml(String.format("3、每邀请一个好友，奖励幸运码<font color='#E2333C' >%s</font>个", "1")));
		tv_prompt4.setText(Html.fromHtml(String.format("4、每邀请一个好友是会员，奖励<font color='#E2333C' >%s</font>元", "1")));
		tv_prompt5.setText(Html.fromHtml(String.format("5、每邀请<font color='#E2333C' >%s</font>个好友，奖励黄金会员<font color='#E2333C' >%s</font>天", "4","1")));
		tv_prompt6.setText(Html.fromHtml(String.format("6、每邀请<font color='#E2333C' >%s</font>个好友，奖励钻石会员<font color='#E2333C' >%s</font>天", "10","1")));
		tv_prompt7.setText(Html.fromHtml(String.format("7、连续签到<font color='#E2333C' >%s</font>天，奖励<font color='#E2333C' >%s</font>名客源", "5","300")));
		tv_prompt8.setText(Html.fromHtml(String.format("8、连续签到<font color='#E2333C' >%s</font>天，奖励钻石会员<font color='#E2333C' >%s</font>天", "30","1")));
		//tv_prompt4.setText(Html.fromHtml(String.format("名额奖励  每邀请一个好友，增加<font color='#E2333C' >%s</font>个加粉名额", "100")));
		//listview_h=(HorizontalListView)v.findViewById(R.id.listview_h);
		adapterh=new HAdapter();
		//listview_h.setAdapter(adapterh);
		gridView.setAdapter(adapterh);
		initDialog();

		if(userModel!=null) {
			setData();
			getUserData();
		}

		initLoginName();

	}

	private void getData(){
		if(Utils.userModel!=null)
		token =Utils.userModel.getToken();
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r", "api/user/getUserInfo");
		params.addQueryStringParameter("token", token);
		proInfo.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, cbInfo);
	}

	private void getSign(){
		if(Utils.userModel!=null)
		token =Utils.userModel.getToken();
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r", "api/user/sign");
		params.addQueryStringParameter("token", token);
		proSign.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, cbSign);
	}

	private void initData(){
		proChange=new SimpleProtocol(mActivity);
		cbChange=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				LoadingD.hideDialog();
			}

			@Override
			public void onFailure(ErrorModel errorModel) {
				LoadingD.hideDialog();
				if(errorModel.getMsg().contains("登录失")){
					spu.setLoginName("");
				}
			}

			@Override
			public void onStart() {
				LoadingD.hideDialog();
				LoadingD.showDialog(mActivity);
			}
		};

		proAdd=new SimpleProtocol(mActivity);
		cbAdd=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				LoadingD.hideDialog();
				lm.clear();
				try{
					JSONObject obj=new JSONObject(s);
					JSONArray array=obj.getJSONArray("items");
					for(int i=0;i<array.length();i++){
						MobileModel mm=new MobileModel();
						mm.setMobileNumber(array.getJSONObject(i).getString("phone"));
						lm.add(mm);
					}
				}catch (Exception e){

				}
				if(lm.size()>0) {
					initDialog();
					cd.show();
					UtilsLog.d("====","="+lm.size());
				}else{
					Utils.toastShow(mActivity,"暂时没有加粉数据");
				}
			}

			@Override
			public void onFailure(ErrorModel errorModel) {
				LoadingD.hideDialog();
				if(errorModel.getMsg().contains("登录失败")){
					spu.setLoginName("");
				}
			}

			@Override
			public void onStart() {
				LoadingD.hideDialog();
				LoadingD.showDialog(mActivity);
			}
		};

		proInfo=new SimpleProtocol(mActivity);
		cbInfo=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				LoadingD.hideDialog();
				System.out.println(s);
				swipe_container.setRefreshing(false);
				try {
					JSONObject obj=new JSONObject(s);
					userModel=new Gson().fromJson(obj.toString(),UserModel.class);
					userModel.setListViPs(new ArrayList<VipModel>());
					spu.setToken(userModel.getToken());
					spu.setHome2Url(userModel.getWeichat_contacts_url());
					JSONArray array=obj.getJSONArray("vip_types");
					for(int i=0;i<array.length();i++){
						VipModel vm=new Gson().fromJson(array.getString(i),VipModel.class);
						userModel.getListViPs().add(vm);
					}
					Utils.userModel=userModel;
					Intent mIntent = new Intent(LOAD_BAOFEN);
					mActivity.sendBroadcast(mIntent);
//					if (XGPushManager.enableService!=XGPushManager.OPERATION_SUCCESS) {
//						registerPush();
//					}
				}catch (Exception e){

				}
				if(userModel!=null)
					setData();
			}

			@Override
			public void onFailure(ErrorModel errorModel) {
				LoadingD.hideDialog();
				UtilsLog.d("====","失败＝＝＝"+errorModel.getMsg());
				if(errorModel.getMsg().contains("登录失")){
					spu.setLoginName("");
				}
				swipe_container.setRefreshing(false);
			}

			@Override
			public void onStart() {
				//LoadingD.hideDialog();
				//LoadingD.showDialog(mActivity);
				swipe_container.setRefreshing(true);
			}
		};
		proSign=new SimpleProtocol(mActivity);
		cbSign=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				Calendar localCalendar = Calendar.getInstance();
				SimpleDateFormat format = new SimpleDateFormat("dd");
				Date paramDate=localCalendar.getTime();
				spu.setForumDay(Integer.valueOf(format.format(paramDate)));
				//tv_sign.setText("已签到");
				listData.get(4).setFunction_name("已签到");
				adapterh.notifyDataSetChanged();
				Utils.toastShow(mActivity, "签到成功！");
				try {
					JSONObject obj=new JSONObject(s);
					String money=obj.getString("bonus_money");
					String day=obj.getString("has_signed_days");
					spu.setHomeSignDays(day);
					spu.setHomeSignMoney(money);
				}catch (Exception e){

				}
			}

			@Override
			public void onFailure(ErrorModel errorModel) {
				if(errorModel.getMsg().contains("登录失")){
					//spu.setLoginName("");
				}
			}

			@Override
			public void onStart() {

			}
		};

		proConfig=new SimpleProtocol(mActivity);
		cbConfig=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				swipe_container.setRefreshing(false);
				initLocalData();
				try {
					JSONObject obj=new JSONObject(s);
					configModel=new Gson().fromJson(obj.toString(),ConfigModel.class);
					configModel.setListViPs(new ArrayList<VipModel>());
					configModel.setListFunction(new ArrayList<FunctionModel>());
					JSONArray array=obj.getJSONArray("vip_types_new");
					for(int i=0;i<array.length();i++){
						VipModel vm=new Gson().fromJson(array.getString(i),VipModel.class);
						configModel.getListViPs().add(vm);
					}
					JSONArray array1=obj.getJSONArray("extension_functions");
					for(int i=0;i<array1.length();i++){
						FunctionModel fm=new Gson().fromJson(array1.getString(i),FunctionModel.class);
						fm.setWeb(true);
						configModel.getListFunction().add(fm);
						listData.add(fm);
					}
					Utils.configModel=configModel;

					JSONObject marquee = obj.getJSONObject("marquee");
					marqueeText = marquee.getString("content");
					marqueeLink = marquee.getString("link");
					HomeFra.this.marquee.setText(marqueeText);
					HomeFra.this.marquee.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							if (!TextUtils.isEmpty(marqueeLink)){
//								Intent intent = new Intent(Intent.ACTION_VIEW);
//								intent.setData(Uri.parse(marqueeLink));
//								startActivity(intent);
								BrowserFragment browserFragment = new BrowserFragment();
								browserFragment.url = marqueeLink;
								mActivity.changeFragment(browserFragment,false);
							}
						}
					});

					String member_button = obj.getString("member_button");
					String qrcode_button = obj.getString("qrcode_button");

					spu.setPersonalCenterBtnName(member_button);
					spu.setQRCodeBtnName(qrcode_button);

//					listData.get(3).setFunction_name(member_button);
//					adapterh.notifyDataSetChanged();
					mActivity.reloadNameTv4();
					mActivity.reloadNameTV1();

				}catch (Exception e){

				}
				if(configModel!=null)
					stConfig();
			}

			@Override
			public void onFailure(ErrorModel errorModel) {
				swipe_container.setRefreshing(false);
				initLocalData();
			}

			@Override
			public void onStart() {
				swipe_container.setRefreshing(true);
			}
		};
	}

	private void setData(){
		if(userModel==null)return;
		UtilsLog.d("====", "====setdata＝＝＝");
		tv_num1.setText(TextUtils.isEmpty(userModel.getAvailable_fans_count())?"0":userModel.getAvailable_fans_count());
		tv_shouru.setText(TextUtils.isEmpty(userModel.getIncomes())?"0":userModel.getIncomes());
		tv_jilu.setText(TextUtils.isEmpty(userModel.getInvites())?"0":userModel.getInvites());
		tv_balance.setText(TextUtils.isEmpty(userModel.getRemains())?"0":userModel.getRemains());
		tv_status.setText("已暂停");
		if(spu.getLoginName()!=null&&spu.getLoginName().length()==11) {
			String start = spu.getLoginName().substring(0,3);
			String end = spu.getLoginName().substring(7);
			tv_phone.setText(start+"****"+end);
		}
		initLoginName();
	}

	private void stConfig(){
//		if(configModel.getListFunction()!=null){
			adapterh.notifyDataSetChanged();
//		}
		bitmapUtilsBase.display(img,configModel.getBaner_image());
		//tv_jiafen1.setText(""+configModel.getAdd_fans_name());
		//tv_jiafen2.setText(""+configModel.getBe_added_fans_name());
		listData.get(0).setFunction_name(configModel.getAdd_fans_name());
		listData.get(1).setFunction_name(configModel.getBe_added_fans_name());
		tv_open_prompt.setText(configModel.getPayvip_des());
		tv_invite_prompt.setText(configModel.getInvite_des());

		tv_prompt5.setText(Html.fromHtml(String.format("5、每邀请<font color='#E2333C' >%s</font>个好友，奖励黄金会员<font color='#E2333C' >%s</font>天", ""+configModel.getVipbonus_invites_count(),"1")));
		tv_prompt6.setText(Html.fromHtml(String.format("6、每邀请<font color='#E2333C' >%s</font>个好友，奖励钻石会员<font color='#E2333C' >%s</font>天", ""+configModel.getSupervipbonus_invites_count(),"1")));
	}

	private void initDialog(){
		cd=new CustomDialog(mActivity,"已添加"+lm.size()+"个好友","打开社交软件.绑定这个手机号的社交软件账号.点击通讯录.即可看到新的好友！","","确定");
		cd.setCancelOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cd.cancel();
			}
		});
		cd.setConfirmOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(addTask!=null&&!addTask.isCancelled()){
					addTask.cancel(true);
					addTask=null;
				}
				addTask=new AddTask();
				addTask.execute();
				cd.cancel();
				LoadingD.showDialog(mActivity);
			}
		});

		cd1=new CustomDialog(mActivity,"提示","确定删除联系人吗","取消","确定");
		cd1.setCancelOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cd1.cancel();
			}
		});
		cd1.setConfirmOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(clearTask!=null&&!clearTask.isCancelled()){
					clearTask.cancel(true);
					clearTask=null;
				}
				clearTask=new ClearTask();
				clearTask.execute();
				cd1.cancel();
				LoadingD.showDialog(mActivity);
			}
		});

		cd2=new CustomDialog(mActivity,"温馨提示","被动加粉功能仅限钻石会员使用，请先开超级钻石会员","暂不","开通");
		cd2.setCancelOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cd2.cancel();
			}
		});
		cd2.setConfirmOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cd2.cancel();
				mActivity.changeFragment(new MemberTypeFra(),false);
//				pw=new PWRecharge(mActivity,(MyApplication)mActivity.getApplication());
//				pw.setSuccessClick(new PWRecharge.OnSuccess() {
//					@Override
//					public void onClick(String type, String name, String price, String sn) {
//						pay1(type, name, price, sn);
//					}
//				});
//				pw.showPW(tv_status);
			}
		});

		cd3=new CustomDialog(mActivity,"温馨提示","筛选加粉功能仅限黄金会员和钻石会员使用，请先开通会员","暂不","开通");
		cd3.setCancelOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cd3.cancel();
			}
		});
		cd3.setConfirmOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cd3.cancel();
				mActivity.changeFragment(new MemberTypeFra(),false);
//				pw=new PWRecharge(mActivity,(MyApplication)mActivity.getApplication());
//				pw.setSuccessClick(new PWRecharge.OnSuccess() {
//					@Override
//					public void onClick(String type, String name, String price, String sn) {
//						pay1(type, name, price, sn);
//					}
//				});
//				pw.showPW(tv_status);
			}
		});
		cd4=new CustomDialog(mActivity,"已添加"+lm.size()+"个好友","打开v信.绑定这个手机号的v信号.点击通讯录.点新的朋友.即可看到新添加的好友","","确定");
		cd4.setCancelOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cd4.cancel();
			}
		});
		cd4.setConfirmOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cd4.cancel();
			}
		});

		cd5=new CustomDialog(mActivity,"温馨提示","被动加粉中！24小时内会有新的好友添加您！点击v信通讯录.点击新的朋友查询即可!","","确定");
		cd5.setCancelOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cd5.cancel();
			}
		});
		cd5.setConfirmOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cd5.cancel();
			}
		});

		cd6=new CustomDialog(mActivity,"提示","确定退出吗?","取消","确定");
		cd6.setCancelOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cd6.cancel();
			}
		});
		cd6.setConfirmOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				spu.setLoginName("");
				tv_login.setTag("0");
				tv_phone.setText("");
				tv_status.setText("");
				initLoginName();
				cd6.cancel();
			}
		});
	}

	private void getUserData(){
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r", "api/qrcode/getUserInfo");
		params.addQueryStringParameter("token", "" + spu.getToken());
		new SimpleProtocol(mActivity).getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				LoadingD.hideDialog();
				System.out.println(s);
				UserInfo baseJson = new Gson().fromJson(s, new TypeToken<UserInfo>() {
				}.getType());


				spu.setAvatar(baseJson.userImg);
				spu.setStatus(baseJson.nickname);
				spu.setShareLink(baseJson.share_link);
			}

			@Override
			public void onFailure(ErrorModel errorModel) {
				LoadingD.hideDialog();
				System.out.println(errorModel.getMsg());
			}

			@Override
			public void onStart() {
				LoadingD.hideDialog();
				LoadingD.showDialog(mActivity);
			}
		});
	}


	@Override
	public void onRefresh() {
		if(!TextUtils.isEmpty(token)) {
			getData();
		}
		getConfigData();
	}
	//添加到通讯录
	private class AddTask extends AsyncTask<String,String,String> {
		@Override
		protected String doInBackground(String... strings) {
			for(int i=0;i<lm.size();i++){
				Utils.AddContacts(mActivity,lm.get(i).getMobileNumber());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			LoadingD.hideDialog();

			//cd4.show();
		}
	}
	//根据城市查询
	private class SelectTask extends AsyncTask<ListView, String, List<MobileModel>> {

		@Override
		protected List<MobileModel> doInBackground(ListView... params) {
			// TODO Auto-generated method stub
			sqLite= SQLiteDatabase.openOrCreateDatabase(DBPATH, null);
			lm.clear();
			lm.addAll(Utils.queryData(sqLite,10));
			return lm;
		}

		@Override
		protected void onPostExecute(List<MobileModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			LoadingD.hideDialog();
			cd.show();
			//new AddTask().execute();
		}
	}
	//清除通讯录
	private class ClearTask extends AsyncTask<String,String,String> {
		@Override
		protected String doInBackground(String... strings) {
			Utils.deleteData3(mActivity);
			return null;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			LoadingD.hideDialog();
		}
	}
	//获取首页数据
	private void getConfigData(){
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r", "api/common/getappsetting");
		params.addQueryStringParameter("token", "" +spu.getToken() );
		proConfig.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, cbConfig);
	}
	//一键加粉
	private void requestDataJiaFen(){
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r", "api/user/jiafen");
		params.addQueryStringParameter("token", "" +spu.getToken() );
		proAdd.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, cbAdd);
	}

	private void requestChange(String str){
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r", "api/user/jiafen");
		params.addQueryStringParameter("token", "" + spu.getToken());
		params.addQueryStringParameter("is_fansing", str);
		proChange.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, cbChange);
	}

	private void pay1(String payType,String name,String price,String sn){
		Bundle b=new Bundle();
		b.putString("paySn", sn);
		b.putString("name", "" + name);
		b.putString("price", "" + price);
		if(payType.equals("1")){//支付宝
			ExternalFragment fra=new ExternalFragment();
			fra.setArguments(b);
			fra.setData(name, price, sn,mActivity,false);
			fra.pay();
			//mActivity.changeFragment(fra, false);
		}else{//v信支付
			WeiXinPayFra fra=new WeiXinPayFra();
			fra.setArguments(b);
			fra.setData(name,price,sn,mActivity,false);
			fra.pay();
			//mActivity.changeFragment(fra, false);
		}
	}

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(action_refresh);
		myIntentFilter.addAction(GET_DATA);
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
			if (action.equals(action_refresh)) {
				userModel=Utils.userModel;
				setData();
				registerPush();
			}else if(action.equals(GET_DATA)){
				if(!TextUtils.isEmpty(token)) {
					getData();
				}
				getConfigData();
				registerPush();
			}
		}
	};

	public void registerPush() {
		// 1.获取设备Token
		XGPushManager.registerPush(mActivity, token
				, new XGIOperateCallback() {

			@Override
			public void onSuccess(Object data, int arg1) {
				// TODO Auto-generated method stub
				Log.d("====", "信鸽注册成功，设备token为：" + data);
			}

			@Override
			public void onFail(Object data, int arg1, String arg2) {
				// TODO Auto-generated method stub

				Log.d("====", "信鸽注册失败，设备token为：" + data);
			}
		});
	}

	private class ViewHolderH{
		ImageView iv;
		TextView tv;
		RelativeLayout rl_content;
	}
	//分类
	private class HAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listData.size();
		}

		@Override
		public Object getItem(int i) {
			return listData.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View v, ViewGroup viewGroup) {
			ViewHolderH holder;
			if(v==null){
				holder=new ViewHolderH();
				v=View.inflate(mActivity,R.layout.item_home_category,null);
				holder.iv=(ImageView)v.findViewById(R.id.iv);
				holder.tv=(TextView)v.findViewById(R.id.tv);
				holder.rl_content=(RelativeLayout)v.findViewById(R.id.rl_content);
				v.setTag(holder);
			}else{
				holder=(ViewHolderH)v.getTag();
			}
			//CategoryModel cm=listCModel.get(i);
			final FunctionModel hm=listData.get(i);
			RelativeLayout.LayoutParams rl=(RelativeLayout.LayoutParams)holder.rl_content.getLayoutParams();
			rl.width=Utils.deviceWidth/4- DensityUtil.dip2px(mActivity,10);
			holder.rl_content.setLayoutParams(rl);
			holder.tv.setText(hm.getFunction_name());
			if(hm.isWeb())
				bitmapUtilsBase.display(holder.iv,hm.getFunction_img());
			else
				holder.iv.setImageResource(Integer.valueOf(hm.getFunction_img()));
			//holder.iv.setBackground(getResources().getDrawable(Integer.valueOf(hm.getFunction_img())));
			holder.rl_content.setTag(i);
			holder.rl_content.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(TextUtils.isEmpty(spu.getLoginName())) {
						pwLogin.showPW(img);
						return;
					}
					int index=Integer.valueOf(view.getTag().toString());
					if(hm.isWeb()) {
						WebViewFra wf = new WebViewFra();
						Bundle b = new Bundle();
						b.putString("url", hm.getFunction_url());
						b.putString("title", hm.getFunction_name());
						wf.setArguments(b);
						mActivity.changeFragment(wf, false);
					}else{
						if(index==0){//一键加粉
							requestDataJiaFen();
						}else if(index==1){//一键被加
							if(userModel.getUser_lv().equals("2")){
								cd5.show();
							}else{
								cd2.show();
							}
						}else if(index==2){//高级筛选
							if(userModel.getUser_lv().equals("1")||userModel.getUser_lv().equals("2")){
								mActivity.changeFragment(new AddFilterFra(), false);
							}else{
								cd3.show();
							}
						}else if(index==3){
							mActivity.changeFragment(new PersonalCenterFra(), false);

						}else if(index==4){//清理通讯录
							 cd1.show();
						}else if (index==5){//签到
							Calendar localCalendar = Calendar.getInstance();
							SimpleDateFormat format = new SimpleDateFormat("dd");
							Date paramDate=localCalendar.getTime();
							if(Math.abs(Integer.valueOf(format.format(paramDate))-spu.getForumDay())>0&&
									gridView.getTag().toString().equals("0")){
								if(!spu.getSignVersion()) {
									dialogSign=new DialogSignText(mActivity);
									dialogSign.setOkclick(new DialogSignText.OkClick() {
										@Override
										public void onClick(int b) {
											sign(true);
										}
									});
									dialogSign.show();
								}else{//验证过后,不再弹出输入验证码框
									sign(true);
								}
							}else{
								sign(false);
							}
						}
//						else if(index==5){//挂机爆粉
//							WebViewFra wf=new WebViewFra();
//							Bundle b=new Bundle();
//							b.putString("url",Utils.userModel.getWeichat_contacts_url());
//							b.putString("title","挂机爆粉");
//							wf.setArguments(b);
//							mActivity.changeFragment(wf,false);
//						}
					}
				}
			});
			return v;
		}
	}

	public void sign(boolean b){
		if(b) {
			getSign();
		}
		PWSign pwSign=new PWSign(mActivity,spu.getHomeSignMoney(),spu.getHomeSignDays());
		pwSign.setSuccessClick(new PWSign.OnSuccess() {
			@Override
			public void onClick(String str) {
				mActivity.changeFragment(new MemberTypeFra(),false);
//				if (pw==null) {
//					pw = new PWRecharge(mActivity, (MyApplication) getContext().getApplicationContext());
//				}
//				pw.showPW(ll_open);

			}
		});
		pwSign.showPW(gridView);
	}
	//
	private void initLocalData(){
		listData.clear();
		for(int i=0;i<6;i++){
			FunctionModel fm=new FunctionModel();
			if(i==0) {
				fm.setFunction_img("" + R.drawable.home_jiafen);
				fm.setFunction_name("一键加粉T");
			}else if(i==1){
				fm.setFunction_img("" + R.drawable.home_beijia);
				fm.setFunction_name("一键被加T");
			}else if(i==2){
				fm.setFunction_img("" + R.drawable.home_shaixuan);
				fm.setFunction_name("高级筛选");
			}else if(i==3){
				fm.setFunction_img("" + R.drawable.personcenter2);
				fm.setFunction_name("个人中心");
//				fm.setFunction_img("" + R.drawable.home_qingkong);
//				fm.setFunction_name("清理通讯录");
			}else if (i==4){
				fm.setFunction_img("" + R.drawable.home_qingkong);
				fm.setFunction_name("清理通讯录");
			}else if(i==5){
				fm.setFunction_img("" + R.drawable.home_qiandao);
				fm.setFunction_name("签到奖励");
			}
//			else if(i==5){
//				fm.setFunction_img("" + R.drawable.home_baofen);
//				fm.setFunction_name("挂机爆粉");
//			}
			fm.setWeb(false);
			listData.add(fm);
		}
	}

	private void initLoginName(){
		if(TextUtils.isEmpty(spu.getLoginName())){
			tv_status.setText("");
			tv_phone.setText("");
			tv_login.setText("登陆");
		}else{
			tv_login.setText("退出");
			tv_login.setTag("1");
		}
	}

}
