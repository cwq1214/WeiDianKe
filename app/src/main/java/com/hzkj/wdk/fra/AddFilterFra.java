package com.hzkj.wdk.fra;

import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hzkj.wdk.model.UserModel;
import com.hzkj.wdk.utils.Utils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.hzkj.wdk.MyApplication;
import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.R;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.MobileModel;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.SharePreferenceUtil;
import com.hzkj.wdk.utils.UtilsLog;
import com.hzkj.wdk.widget.CustomDialog;
import com.hzkj.wdk.widget.PWSelectCity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *筛选加粉
 */
public class AddFilterFra extends BaseFragment implements JiaFenConstants,OnClickListener{
	private MainActivity mActivity;
	private ImageView back;
	private IResponseCallback<String> cb,cbAdd;
	private SimpleProtocol pro,proAdd;
	private UserModel userModel=null;
	private String token=null;
	private TextView tv_area,tv_industry,tv_gender;
	private PWSelectCity pw;
	private Button btn_ok;
	private static SQLiteDatabase sqLite;
	private List<MobileModel> listData=new ArrayList<>();
	private String zipCode;
	private SelectTask selectTask;
	private CustomDialog cd;
	private AddTask addTask;
	private SharePreferenceUtil spu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mActivity=(MainActivity)getActivity();
		pw=new PWSelectCity(mActivity);
		spu=new SharePreferenceUtil(mActivity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.fragment_add_filter,null);
		Utils.initV(mActivity);
		userModel= Utils.userModel;
		if(userModel!=null)
		token =userModel.getToken();
		mActivity.FrameLayoutVisible(true);
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
		}else if(v==tv_area){//地区
			pw=new PWSelectCity(mActivity);
			pw.setOkClick(new PWSelectCity.OKClick() {
				@Override
				public void okClick(String area,String city, String code) {
					if(area.equals(city)){city="";}
					tv_area.setText(""+area+"  "+city);
					zipCode=code;
				}
			});
			pw.ShowPop(v);
		}else if(v==tv_gender){//性别
			FilterGenderIndustryFra ff=new FilterGenderIndustryFra();
			Bundle b=new Bundle();
			b.putBoolean("type", true);
			ff.setArguments(b);
			mActivity.changeFragment(ff,false);
		}else if(v==tv_industry){//职业
			FilterGenderIndustryFra ff=new FilterGenderIndustryFra();
			Bundle b=new Bundle();
			b.putBoolean("type", false);
			ff.setArguments(b);
			mActivity.changeFragment(ff, false);
		}else if(v==btn_ok){
			pw.cancle();

//			if(!selectTask.isCancelled()){
//				selectTask.cancel(true);
//				selectTask=null;
//			}
//			selectTask=new SelectTask();
//			selectTask.execute();
//			LoadingD.showDialog(mActivity);
			requestDataJiaFen();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}
		}
	}

	private void initView(View v){
		initDialog();
		selectTask=new SelectTask();
		addTask=new AddTask();
		back=(ImageView)v.findViewById(R.id.iv_left);
		back.setOnClickListener(this);
		tv_area=(TextView)v.findViewById(R.id.tv_area);
		tv_area.setOnClickListener(this);
		tv_gender=(TextView)v.findViewById(R.id.tv_gender);
		tv_gender.setOnClickListener(this);
		tv_industry=(TextView)v.findViewById(R.id.tv_industry);
		tv_industry.setOnClickListener(this);
		btn_ok=(Button)v.findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(this);
		if(!Utils.isNull(Utils.gender)){
			tv_gender.setText(Utils.gender);
		}
		if(!Utils.isNull(Utils.industry)){
			tv_industry.setText(Utils.industry);
		}
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
				LoadingD.hideDialog();
				LoadingD.showDialog(mActivity);
			}
		};
		proAdd=new SimpleProtocol(mActivity);
		cbAdd=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				LoadingD.hideDialog();
				listData.clear();
				try{
					JSONObject obj=new JSONObject(s);
					JSONArray array=obj.getJSONArray("items");
					for(int i=0;i<array.length();i++){
						MobileModel mm=new MobileModel();
						mm.setMobileNumber(array.getJSONObject(i).getString("phone"));
						listData.add(mm);
						//LoadingD.hideDialog();
					}
				}catch (Exception e){

				}
				if(listData.size()>0) {
					initDialog();
					cd.show();
				}
			}

			@Override
			public void onFailure(ErrorModel errorModel) {
				LoadingD.hideDialog();
			}

			@Override
			public void onStart() {
				LoadingD.hideDialog();
				LoadingD.showDialog(mActivity);
			}
		};
	}

	//根据城市查询
	private class SelectTask extends AsyncTask<ListView, String, List<MobileModel>> {

		@Override
		protected List<MobileModel> doInBackground(ListView... params) {
			// TODO Auto-generated method stub
			sqLite= SQLiteDatabase.openOrCreateDatabase(DBPATH, null);
			return Utils.queryData(sqLite,"" + zipCode);
		}

		@Override
		protected void onPostExecute(List<MobileModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			UtilsLog.d("====", "listsize==" + result.size());
			listData=result;
			initDialog();
			cd.show();
			LoadingD.hideDialog();
			//new AddTask().execute();
		}
	}
	//添加到通讯录
	private class AddTask extends  AsyncTask<String,String,String>{
		@Override
		protected String doInBackground(String... strings) {
			for(int i=0;i<listData.size();i++){
				Utils.AddContacts(mActivity,listData.get(i).getMobileNumber());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			LoadingD.hideDialog();
		}
	}

	//一键加粉
	private void requestDataJiaFen(){
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r", "api/user/jiafen");
		params.addQueryStringParameter("token", "" + spu.getToken());
		proAdd.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, cbAdd);
	}

