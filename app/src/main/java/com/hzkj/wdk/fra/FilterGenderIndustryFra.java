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

import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.model.UserModel;
import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.MyApplication;
import com.hzkj.wdk.R;
import com.hzkj.wdk.utils.JiaFenConstants;

import java.util.ArrayList;
import java.util.List;

/**
 *筛选
 */
public class FilterGenderIndustryFra extends BaseFragment implements JiaFenConstants,OnClickListener{
	private MainActivity mActivity;
	private ImageView back,ivCheck;
	private IResponseCallback<String> cb;
	private SimpleProtocol pro;
	private UserModel userModel=null;
	private String token=null;
	private ListView listview;
	private List<String> listData=new ArrayList<>();
	private MyAdapter adapterGender,adapterIndustry;
	private boolean gender=true;
	private Button btn_ok;
	private String content;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mActivity=(MainActivity)getActivity();
		Bundle b = getArguments();
		gender=b.getBoolean("type");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.fragment_filter_gender_industry,null);
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
			mActivity.backFragment();
		}
	}

	private void initView(View v){
		String[] genders = getResources().getStringArray(R.array.gender);
		String[] industry=getResources().getStringArray(R.array.industry);
		adapterGender=new MyAdapter(genders);
		adapterIndustry=new MyAdapter(industry);
		back=(ImageView)v.findViewById(R.id.iv_left);
		back.setOnClickListener(this);
		btn_ok=(Button)v.findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(this);
		listview=(ListView)v.findViewById(R.id.listview);
		if(gender)
			listview.setAdapter(adapterGender);
		else
			listview.setAdapter(adapterIndustry);

	}

	private class ViewHolder{
		TextView tv_name;
		ImageView iv_check;
	}

	private class MyAdapter extends BaseAdapter{
		String[] strs;
		MyAdapter(String[] smida){
			strs=smida;
		}

		@Override
		public int getCount() {
			return strs.length;
		}

		@Override
		public Object getItem(int i) {
			return strs[i];
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View v, ViewGroup viewGroup) {
			final ViewHolder holder;
			if(v==null) {
				holder=new ViewHolder();
				v = View.inflate(mActivity, R.layout.item_gender_industry, null);
				holder.iv_check=(ImageView)v.findViewById(R.id.iv_check);
				holder.tv_name=(TextView)v.findViewById(R.id.tv_name);
				v.setTag(holder);
			}else{
				holder=(ViewHolder)v.getTag();
			}
			holder.tv_name.setText(strs[i]);
			holder.tv_name.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if (ivCheck != null) {
						ivCheck.setVisibility(View.INVISIBLE);
					}
					holder.iv_check.setVisibility(View.VISIBLE);
					content = holder.tv_name.getText().toString();
					ivCheck = holder.iv_check;
					if(gender)
						Utils.gender=content;
					else
						Utils.industry=content;
				}
			});
			return v;
		}
	}

}
