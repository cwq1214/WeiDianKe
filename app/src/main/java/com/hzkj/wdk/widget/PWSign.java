package com.hzkj.wdk.widget;

import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.SharePreferenceUtil;
import com.hzkj.wdk.R;

/**
 * 签到提示
 */
public class PWSign extends LinearLayout implements View.OnClickListener,OnItemClickListener,JiaFenConstants {

	private View mView;
	private Context c;
	private SharePreferenceUtil spu;
	private PopupWindow window;
	private OnSuccess oSuccess;
	private ImageView iv_close;
	private SimpleProtocol pro;
	private IResponseCallback<String> cb;
	private TextView tv_money,tv_days,tv_prompt1,tv_prompt2,tv_prompt3;
	private String id="";
	private String payType="";
	private MainActivity act;
	private String name,moneys,days;
	private LinearLayout ll_open;

	public interface OnSuccess{
		public void onClick(String str);
	}

	public PWSign(MainActivity context,String money,String day) {
		// TODO Auto-generated constructor stub
		super(context);
		act=context;
		c=context;
		spu=new SharePreferenceUtil(context);
		moneys=money;
		days=day;

	}

	@Override
	public void onClick(View v) {
		if(v==iv_close){
			window.dismiss();
		}else if(v==ll_open){//开通
			window.dismiss();
			if(oSuccess!=null)
				oSuccess.onClick("");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

	}

	public void showPW(View parent){
		if (window == null) {
			mView=View.inflate(c,R.layout.pw_sign,null);
			iv_close=(ImageView)mView.findViewById(R.id.iv_close);
			iv_close.setOnClickListener(this);
			tv_money=(TextView)mView.findViewById(R.id.tv_money);
			tv_days=(TextView)mView.findViewById(R.id.tv_days);
			tv_money.setText(Html.fromHtml(String.format("<font color='#E2333C' >%s</font>元", "+"+moneys)));
			tv_days.setText(Html.fromHtml(String.format("已签到<font color='#4EB6E2' >%s</font>天，", ""+days)));
			tv_prompt1=(TextView)mView.findViewById(R.id.tv_prompt1);
			tv_prompt2=(TextView)mView.findViewById(R.id.tv_prompt2);
			tv_prompt3=(TextView)mView.findViewById(R.id.tv_prompt3);
			tv_prompt1.setText(Html.fromHtml(String.format("1、累计签到<font color='#E2333C' >%s</font>天，送<font color='#E2333C' >%s</font>个加粉名额", "5","50")));
			tv_prompt2.setText(Html.fromHtml(String.format("2、累计签到<font color='#E2333C' >%s</font>天，送<font color='#E2333C' >%s</font>个加粉名额", "15","200")));
			tv_prompt3.setText(Html.fromHtml(String.format("3、签到<font color='#E2333C' >%s</font>天，送钻石会员<font color='#E2333C' >%s</font>天", "30","1")));
			ll_open=(LinearLayout)mView.findViewById(R.id.ll_open);
			ll_open.setOnClickListener(this);
			window = new PopupWindow(mView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}
		//动画
		window.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar7));
		window.setFocusable(true);
		window.update();
		window.showAtLocation(parent, Gravity.CENTER, 0, 0);
	}

	public void setSuccessClick(OnSuccess os){
		this.oSuccess=os;
	}

}
