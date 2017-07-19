package com.hzkj.wdk.fra;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hzkj.wdk.R;
import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.UserModel;
import com.hzkj.wdk.model.VipModel;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.widget.PWRecharge;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 *选择会员类型
 */
public class MemberTypeFra extends BaseFragment implements JiaFenConstants,OnClickListener{
	private MainActivity mActivity;
	private ImageView back;
	private IResponseCallback<String> cb;
	private SimpleProtocol pro;
	private UserModel userModel=null;
	private String token=null;
	private ListView listview;
	private String id,name;
	private BaseAdapter adapter;

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
		View v=inflater.inflate(R.layout.fra_member_type,null);
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
		back=(ImageView)v.findViewById(R.id.iv_left);
		back.setOnClickListener(this);
		listview=(ListView)v.findViewById(R.id.listview);
		adapter=new MyAdapter();
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				FraPaymentType fra=new FraPaymentType();
				Bundle b=new Bundle();
				b.putString("id",""+Utils.configModel.getListViPs().get(i).getId());
				b.putString("content",Utils.configModel.getListViPs().get(i).getValue_des());
				b.putString("money",""+Utils.configModel.getListViPs().get(i).getValue());
				b.putBoolean("luckcode",false);
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
		TextView tv_content,tv_type;
		View view;
		ImageView iv_left;
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (Utils.configModel==null)
				return 0;
			return Utils.configModel.getListViPs()==null?0:Utils.configModel.getListViPs().size();
		}

		@Override
		public Object getItem(int i) {
			return Utils.configModel.getListViPs().get(i);
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
				v=View.inflate(mActivity,R.layout.item_member_type,null);
				holder.tv_content=(TextView)v.findViewById(R.id.tv_content);
				holder.tv_type=(TextView)v.findViewById(R.id.tv_type);
				holder.view=(View)v.findViewById(R.id.view);
				holder.iv_left=(ImageView)v.findViewById(R.id.iv_left);
				v.setTag(holder);
			}else{
				holder=(ViewHolder)v.getTag();
			}
			final VipModel model=Utils.configModel.getListViPs().get(i);
			holder.tv_content.setText(model.getFunction_des());
			if(i==0||i==3){
				holder.tv_type.setTextColor(getResources().getColor(R.color.red));
				//holder.tv_content.setTextColor(getResources().getColor(R.color.black));
			}else if(i==4||i==5){
				holder.tv_type.setTextColor(getResources().getColor(R.color.blue));
				//holder.tv_content.setTextColor(getResources().getColor(R.color.black));
			}else{
				holder.tv_type.setTextColor(getResources().getColor(R.color.black));
				//holder.tv_content.setTextColor(getResources().getColor(R.color.black));
			}
			if(i==2||i==4){
				holder.view.setVisibility(View.VISIBLE);
			}else{
				holder.view.setVisibility(View.GONE);
			}

			if(model.getValue_des().contains("黄金")){
				holder.iv_left.setImageResource(R.drawable.huangjin);
			}else{
				holder.iv_left.setImageResource(R.drawable.zuanshi);
			}
			holder.tv_type.setText(model.getValue_des());
//			v.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					name=model.getValue_des();
//					id=model.getId();
//				}
//			});
			return v;
		}
	}

}
