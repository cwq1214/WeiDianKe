package com.hzkj.wdk.widget;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.hzkj.wdk.MyApplication;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.UserModel;
import com.hzkj.wdk.model.VipModel;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.SharePreferenceUtil;
import com.hzkj.wdk.utils.Utils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.hzkj.wdk.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 登录
 */
public class PWLogin extends LinearLayout implements View.OnClickListener,JiaFenConstants {

	private View mView;
	private EditText et_phone,et_code,et_phone_invite;
	private Button btn_ok,btn_getCode;
	private Context c;
	private SharePreferenceUtil spu;
	private PopupWindow window;
	private OnSuccess oSuccess;
	private SimpleProtocol pro,proCode;
	private IResponseCallback<String> cb,cbCode;
	private MyApplication applica;
	private MyCount mc;
	private long leftTime = 0;

	public interface OnSuccess{
		public void onClick(int b);
	}

	public PWLogin(Context context,MyApplication application) {
		// TODO Auto-generated constructor stub
		super(context);
		c=context;
		applica=application;
		spu=new SharePreferenceUtil(context);
	}

	@Override
	public void onClick(View v) {
		if(v==btn_ok){
			if(verification())
				login();
		}else if(v==btn_getCode){
			getCode();
		}
	}

	public void showPW(View parent){
		if (window == null) {
			mView=View.inflate(c,R.layout.pw_login,null);
			et_code = (EditText)mView.findViewById(R.id.et_code);
			et_phone=(EditText)mView.findViewById(R.id.et_phone);
			btn_ok = (Button)mView.findViewById(R.id.btn_ok);
			btn_ok.setOnClickListener(this);
			btn_getCode = (Button)mView.findViewById(R.id.btn_getCode);
			btn_getCode.setOnClickListener(this);
			et_phone_invite=(EditText)mView.findViewById(R.id.et_phone_invite);

			initView();
			initData();
			window = new PopupWindow(mView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}
		//动画
		window.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent_dish_bg));
		window.setFocusable(true);
		window.update();
		window.showAtLocation(parent, Gravity.CENTER, 0, 0);
	}

	public void setSuccessClick(OnSuccess os){
		this.oSuccess=os;
	}

	private void initView(){
		et_phone.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if(charSequence.length()==11){
					btn_getCode.setEnabled(true);
					btn_getCode.setBackgroundResource(R.drawable.btn_blue_angle);
				}else{
					btn_getCode.setEnabled(false);
					btn_getCode.setBackgroundResource(R.drawable.btn_gray_angle);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});
		et_code.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (charSequence.length() >= 4) {
					btn_ok.setEnabled(true);
					btn_ok.setBackgroundResource(R.drawable.btn_red_angle);
				} else {
					btn_ok.setBackgroundResource(R.drawable.btn_gray_angle);
					btn_ok.setEnabled(false);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});
	}

	//初始化数据
	private void initData(){
		pro=new SimpleProtocol(c);
		cb=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				System.out.println("login "+s);
				try {
					JSONObject obj=new JSONObject(s);
					UserModel um=new Gson().fromJson(obj.toString(),UserModel.class);
					spu.setToken(um.getToken());
					spu.setHome2Url(um.getWeichat_contacts_url());
					um.setListViPs(new ArrayList<VipModel>());
					JSONArray array=obj.getJSONArray("vip_types");
					for(int i=0;i<array.length();i++){
						VipModel vm=new Gson().fromJson(array.getString(i),VipModel.class);
						um.getListViPs().add(vm);
					}
					Utils.userModel=um;
					spu.setLoginName(et_phone.getText().toString());

					Intent mIntent = new Intent(action_refresh);
					c.sendBroadcast(mIntent);
				}catch (Exception e){

				}
				window.dismiss();
			}

			@Override
			public void onFailure(ErrorModel errorModel) {
				//window.dismiss();
				Utils.toastShow(c,"登录失败,请重新登录");
			}

			@Override
			public void onStart() {

			}
		};
		proCode=new SimpleProtocol(c);
		cbCode=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				Utils.toastShow(c,"发送成功");
				if (mc != null)
					mc.cancel();
				mc = new MyCount(60 * 1000, 1000); // 第一参数是总的时间，第二个是间隔时间 都是毫秒为单位
				mc.start();
				LoadingD.hideDialog();
			}

			@Override
			public void onFailure(ErrorModel errorModel) {
				Utils.toastShow(c,"发送失败");
				LoadingD.hideDialog();
			}

			@Override
			public void onStart() {
				LoadingD.hideDialog();
				LoadingD.showDialog(c);
			}
		};
	}

	//验证
	private boolean verification(){
		if(TextUtils.isEmpty(et_phone.getText().toString())){
			Utils.toastShow(c, "请输入用户名");
			return false;
		}else if(TextUtils.isEmpty(et_code.getText().toString())){
			Utils.toastShow(c,"请输入验证码");
			return false;
		}
		return true;
	}

	public void login(){
		if(verification()){
			//spu.setLoginName(et_phone.getText().toString());
			getData();
		}
	}

	public void getCode(){
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r", "api/common/sendsms");
		params.addQueryStringParameter("phone", "" + et_phone.getText().toString());
		proCode.getData(HttpRequest.HttpMethod.GET, SERVER_URL, params, cbCode);
	}

	private void getData(){
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r", "api/common/userDo");
		params.addQueryStringParameter("phone", "" + et_phone.getText().toString());
		params.addQueryStringParameter("ref_phone",et_phone_invite.getText().toString());
		params.addQueryStringParameter("code",""+et_code.getText().toString());
		pro.getData(HttpRequest.HttpMethod.GET, SERVER_URL, params, cb);
	}

	// 倒计时
	class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			btn_getCode.setEnabled(true);
			btn_getCode.setText("获取验证码");
//			btn_get_code
//					.setTextColor(getResources().getColor(R.color.blue_new));
		}

		@Override
		public void onTick(long millisUntilFinished) {
			btn_getCode.setEnabled(false);
//			btn_get_code.setTextColor(getResources().getColor(
//					R.color.login_gray1_color));
			// btn_get_code.setText("重新获取(" + millisUntilFinished / 1000 + ")");
			btn_getCode.setText(String.format("重新获取(%1$d)",
					millisUntilFinished / 1000));
			leftTime = millisUntilFinished / 1000;
		}
	}


}