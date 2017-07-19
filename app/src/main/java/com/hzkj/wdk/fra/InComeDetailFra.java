package com.hzkj.wdk.fra;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.IncomeModel;
import com.hzkj.wdk.model.IncomesDetailModel;
import com.hzkj.wdk.model.UserModel;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.Utils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.hzkj.wdk.MyApplication;
import com.hzkj.wdk.R;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 *收入明细
 */
public class InComeDetailFra extends BaseFragment implements JiaFenConstants,OnClickListener{
	private MainActivity mActivity;
	private ImageView back,iv_right;
	private IResponseCallback<String> cb;
	private SimpleProtocol pro;
	private UserModel userModel=null;
	private String token=null;
	private TextView tv_yue,tv_shouru,tv_tixian,tv_jilu1,tv_jilu2;
	private Button btn_tixian;
	private ListView listview;
	private MyAdapter adapter;
	private IncomeModel model;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mActivity=(MainActivity)getActivity();
		initData();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.fragment_in_come_detail,null);
		mActivity.FrameLayoutVisible(true);
		userModel= Utils.userModel;
		if(userModel!=null)
		token =userModel.getToken();
		initView(v);
		getData();
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
		}else if(v==tv_jilu1){
			tv_jilu1.setBackgroundResource(R.color.color_translation);
			tv_jilu2.setBackgroundResource(R.drawable.btn_white_rightangle);
			tv_jilu1.setTextColor(getResources().getColor(R.color.white));
			tv_jilu2.setTextColor(getResources().getColor(R.color.red));
			MyAdapter adapter=new MyAdapter(model.getListData());
			listview.setAdapter(adapter);
		}else if(v==tv_jilu2){
			tv_jilu2.setBackgroundResource(R.color.color_translation);
			tv_jilu1.setBackgroundResource(R.drawable.btn_white_leftangle);
			tv_jilu1.setTextColor(getResources().getColor(R.color.red));
			tv_jilu2.setTextColor(getResources().getColor(R.color.white));
			MyAdapter adapter1=new MyAdapter(model.getListDataCash());
			listview.setAdapter(adapter1);
		}else if(iv_right==v){
			getData();
		}
	}

	private void initView(View v){
		back=(ImageView)v.findViewById(R.id.iv_left);
		back.setOnClickListener(this);
		iv_right=(ImageView)v.findViewById(R.id.iv_right);
		iv_right.setOnClickListener(this);
		tv_jilu1=(TextView)v.findViewById(R.id.tv_jilu1);
		tv_jilu2=(TextView)v.findViewById(R.id.tv_jilu2);
		tv_shouru=(TextView)v.findViewById(R.id.tv_shouru);
		tv_tixian=(TextView)v.findViewById(R.id.tv_tixian);
		tv_yue=(TextView)v.findViewById(R.id.tv_yue);
		tv_jilu1.setOnClickListener(this);
		tv_jilu2.setOnClickListener(this);
		btn_tixian=(Button)v.findViewById(R.id.btn_tixian);
		listview=(ListView)v.findViewById(R.id.listview);
		List<IncomesDetailModel> listd=new ArrayList<>();
		adapter=new MyAdapter(listd);
		listview.setAdapter(adapter);
	}

	private void initData(){
		pro=new SimpleProtocol(mActivity);
		cb=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				LoadingD.hideDialog();
				try {
					JSONObject obj=new JSONObject(s);
					model=new Gson().fromJson(obj.toString(),IncomeModel.class);
					model.setListData(new ArrayList<IncomesDetailModel>());
					model.setListDataCash(new ArrayList<IncomesDetailModel>());
					JSONArray array=obj.getJSONArray("incomes_history");
					JSONArray arrayCash=obj.getJSONArray("cash_history");
					for(int i=0;i<array.length();i++){
						model.getListData().add(new Gson().fromJson(array.getString(i),IncomesDetailModel.class));
					}
					for(int i=0;i<arrayCash.length();i++){
						model.getListDataCash().add(new Gson().fromJson(arrayCash.getString(i),IncomesDetailModel.class));
					}
				}catch (Exception  e){

				}
				if(model!=null)
					setData();
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
		params.addQueryStringParameter("r", "api/user/getBalance");
		params.addQueryStringParameter("token", token);
		pro.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, cb);
	}
	private void setData(){
		tv_yue.setText(model.getRemains());
		tv_tixian.setText(model.getCasched());
		tv_shouru.setText(model.getIncomes());
		MyAdapter adapter=new MyAdapter(model.getListData());
		listview.setAdapter(adapter);
	}
	private class ViewHolder {
		TextView tv_content,tv_money,tv_time;
	}
	private class MyAdapter extends BaseAdapter{
		List<IncomesDetailModel> listData;
		MyAdapter(List<IncomesDetailModel> listM){
			listData=listM;
		}
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
				v=View.inflate(mActivity,R.layout.item_shouru,null);
				holder.tv_content=(TextView)v.findViewById(R.id.tv_content);
				holder.tv_money=(TextView)v.findViewById(R.id.tv_money);
				holder.tv_time=(TextView)v.findViewById(R.id.tv_time);
				v.setTag(holder);
			}else{
				holder=(ViewHolder)v.getTag();
			}
			IncomesDetailModel model=listData.get(i);
			holder.tv_content.setText(model.getDes());
			holder.tv_money.setText(model.getValue());
			holder.tv_time.setText(model.getTime());
			return v;
		}
	}

}
