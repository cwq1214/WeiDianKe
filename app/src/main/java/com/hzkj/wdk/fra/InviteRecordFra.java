package com.hzkj.wdk.fra;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.RecordModel;
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
 *邀请记录
 */
public class InviteRecordFra extends BaseFragment implements JiaFenConstants,OnClickListener{
	private MainActivity mActivity;
	private ImageView back,iv_right;
	private IResponseCallback<String> cb;
	private SimpleProtocol pro;
	private UserModel userModel=null;
	private String token=null;
	private ListView listview;
	private MyAdapter adapter;
	private List<RecordModel> listD=new ArrayList<>();

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
		View v=inflater.inflate(R.layout.fragment_invite_record,null);
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
		}else if(v==iv_right){
			getData();
		}
	}

	private void initView(View v){
		back=(ImageView)v.findViewById(R.id.iv_left);
		back.setOnClickListener(this);
		iv_right=(ImageView)v.findViewById(R.id.iv_right);
		iv_right.setOnClickListener(this);
		listview=(ListView)v.findViewById(R.id.listview);

		adapter=new MyAdapter(listD);
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
					JSONArray array=obj.getJSONArray("items");
					for(int i=0;i<array.length();i++){
						RecordModel item=new Gson().fromJson(array.getString(i),RecordModel.class);
						listD.add(item);
					}
				}catch (Exception e){

				}
				if(listD.size()>0){
					adapter.setData(listD);
					adapter.notifyDataSetChanged();
				}

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
		params = Utils.addParams(params);
		params.addQueryStringParameter("r", "api/user/inviteList");
		params.addQueryStringParameter("token", token);
		pro.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, cb);
	}

	private class ViewHolder {
		TextView tv_content,tv_time;
	}
	private class MyAdapter extends BaseAdapter {
		List<RecordModel> listData;
		MyAdapter(List<RecordModel> listM){
			listData=listM;
		}
		void setData(List<RecordModel> listM){
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
				v=View.inflate(mActivity,R.layout.item_record,null);
				holder.tv_content=(TextView)v.findViewById(R.id.tv_content);
				holder.tv_time=(TextView)v.findViewById(R.id.tv_time);
				v.setTag(holder);
			}else{
				holder=(ViewHolder)v.getTag();
			}
			return v;
		}
	}

}
