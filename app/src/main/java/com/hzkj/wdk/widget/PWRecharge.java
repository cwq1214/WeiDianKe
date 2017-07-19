package com.hzkj.wdk.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.VipModel;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.SharePreferenceUtil;
import com.hzkj.wdk.utils.Utils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.hzkj.wdk.MyApplication;
import com.hzkj.wdk.R;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.UtilsLog;

import org.json.JSONObject;

/**
 * 充值
 */
public class PWRecharge extends LinearLayout implements View.OnClickListener,OnItemClickListener,JiaFenConstants{

	private View mView;
	private Context c;
	private SharePreferenceUtil spu;
	private PopupWindow window;
	private OnSuccess oSuccess;
	private ImageView iv_close;
	private LinearLayout ll1,ll2,ll3,ll4,ll5;
	private ListView listview;
	private SimpleProtocol pro;
	private IResponseCallback<String> cb;
	private TextView tv_zhifubao,tv_weixin;
	private String id="";
	private String payType="";
	private MainActivity act;
	private String name;


	public interface OnSuccess{
		public void onClick(String type,String name,String price,String sn);
	}

	public PWRecharge(MainActivity context,MyApplication application) {
		// TODO Auto-generated constructor stub
		super(context);
		act=context;
		c=context;
		spu=new SharePreferenceUtil(context);
		if(Utils.userModel!=null)
		initData();
	}

	@Override
	public void onClick(View v) {
		if(v==iv_close){
			window.dismiss();
			listview.setVisibility(View.VISIBLE);
			tv_weixin.setVisibility(View.GONE);
			tv_zhifubao.setVisibility(View.GONE);
		}else if(v==tv_weixin){
			payType="2";
			pay(id,"2");
		}else if(v==tv_zhifubao){
			payType="1";
			pay(id,"1");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

	}

	private void pay(String id,String pay){
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("r", "api/User/openMember");
		params.addQueryStringParameter("token", Utils.userModel.getToken());
		params.addQueryStringParameter("vip_id", id);
		params.addQueryStringParameter("payment", pay);
		pro.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, cb);
	}

	public void showPW(View parent){
		if (window == null) {
			mView=View.inflate(c,R.layout.pw_recharge,null);
			iv_close=(ImageView)mView.findViewById(R.id.iv_close);
			iv_close.setOnClickListener(this);
			tv_weixin=(TextView)mView.findViewById(R.id.tv_weixin);
			tv_weixin.setOnClickListener(this);
			tv_zhifubao=(TextView)mView.findViewById(R.id.tv_zhifubao);
			tv_zhifubao.setOnClickListener(this);
			listview=(ListView)mView.findViewById(R.id.listview);
			listview.setOnItemClickListener(this);

			MyAdapter adapter=new MyAdapter();
			listview.setAdapter(adapter);
			window = new PopupWindow(mView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}
		//动画
		window.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar7));
		window.setFocusable(true);
		window.update();
		window.showAtLocation(parent, Gravity.CENTER, 0, 0);
	}

	private void initData(){
		pro=new SimpleProtocol(c);
		cb=new IResponseCallback<String>() {
			@Override
			public void onSuccess(String s) {
				window.dismiss();
				try {
					JSONObject obj=new JSONObject(s);
					String pay_sn=obj.getString("pay_sn");
					String pay_value=obj.getString("pay_value");
					listview.setVisibility(View.VISIBLE);
					tv_weixin.setVisibility(View.GONE);
					tv_zhifubao.setVisibility(View.GONE);
					oSuccess.onClick(payType, name, pay_value, pay_sn);
					UtilsLog.d("====", "====" + pay_sn + "==" + pay_value);

				}catch (Exception e){

				}
				LoadingD.hideDialog();
			}

			@Override
			public void onFailure(ErrorModel errorModel) {
				LoadingD.hideDialog();
				window.dismiss();
			}

			@Override
			public void onStart() {
				LoadingD.showDialog(act);
			}
		};
	}

	public void setSuccessClick(OnSuccess os){
		this.oSuccess=os;
	}

	class ViewHolder{
		TextView tv_content,tv_type;
		LinearLayout ll;
	}

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return Utils.userModel.getListViPs()==null?0:Utils.userModel.getListViPs().size();
		}

		@Override
		public Object getItem(int i) {
			return Utils.userModel.getListViPs().get(i);
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
				v=View.inflate(c,R.layout.item_vip,null);
				holder.tv_content=(TextView)v.findViewById(R.id.tv_content);
				holder.tv_type=(TextView)v.findViewById(R.id.tv_type);
				holder.ll=(LinearLayout)v.findViewById(R.id.ll);
				v.setTag(holder);
			}else{
				holder=(ViewHolder)v.getTag();
			}
			final VipModel model=Utils.userModel.getListViPs().get(i);
			holder.tv_content.setText(model.getFunction_des());
			if(i==0||i==1||i==2){
				holder.tv_type.setTextColor(getResources().getColor(R.color.black));
				holder.tv_content.setTextColor(getResources().getColor(R.color.red));
			}else{
				holder.tv_type.setTextColor(getResources().getColor(R.color.blue));
				holder.tv_content.setTextColor(getResources().getColor(R.color.green));
			}
			if(i==0)
				holder.ll.setBackgroundResource(R.drawable.frame_fine_green);

			holder.tv_type.setText(model.getValue_des());
			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					name=model.getValue_des();
					id=model.getId();
					listview.setVisibility(View.GONE);
					tv_weixin.setVisibility(View.VISIBLE);
					tv_zhifubao.setVisibility(View.VISIBLE);
				}
			});
			return v;
		}
	}

}
