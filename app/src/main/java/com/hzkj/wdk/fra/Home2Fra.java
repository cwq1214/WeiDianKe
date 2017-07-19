package com.hzkj.wdk.fra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.JiangPinModel;
import com.hzkj.wdk.utils.DensityUtil;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.SharePreferenceUtil;
import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.utils.UtilsLog;
import com.hzkj.wdk.R;
import com.hzkj.wdk.widget.CustomDialog1;
import com.hzkj.wdk.widget.CustomDialog2;
import com.hzkj.wdk.widget.PWReport;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *抽奖
 */
public class Home2Fra extends BaseFragment implements JiaFenConstants,OnClickListener
		,SwipeRefreshLayout.OnRefreshListener{
	private MainActivity mActivity;
	private ImageView back,iv_ok;
	private IResponseCallback<String> cb,cb1;//剩余抽奖次数,抽奖
	private SimpleProtocol pro,pro1;//剩余抽奖次数,抽奖
	private View v;
	private SharePreferenceUtil spu;
	private GridView gridview;
	private LinearLayout ll_guize,ll_wode,ll_jiangpin,ll_share;
	private TextView tv_xingyunma;
	private ListView listview;
	private BaseAdapter adapter,adapter1;
	private List<JiangPinModel> listData=new ArrayList<>();
	private List<String> list=new ArrayList<>();
	private Timer timer,timer1,timer2,timer3,timer4;
	private int next=0,index=-1,count=0,indexP,index1=0;//递加,移动下标,剩余抽奖次数,中奖下标,奖品滚动下标
	private List<Integer> listInt=new ArrayList<>();//本地奖品数据码
	private List<String>listServer=new ArrayList<>();//服务器奖品数据码
	private CustomDialog1 dialog1;
	private CustomDialog2 dialog2;
	private String jiangPin="",jianPinName;
	private SwipeRefreshLayout swipe_container;
	private boolean isBusy=false;

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
		v=inflater.inflate(R.layout.fragment_home2,null);
		mActivity.FrameLayoutVisible(true);
		spu=new SharePreferenceUtil(mActivity);
		listInt.clear();
		list.clear();
		listData.clear();
		listServer.clear();
		initView(v);
		initData();
		getData();
		registerBoradcastReceiver();
		initTimer4();
		return v;
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==back){
			mActivity.backFragment();
		}else if(v==iv_ok){//抽奖
			if(count>0) {
				iv_ok.setEnabled(false);
				getData1();
			}else{
				Utils.toastShowLong(mActivity,"没有幸运码了,分享或者购买来获取幸运码");
				initTimer3();
			}
		}else if(v==ll_wode){//抽奖纪录
			mActivity.changeFragment(new MyJiangPinFra(),false);
		}else if(v==ll_jiangpin){//奖品列表
			mActivity.changeFragment(new JiangPinListFra(),false);
		}else if(v==ll_guize){//规则
			WebViewFra fra=new WebViewFra();
			Bundle b=new Bundle();
			b.putString("url","");
			b.putString("title","抽奖规则");
			fra.setArguments(b);
			mActivity.changeFragment(fra,false);
		}else if(v==ll_share){
			PWReport pw=new PWReport(mActivity);
			pw.setOkClick(new PWReport.OKClick() {
				@Override
				public void okClick(String path, int type) {
					if(type==1){//分享
						mActivity.openUmengShare("","","","");
					}else if(type==2){//购买
						mActivity.changeFragment(new FraLuckCode(),false);
					}
				}
			});
			pw.ShowPop(v);
		}
	}

	@Override
	public void onRefresh() {
		if(!isBusy){
			getData();
		}
	}

	private void initView(View v){
		gridview=(GridView)v.findViewById(R.id.gridview);
		RelativeLayout.LayoutParams rl=(RelativeLayout.LayoutParams)gridview.getLayoutParams();
		rl.height=Utils.deviceWidth- DensityUtil.dip2px(mActivity,80);
		back=(ImageView)v.findViewById(R.id.iv_left);
		back.setOnClickListener(this);
		gridview.setLayoutParams(rl);
		iv_ok=(ImageView)v.findViewById(R.id.iv_ok);
		iv_ok.setOnClickListener(this);
		ll_share=(LinearLayout) v.findViewById(R.id.ll_share);
		ll_share.setOnClickListener(this);
		ll_guize=(LinearLayout)v.findViewById(R.id.ll_guize);
		ll_guize.setOnClickListener(this);
		ll_jiangpin=(LinearLayout)v.findViewById(R.id.ll_jiangpin);
		ll_jiangpin.setOnClickListener(this);
		ll_wode=(LinearLayout)v.findViewById(R.id.ll_wode);
		ll_wode.setOnClickListener(this);
		tv_xingyunma=(TextView)v.findViewById(R.id.tv_xingyunma);
		swipe_container=(SwipeRefreshLayout)v.findViewById(R.id.swipe_container);
		swipe_container.setOnRefreshListener(this);
		swipe_container.setColorSchemeResources(android.R.color.holo_orange_light,
				android.R.color.holo_orange_dark,
				android.R.color.holo_red_light);
		listview=(ListView)v.findViewById(R.id.listview);
		LinearLayout.LayoutParams ll=(LinearLayout.LayoutParams)listview.getLayoutParams();
		ll.width=Utils.deviceWidth/5*3;
		listview.setLayoutParams(ll);
		listInt.add(0);
		listInt.add(1);
		listInt.add(2);
		listInt.add(3);
		listInt.add(7);
		listInt.add(11);
		listInt.add(15);
		listInt.add(14);
		listInt.add(13);
		listInt.add(12);
		listInt.add(8);
		listInt.add(4);

		listServer.add("11");
		listServer.add("12");
		listServer.add("21");
		listServer.add("22");
		listServer.add("23");
		listServer.add("26");
		listServer.add("34");
		listServer.add("33");
		listServer.add("32");
		listServer.add("31");
		listServer.add("25");
		listServer.add("34");

		for(int i=0;i<16;i++){
			JiangPinModel m=new JiangPinModel();
			m.setCheck(false);
			if(i==0) {
				m.setNormal(R.drawable.jiangpin1);
				m.setPress(R.drawable.jiangpin_p1);
				m.setBonus_type("11");
			}else if(i==1){
				m.setNormal(R.drawable.jiangpin2);
				m.setPress(R.drawable.jiangpin_p2);
				m.setBonus_type("12");
			}else if(i==2){
				m.setNormal(R.drawable.jiangpin3);
				m.setPress(R.drawable.jiangpin_p3);
				m.setBonus_type("21");
			}else if(i==3){
				m.setNormal(R.drawable.jiangpin4);
				m.setPress(R.drawable.jiangpin_p4);
				m.setBonus_type("22");
			}else if(i==4){
				m.setNormal(R.drawable.jiangpin5);
				m.setPress(R.drawable.jiangpin_p5);
				m.setBonus_type("23");
			}else if(i==5){////
				m.setNormal(R.drawable.jiangpin5);
				m.setPress(R.drawable.jiangpin_p5);
				m.setBonus_type("24");
			}else if(i==6){////
				m.setNormal(R.drawable.jiangpin5);
				m.setPress(R.drawable.jiangpin_p5);
				m.setBonus_type("24");
			}else if(i==7){
				m.setNormal(R.drawable.jiangpin6);
				m.setPress(R.drawable.jiangpin_p6);
				m.setBonus_type("24");
			}else if(i==8){
				m.setNormal(R.drawable.jiangpin7);
				m.setPress(R.drawable.jiangpin_p7);
				m.setBonus_type("25");
			}else if(i==9){////
				m.setNormal(R.drawable.jiangpin10);
				m.setPress(R.drawable.jiangpin_p10);
				m.setBonus_type("32");
			}else if(i==10){////
				m.setNormal(R.drawable.jiangpin11);
				m.setPress(R.drawable.jiangpin_p11);
				m.setBonus_type("33");
			}else if(i==11){
				m.setNormal(R.drawable.jiangpin8);
				m.setPress(R.drawable.jiangpin_p8);
				m.setBonus_type("26");
			}else if(i==12){
				m.setNormal(R.drawable.jiangpin9);
				m.setPress(R.drawable.jiangpin_p9);
				m.setBonus_type("31");
			}else if(i==13){
				m.setNormal(R.drawable.jiangpin10);
				m.setPress(R.drawable.jiangpin_p10);
				m.setBonus_type("32");
			}else if(i==14){
				m.setNormal(R.drawable.jiangpin11);
				m.setPress(R.drawable.jiangpin_p11);
				m.setBonus_type("33");
			}else if(i==15){
				m.setNormal(R.drawable.jiangpin12);
				m.setPress(R.drawable.jiangpin_p12);
				m.setBonus_type("34");
			}
			listData.add(m);
		}
		list.add("150*****850 已中:黄金会员永久");
		list.add("139*****341 已中:iphone7");
		list.add("158*****822 已中:黄金1天");
		list.add("186*****334 已中:钻石会员30天");
		list.add("133*****567 已中:客源名额500个");
		list.add("152*****998 已中:黄金会员永久");
		list.add("159*****876 已中:iphone6");
		list.add("156*****880 已中:钻石会员永久");
		list.add("137*****855 已中:黄金会员永久");
		list.add("158*****133 已中:钻石会员1天");
		list.add("189*****256 已中:黄金会员1天");
		list.add("134*****339 已中:钻石会员永久");
		list.add("155*****553 已中:客源名额1万个");
		list.add("133*****498 已中:钻石会员永久");
		list.add("179*****660 已中:客源名额500个");
		list.add("185*****698 已中:客源名额100个");
		list.add("132*****555 已中:钻石会员30天");
		list.add("157*****229 已中:黄金会员永久");
		list.add("185*****951 已中:客源名额50个");
		list.add("133*****892 已中:iphone6");
		list.add("156*****650 已中:钻石会员1天");
		list.add("175*****565 已中:iphone7");
		list.add("188*****951 已中:黄金会员1天");
		list.add("139*****553 已中:钻石会员永久");
		list.add("157*****864 已中:黄金会员永久");
		adapter=new MyAdapter();
		gridview.setAdapter(adapter);
		adapter1=new MyAdapter1();
		listview.setAdapter(adapter1);
		dialog1=new CustomDialog1(mActivity);
		dialog1.setClick1(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mActivity.openUmengShare("","","","");
				dialog1.dismiss();
			}
		});
		dialog1.setClick2(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mActivity.changeFragment(new FraLuckCode(),false);
				dialog1.dismiss();
			}
		});
		dialog2=new CustomDialog2(mActivity);
		dialog2.setConfirmOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog2.dismiss();
			}
		});
	}

	private void initData(){
		pro=new SimpleProtocol(mActivity);
		cb=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				swipe_container.setRefreshing(false);
				isBusy=false;
				try {
					JSONObject obj=new JSONObject(s);
					String str=obj.getString("lottery_left_count");
					count=Integer.valueOf(str);
					tv_xingyunma.setText("我的幸运码:"+str+"个");
				}catch (Exception e){

				}
			}

			@Override
			public void onFailure(ErrorModel errorModel) {
				isBusy=false;
				swipe_container.setRefreshing(false);
			}

			@Override
			public void onStart() {
				isBusy=true;
				swipe_container.setRefreshing(true);
			}
		};
		pro1=new SimpleProtocol(mActivity);
		cb1=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				iv_ok.setEnabled(true);
				try {
					JSONObject obj=new JSONObject(s);
					String str=obj.getString("lottery_left_count");
					count=Integer.valueOf(str);
					jiangPin=obj.getString("bonus_type");
					tv_xingyunma.setText("我的幸运码:"+str+"个");
					for(int i=0;i<listServer.size();i++){
						if(listServer.get(i).equals(jiangPin)){
							indexP=i;
							jianPinName=Utils.initJiangPin(jiangPin);
							initTimer();
						}
					}
				}catch (Exception e){

				}
			}

			@Override
			public void onFailure(ErrorModel errorModel) {
				getData();
				iv_ok.setEnabled(true);
			}

			@Override
			public void onStart() {

			}
		};
	}
	//获取抽奖次数
	private void getData(){
		if(Utils.userModel==null)return;
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r", "api/user/getChouCount");
		params.addQueryStringParameter("token", Utils.userModel.getToken());
		pro.getData(HttpRequest.HttpMethod.GET, SERVER_URL, params, cb);
	}
	//抽奖
	private void getData1(){
		if(Utils.userModel==null)return;
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r", "api/user/choujiang");
		params.addQueryStringParameter("token", Utils.userModel.getToken());
		pro1.getData(HttpRequest.HttpMethod.GET, SERVER_URL, params, cb1);
	}

	private class ViewHolder{
		ImageView iv;
	}

	private class MyAdapter extends BaseAdapter{

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
			ViewHolder holder;
			if(v==null){
				holder=new ViewHolder();
				v=View.inflate(mActivity,R.layout.item_jiangpin,null);
				holder.iv=(ImageView)v.findViewById(R.id.iv);
				LinearLayout.LayoutParams ll=(LinearLayout.LayoutParams)holder.iv.getLayoutParams();
				ll.width=(Utils.deviceWidth- DensityUtil.dip2px(mActivity,95))/4;
				ll.height=(Utils.deviceWidth- DensityUtil.dip2px(mActivity,95))/4;
				holder.iv.setLayoutParams(ll);
				v.setTag(holder);
			}else{
				holder=(ViewHolder)v.getTag();
			}
			JiangPinModel m=listData.get(i);

			if(m.isCheck()||i==index){
				holder.iv.setImageResource(m.getPress());
			}else{
				holder.iv.setImageResource(m.getNormal());
			}
			if(i==5||i==6||i==9||i==10){
				holder.iv.setVisibility(View.INVISIBLE);
			}
			return v;
		}
	}

	private class ViewHolder1{
		TextView tv;
	}

	private class MyAdapter1 extends BaseAdapter{

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int i) {
			return list.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View v, ViewGroup viewGroup) {
			ViewHolder1 holder;
			if(v==null){
				holder=new ViewHolder1();
				v=View.inflate(mActivity,R.layout.item_jiangpin1,null);
				holder.tv=(TextView)v.findViewById(R.id.tv);
				v.setTag(holder);
			}else{
				holder=(ViewHolder1)v.getTag();
			}
			String str=list.get(i);
			holder.tv.setText(""+str);
			return v;
		}
	}

	private void initTimer(){
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(next>=12){
					timer.cancel();
					next=0;
					initTimer1();
					return;
				}else{
					index=listInt.get(next);
					Message msg=new Message();
					msg.what=1;
					mHandler.sendMessage(msg);
					next++;
				}
			}
		}, 0, 100l);
	}

	private void initTimer1(){
		timer1 = new Timer();
		timer1.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(next>=12){
					timer1.cancel();
					next=0;
					initTimer2();
					return;
				}else{
					index=listInt.get(next);
					Message msg=new Message();
					msg.what=1;
					mHandler.sendMessage(msg);
					next++;
				}

			}
		}, 0, 150l);
	}

	private void initTimer2(){
		timer2 = new Timer();
		timer2.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(next==indexP){
					index=listInt.get(next);
					Message msg=new Message();
					msg.what=2;
					mHandler.sendMessage(msg);
					timer2.cancel();
					//next=indexP;
					return;
				}else{
					if(next>=12) {
						timer2.cancel();
						next = 0;
						return;
					}
					index=listInt.get(next);
					Message msg=new Message();
					msg.what=1;
					mHandler.sendMessage(msg);
					next++;
				}

			}
		}, 0, 200l);
	}

	private void initTimer3(){
		timer3 = new Timer();
		timer3.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
					Message msg=new Message();
					msg.what=3;
					mHandler.sendMessage(msg);
					timer3.cancel();
			}
		}, 3500l, 2000l);
	}
	Message msg1;
	private void initTimer4(){
		timer4 = new Timer();
		timer4.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				msg1=new Message();
				msg1.what=4;
				mHandler.sendMessage(msg1);
			}
		}, 0l, 1500l);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1://抽奖进行中
					adapter.notifyDataSetChanged();
					break;
				case 2://抽奖结束
					timer2.cancel();
					timer1.cancel();
					timer.cancel();
					adapter.notifyDataSetChanged();
					dialog2.showCD(jianPinName);
					break;
				case 3://已经没有抽奖码提示
					timer3.cancel();
					dialog1.show();
					break;
				case 4:
					index1 ++;
					if(index1 >= listview.getCount()) {
						index1 = 0;
					}
					listview.setSelection(index1);
					break;
				default:
					break;
			}
		};
	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(action_refresh);
		myIntentFilter.addAction(GET_DATA);
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
			if (action.equals(action_refresh)) {
				getData();
			}else if(action.equals(GET_DATA)){
				getData();
			}else if(action.equals(LOAD_BAOFEN)){
				getData();
			}
		}

	};

}
