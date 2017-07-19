package com.hzkj.wdk.fra;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.hzkj.wdk.model.JiangPinModel;
import com.hzkj.wdk.model.UserModel;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.Utils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *我的奖品
 */
public class MyJiangPinFra extends BaseFragment implements JiaFenConstants,OnClickListener
		,SwipeRefreshLayout.OnRefreshListener{
	private MainActivity mActivity;
	private ImageView back;
	private IResponseCallback<String> cb;
	private SimpleProtocol pro;
	private SwipeRefreshLayout swipe_container;
	private List<JiangPinModel> listData=new ArrayList<>();
	private BaseAdapter adapter;
	private ListView listview;

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
		View v=inflater.inflate(R.layout.fra_my_jiangpin,null);
		mActivity.FrameLayoutVisible(true);
		initView(v);
		initData();
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
		listview=(ListView) v.findViewById(R.id.listview);
		adapter=new MyAdapter();
		listview.setAdapter(adapter);
	}

	private void initData(){
		pro=new SimpleProtocol(mActivity);
		cb=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				//LoadingD.hideDialog();
				listData.clear();
				swipe_container.setRefreshing(false);
				try {
					JSONArray obj=new JSONArray(s);
					for(int i=0;i<obj.length();i++){
						JiangPinModel m=new Gson().fromJson(obj.getString(i),JiangPinModel.class);
						listData.add(m);
					}
				}catch (Exception e){

				}
				if(listData.size()>0)
					adapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(ErrorModel errorModel) {
				//LoadingD.hideDialog();
				swipe_container.setRefreshing(false);
			}

			@Override
			public void onStart() {
				//LoadingD.showDialog(mActivity);
				swipe_container.setRefreshing(true);
			}
		};
	}

	private void getData(){
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("token", Utils.userModel.getToken());
		params.addQueryStringParameter("r", "api/user/getChouRecord");
		pro.getData(HttpRequest.HttpMethod.GET, SERVER_URL, params, cb);
	}

	class ViewHolder{
		TextView tv_title,tv_time;
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
				v=View.inflate(mActivity,R.layout.item_my_jiangpin,null);
				holder.tv_time=(TextView)v.findViewById(R.id.tv_time);
				holder.tv_title=(TextView)v.findViewById(R.id.tv_title);
				v.setTag(holder);
			}else{
				holder=(ViewHolder)v.getTag();
			}
			JiangPinModel m=listData.get(i);
			holder.tv_title.setText(Utils.initJiangPin(m.getBonus_type()));
			holder.tv_time.setText(Utils.TimeStampDate(m.getAdd_time(),"yyyy-MM-dd"));
			return v;
		}
	}



}
