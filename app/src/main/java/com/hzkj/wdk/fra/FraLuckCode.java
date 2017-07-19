package com.hzkj.wdk.fra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.hzkj.wdk.MyApplication;
import com.hzkj.wdk.R;
import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.UserModel;
import com.hzkj.wdk.model.XingYunMaModel;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.Utils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 *购买幸运码
 */
public class FraLuckCode extends BaseFragment implements JiaFenConstants,OnClickListener,
		SwipeRefreshLayout.OnRefreshListener{
	private MainActivity mActivity;
	private ImageView back;
	private IResponseCallback<String> cb;
	private SimpleProtocol pro;
	private UserModel userModel=null;
	private String token=null;
	private List<XingYunMaModel> listData=new ArrayList<>();
	private BaseAdapter adapter;
	private ListView listview;
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
		View v=inflater.inflate(R.layout.fra_luck_code,null);
		registerBoradcastReceiver();
		mActivity.FrameLayoutVisible(true);
		userModel= Utils.userModel;
		if(userModel!=null)
		token =userModel.getToken();
		initView(v);
		initData();
		listData.clear();
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
		}
	}

	@Override
	public void onRefresh() {
		listData.clear();
		getData();
	}

	private void initView(View v){
		back=(ImageView)v.findViewById(R.id.iv_left);
		back.setOnClickListener(this);
		swipe_container=(SwipeRefreshLayout)v.findViewById(R.id.swipe_container);
		swipe_container.setOnRefreshListener(this);
		swipe_container.setColorSchemeResources(android.R.color.holo_orange_light,
				android.R.color.holo_orange_dark,
				android.R.color.holo_red_light);
		adapter=new MyAdapter();
		listview=(ListView)v.findViewById(R.id.listview);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				FraPaymentType fra=new FraPaymentType();
				Bundle b=new Bundle();
				b.putString("id",""+listData.get(i).getLucky_code_id());
				b.putString("content",listData.get(i).getLucky_code_count()+"个幸运码"+"【 ¥ "+listData.get(i).getLucky_code_price()+" 】");
				b.putString("money",""+listData.get(i).getLucky_code_price());
				fra.setArguments(b);
				mActivity.changeFragment(fra,false);
			}
		});
	}

	private void initData(){
		pro=new SimpleProtocol(mActivity);
		cb=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				LoadingD.hideDialog();
				try {
					JSONArray array=new JSONArray(s);
					for(int i=0;i<array.length();i++){
						XingYunMaModel model=new Gson().fromJson(array.getString(i),XingYunMaModel.class);
						listData.add(model);
					}
				}catch (Exception e){

				}
				if(listData.size()>0)
					adapter.notifyDataSetChanged();
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

	private void getData() {
		RequestParams params = new RequestParams();
		params = Utils.addParams(params);
		params.addQueryStringParameter("token", token);
		params.addQueryStringParameter("r", "api/common/getChoujiangList");
		pro.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, cb);
	}

		private void buy(){
		if(Utils.userModel==null)return;
		RequestParams params = new RequestParams();
		params = Utils.addParams(params);
		params.addQueryStringParameter("token", Utils.userModel.getToken());
		params.addQueryStringParameter("r", "api/user/buyLuckycode");
		pro.getData(HttpRequest.HttpMethod.GET, SERVER_URL, params, cb);
	}


	private class ViewHolder{
		TextView tv_title,tv_price;
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
				v=View.inflate(mActivity,R.layout.item_luck_code,null);
				holder.tv_price=(TextView)v.findViewById(R.id.tv_price);
				holder.tv_title=(TextView)v.findViewById(R.id.tv_title);
				v.setTag(holder);
			}else{
				holder=(ViewHolder)v.getTag();
			}
			XingYunMaModel model=listData.get(i);
			holder.tv_price.setText("【 ¥ "+model.getLucky_code_price()+" 】");
			holder.tv_title.setText(model.getLucky_code_count()+"个幸运码");
			return v;
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
