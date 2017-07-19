package com.hzkj.wdk.fra;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.UserModel;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.Utils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.hzkj.wdk.MyApplication;
import com.hzkj.wdk.R;

/**
 *体现
 */
public class TiXianFra extends BaseFragment implements JiaFenConstants,OnClickListener{
	private MainActivity mActivity;
	private ImageView back;
	private IResponseCallback<String> cb;
	private SimpleProtocol pro;
	private UserModel userModel=null;
	private String token=null;
	private TextView tv_yue;
	private EditText et_account,et_name,et_jine;
	private Button btn_ok;
	private double price;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mActivity=(MainActivity)getActivity();
		if(!TextUtils.isEmpty(getArguments().getString("price"))) {
			price =Double.parseDouble(getArguments().getString("price"));
		}
		initData();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.fragment_tixian,null);
		mActivity.FrameLayoutVisible(true);
		userModel= Utils.userModel;
		if(userModel!=null)
		token =userModel.getToken();
		initView(v);
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
		}else if(v==btn_ok){
			if(verification()){
				getData();
			}
		}
	}

	private void initView(View v){
		back=(ImageView)v.findViewById(R.id.iv_left);
		back.setOnClickListener(this);
		btn_ok=(Button)v.findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(this);
		et_account=(EditText)v.findViewById(R.id.et_account);
		et_jine=(EditText)v.findViewById(R.id.et_jine);
		et_name=(EditText)v.findViewById(R.id.et_name);
		tv_yue=(TextView)v.findViewById(R.id.tv_yue);
		tv_yue.setText(TextUtils.isEmpty(""+price)?"0.01":""+price);
	}

	private void initData(){
		pro=new SimpleProtocol(mActivity);
		cb=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				LoadingD.hideDialog();
				Utils.toastShow(mActivity,"申请成功，请耐心等待");
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
		params.addQueryStringParameter("r", "api/user/withdraw");
		params.addQueryStringParameter("token", token);
		params.addQueryStringParameter("alipay_account", et_account.getText().toString());
		params.addQueryStringParameter("real_name", et_name.getText().toString());
		params.addQueryStringParameter("cash_value", et_jine.getText().toString());
		pro.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, cb);
	}

	private boolean verification(){
		if(price<10){
			Utils.toastShow(mActivity,"金额不足");
			return false;
		} else if(TextUtils.isEmpty(et_account.getText().toString())){
			initError(et_account,"请输入账号");
			return false;
		}else if(TextUtils.isEmpty(et_name.getText().toString())){
			initError(et_name,"请输入姓名");
			return false;
		}else if(TextUtils.isEmpty(et_jine.getText().toString())){
			initError(et_jine,"请输入金额");
			return false;
		}
		return true;
	}

	private void initError(EditText et,String str){
		et.setFocusable(true);
		et.setFocusableInTouchMode(true);
		et.requestFocus();
		et.setError(str);
	}

}