//	private List<MobileModel> queryData(String s) {
//		List<MobileModel> listD=new ArrayList<MobileModel>();
//		String[] cloums={"id","MobileNumber","MobileArea","MobileType","AreaCode","PostCode"};
//		String[] args={s};
//		Cursor cursor = sqLite.query("mobile", cloums, "PostCode=?", args, null, null, null, null);
//		while (cursor.moveToNext()) {
//			//String rowid = cursor.getString(cursor.getColumnIndex("rowid"));
//			String id = cursor.getString(cursor.getColumnIndex("id"));
//			String MobileNumber=cursor.getString(cursor.getColumnIndex("MobileNumber"));
//			String MobileArea=cursor.getString(cursor.getColumnIndex("MobileArea"));
//			String MobileType=cursor.getString(cursor.getColumnIndex("MobileType"));
//			String AreaCode=cursor.getString(cursor.getColumnIndex("AreaCode"));
//			String PostCode=cursor.getString(cursor.getColumnIndex("PostCode"));
//			MobileModel ma=new MobileModel();
//			//ma.setRowid(rowid);
//			ma.setId(id);
//			ma.setMobileNumber(MobileNumber);
//			ma.setMobileArea(MobileArea);
//			ma.setMobileType(MobileType);
//			ma.setAreaCode(AreaCode);
//			ma.setPostCode(PostCode);
//			listD.add(ma);
//		}
//		sqLite.close();
//		return listD;
//	}

	private void initDialog(){
		cd=new CustomDialog(mActivity,"完成","已筛选到符合条件的"+listData.size()+"个手机号","取消","去导入");
		cd.setCancelOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cd.cancel();
				LoadingD.hideDialog();
			}
		});
		cd.setConfirmOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!addTask.isCancelled()){
					addTask.cancel(true);
					addTask=null;
				}
				addTask=new AddTask();
				addTask.execute();
				cd.cancel();
				//LoadingD.showDialog(mActivity);
			}
		});
	}

}
