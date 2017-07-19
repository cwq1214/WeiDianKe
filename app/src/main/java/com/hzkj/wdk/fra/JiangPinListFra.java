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
import java.util.ArrayList;
import java.util.List;

/**
 *奖品列表
 */
public class JiangPinListFra extends BaseFragment implements JiaFenConstants,OnClickListener
		{
	private MainActivity mActivity;
	private ImageView back;
	private IResponseCallback<String> cb;
	private SimpleProtocol pro;
	private UserModel userModel=null;
	private String token=null;
	//private SwipeRefreshLayout swipe_container;
	private List<String> listData=new ArrayList<>();
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
		View v=inflater.inflate(R.layout.fra_jiangpin_list,null);
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
		listData.add("iPhone6");
		listData.add("iPhone7");
		listData.add("钻石会员1天");
		listData.add("钻石会员30天");
		listData.add("钻石会员永久");
		listData.add("黄金会员1天");
		listData.add("黄金会员永久");
		listData.add("50个加粉名额");
		listData.add("100个加粉名额");
		listData.add("500个加粉名额");
		listData.add("10000个加粉名额");
		back=(ImageView)v.findViewById(R.id.iv_left);
		back.setOnClickListener(this);
//		swipe_container=(SwipeRefreshLayout)v.findViewById(R.id.swipe_container);
//		swipe_container.setOnRefreshListener(this);
//		swipe_container.setColorSchemeResources(android.R.color.holo_orange_light,
//				android.R.color.holo_orange_dark,
//				android.R.color.holo_red_light);
		listview=(ListView) v.findViewById(R.id.listview);
		adapter=new MyAdapter();
		listview.setAdapter(adapter);
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

	private void getData() {
		RequestParams params = new RequestParams();
		params = Utils.addParams(params);
		params.addQueryStringParameter("token", token);
		params.addQueryStringParameter("r", "api/user/getChouRecord");
		pro.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, cb);
	}



	class ViewHolder{
		TextView tv_title;
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
				v=View.inflate(mActivity,R.layout.item_jiangpin_list,null);
				holder.tv_title=(TextView)v.findViewById(R.id.tv_title);
				v.setTag(holder);
			}else{
				holder=(ViewHolder)v.getTag();
			}
			holder.tv_title.setText(listData.get(i));
			return v;
		}
	}

}
