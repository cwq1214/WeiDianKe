package com.hzkj.wdk.fra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzkj.wdk.R;
import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.http.SimpleResponseCallback;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.utils.UtilsLog;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tencent.weibo.sdk.android.network.ReqParam;
import com.unionpay.UPPayAssistEx;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 *支付方式
 */
public class FraPaymentType extends BaseFragment implements JiaFenConstants,OnClickListener{
	private MainActivity mActivity;
	private ImageView back;
	private IResponseCallback<String> cb;
	private SimpleProtocol pro;
	private String luckId="",content="",money="";
	private TextView tv_title,tv_money;
	private Button btn_ok;
	private CheckBox cb_wx,cb_zfb;
	private boolean luckCode=true;//购买幸运码还是开通vip
	public boolean isRecharge = false;//是否充值
	private View d1,d2,d3;

	private EditText input_recharge;
	private LinearLayout layout_recharge;
	private CheckBox cb_yl;
	private LinearLayout layout_order;
	private LinearLayout layout_allPrice;
	private TextView title;
	private LinearLayout layout_ali,layout_wx,layout_union;
	public boolean isZuanVip=false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mActivity=(MainActivity)getActivity();
		if (getArguments()!=null) {
			luckId = getArguments().getString("id");
			content = getArguments().getString("content");
			money = getArguments().getString("money");
			luckCode = getArguments().getBoolean("luckcode", true);
		}
		UtilsLog.d("====","===luckcode=="+luckCode);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.fra_payment_type,null);
		mActivity.FrameLayoutVisible(true);
		initView(v);
		initData();
		registerBoradcastReceiver();
		if (isRecharge){
			luckCode = false;
		}
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r","api/common/getpayconfig");

		new SimpleProtocol(getContext()).getData(HttpRequest.HttpMethod.GET,SERVER_URL,params,new SimpleResponseCallback(mActivity){
			@Override
			public void onSuccess(String o) {
				super.onSuccess(o);
				try {
					JSONObject jsonObject = new JSONObject(o);
					String wechat_merId = jsonObject.getString("wechat_merId");
					String wechat_api_key = jsonObject.getString("wechat_api_key");
					String wechat_app_id = jsonObject.getString("wechat_app_id");
					String alipay_merId = jsonObject.getString("alipay_merId");
					String alipay_seller = jsonObject.getString("alipay_seller");
					String alipay_rsa_private = jsonObject.getString("alipay_rsa_private");

					WeiXinPayFra.WEIXIN_API_KEY  = wechat_api_key;
					WeiXinPayFra.WEIXIN_MERCHANT = wechat_merId;
					WeiXinPayFra.WEIXIN_APPID = wechat_app_id;

					ExternalFragment.PARTNER = alipay_merId ;
					ExternalFragment.RSA_PRIVATE = alipay_rsa_private;
					ExternalFragment.SELLER = alipay_seller;


				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(ErrorModel errorModel) {
				super.onFailure(errorModel);
			}
		});
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {



		if(v==back){//返回
			mActivity.backFragment();
		}else if(v==btn_ok){//支付
			if(cb_wx.isChecked()){//v信
				if (TextUtils.isEmpty(WeiXinPayFra.WEIXIN_APPID)
						||TextUtils.isEmpty(WeiXinPayFra.WEIXIN_API_KEY)
						||TextUtils.isEmpty(WeiXinPayFra.WEIXIN_MERCHANT)){
					Utils.toastShow(getContext(),"暂不支持微信支付");
					return;
				}
				if (isZuanVip){
					getZiZuanSign("2");
				}else if (isRecharge){
					recharge("2");
				}else {
					if(luckCode)
						getData("2");
					else
						getDataVip("2");
				}
			}else if(cb_zfb.isChecked()){//支付宝
				if (TextUtils.isEmpty(ExternalFragment.PARTNER)
						||TextUtils.isEmpty(ExternalFragment.RSA_PRIVATE)
						||TextUtils.isEmpty(ExternalFragment.SELLER)){
					Utils.toastShow(getContext(),"暂不支持支付宝支付");
					return;
				}
				if (isZuanVip){
					getZiZuanSign("1");
				}else if (isRecharge){
					recharge("1");
				}else {
					if (luckCode)
						getData("1");
					else
						getDataVip("1");
				}
			}else if (cb_yl.isChecked()){//银联

				if (isZuanVip) {
					getZiZuanSign("3");
				}else if (isRecharge){
					recharge("3");
				}else {
					if(luckCode)
						getData("3");
					else
						getDataVip("3");
				}
			}
			else{
				Utils.toastShow(mActivity,"请选择支付方式");
			}
		}
	}

	private void initView(View v){
//		d1 = v.findViewById(R.id.d1);
		d2 = v.findViewById(R.id.d2);
		d3 = v.findViewById(R.id.d3);
		layout_ali = (LinearLayout) v.findViewById(R.id.layout_alipay);
		layout_wx = (LinearLayout) v.findViewById(R.id.layout_wx);
		layout_union = (LinearLayout) v.findViewById(R.id.layout_union);

		input_recharge = (EditText) v.findViewById(R.id.input_recharge);
		layout_recharge = (LinearLayout) v.findViewById(R.id.layout_recharge);
		back=(ImageView)v.findViewById(R.id.iv_left);
		back.setOnClickListener(this);
		layout_allPrice = (LinearLayout) v.findViewById(R.id.layout_allPrice);
		layout_order = (LinearLayout) v.findViewById(R.id.layout_order);
		tv_title=(TextView)v.findViewById(R.id.tv_title);
		tv_money=(TextView)v.findViewById(R.id.tv_money);
		btn_ok=(Button)v.findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(this);
		cb_wx=(CheckBox)v.findViewById(R.id.cb_wx);
		cb_yl = (CheckBox) v.findViewById(R.id.cb_yl);
		cb_zfb=(CheckBox)v.findViewById(R.id.cb_zfb);
		title = (TextView) v.findViewById(R.id.title);
		layout_wx.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cb_wx.setChecked(true);
				cb_zfb.setChecked(false);
				cb_yl.setChecked(false);
			}
		});
		layout_ali.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cb_wx.setChecked(false);
				cb_zfb.setChecked(true);
				cb_yl.setChecked(false);
			}
		});
		layout_union.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cb_wx.setChecked(false);
				cb_zfb.setChecked(false);
				cb_yl.setChecked(true);
			}
		});
		cb_wx.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cb_wx.setChecked(true);
				cb_zfb.setChecked(false);
				cb_yl.setChecked(false);
			}
		});
		cb_zfb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cb_wx.setChecked(false);
				cb_zfb.setChecked(true);
				cb_yl.setChecked(false);
			}
		});
		cb_yl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cb_wx.setChecked(false);
				cb_zfb.setChecked(false);
				cb_yl.setChecked(true);
			}
		});
		tv_title.setText(content);

		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		if (!TextUtils.isEmpty(money)) {
			tv_money.setText("¥ " + decimalFormat.format(Double.valueOf(money)));
		}else {
			tv_money.setText("¥ " + money);

		}

		layout_recharge.setVisibility(isRecharge?View.VISIBLE:View.GONE);
		d2.setVisibility(isRecharge?View.VISIBLE:View.GONE);

		layout_order.setVisibility(isRecharge?View.GONE:View.VISIBLE);
		layout_allPrice.setVisibility(isRecharge?View.GONE:View.VISIBLE);
		d3.setVisibility(isRecharge?View.GONE:View.VISIBLE);


		if (isRecharge){
			title.setText("充值");
			input_recharge.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

				}

				@Override
				public void onTextChanged(CharSequence s, int i, int i1, int i2) {
					if (s.toString().contains(".")) {
						if (s.length() - 1 - s.toString().indexOf(".") > 2) {
							s = s.toString().subSequence(0,
									s.toString().indexOf(".") + 3);
							input_recharge.setText(s);
							input_recharge.setSelection(s.length());
						}
					}
					if (s.toString().trim().substring(0).equals(".")) {
						s = "0" + s;
						input_recharge.setText(s);
						input_recharge.setSelection(2);
					}

					if (s.toString().startsWith("0")
							&& s.toString().trim().length() > 1) {
						if (!s.toString().substring(1, 2).equals(".")) {
							input_recharge.setText(s.subSequence(0, 1));
							input_recharge.setSelection(1);
							return;
						}
					}
				}

				@Override
				public void afterTextChanged(Editable editable) {

				}
			});
		}
	}

	private void initData(){
		pro=new SimpleProtocol(mActivity);
		cb=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				LoadingD.hideDialog();
				String pay_sn="";
				String pay_value="";
				String tn="";
				JSONObject obj= null;
				try {
					obj = new JSONObject(s);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				try {
					pay_sn=obj.getString("pay_sn");

				}catch (Exception e){
					e.printStackTrace();
				}
				try {
					pay_value=obj.getString("pay_value");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					tn = obj.getString("tn");
				} catch (JSONException e) {
					e.printStackTrace();
				}


				pay(content,pay_value,pay_sn,tn);
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

	private void getData(String payType) {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r", "api/user/buyLuckycode");
		params.addQueryStringParameter("token", Utils.userModel.getToken());
		params.addQueryStringParameter("lucky_code_id", ""+luckId);
		params.addQueryStringParameter("payment", ""+payType);
		pro.getData(HttpRequest.HttpMethod.GET, SERVER_URL, params, cb);
	}

	private void getDataVip(String payType) {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r", "api/user/newopenMember");
		params.addQueryStringParameter("token", Utils.userModel.getToken());
		params.addQueryStringParameter("vip_id", ""+luckId);
		params.addQueryStringParameter("payment", ""+payType);
		pro.getData(HttpRequest.HttpMethod.GET, SERVER_URL, params, cb);
	}

	//充值
	private void recharge(String payment){
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r", "api/user/qrcodeRecharge");
		params.addQueryStringParameter("token", Utils.userModel.getToken());
		params.addQueryStringParameter("payment", payment);
		params.addQueryStringParameter("price",input_recharge.getText().toString());

		pro.getData(HttpRequest.HttpMethod.GET, SERVER_URL, params, cb);
	}

	private void getZiZuanSign(String payment){
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r", "api/user/openQrcodeMember");
		params.addQueryStringParameter("token", Utils.userModel.getToken());
		params.addQueryStringParameter("serviceId",luckId);
		params.addQueryStringParameter("payment", payment);

		pro.getData(HttpRequest.HttpMethod.GET, SERVER_URL, params, cb);

	}


	private void pay (String name,String price,String sn,String tn){
		Bundle b=new Bundle();
		b.putString("paySn", sn);
		b.putString("name", "" + name);
		b.putString("price", "" + price);
		b.putString("tn", "" + tn);
		if(cb_zfb.isChecked()){//支付宝
			System.out.println("=====支付宝");
			ExternalFragment fra=new ExternalFragment();
			fra.setArguments(b);
			fra.setData(name, price, sn,mActivity,luckCode);
			fra.pay();
			//mActivity.changeFragment(fra, false);
		}else if(cb_wx.isChecked()){//v信支付
			System.out.println("=====v信");
			WeiXinPayFra fra=new WeiXinPayFra();
			fra.setArguments(b);
			fra.setData(name,price,sn,mActivity,luckCode);
			fra.pay();
			//mActivity.changeFragment(fra, false);
		}else if (cb_yl.isChecked()){
//			UnionPayFragment unionPayFragment = new UnionPayFragment();
//			unionPayFragment.setArguments(b);
//			unionPayFragment.setData(name,price,sn,luckCode,tn);
//			unionPayFragment.pay();
//			mActivity.changeFragment(unionPayFragment,false);
			UPPayAssistEx.startPay(getContext(), null, null, tn, "00");
		}
	}

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
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
			if(action.equals(GET_DATA)){
				//mActivity.backFragment();
			}
		}

	};

}
