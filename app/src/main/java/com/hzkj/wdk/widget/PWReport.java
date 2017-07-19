package com.hzkj.wdk.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.hzkj.wdk.R;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 拍照、选择照片
 * 
 * @author berton
 * 
 */
public class PWReport extends LinearLayout implements View.OnClickListener{

	private Context c;
	private PopupWindow aPopuwindow;
	private View showView;
	private TextView tv_1,tv_2,tv_cancle;
	public interface OKClick{
		public void okClick(String path, int type);
	}
	private OKClick okClick;
	/**
	 *
	 * @param context
	 * @param
	 * @param
	 * @param
	 *
	 *
	 */
	public PWReport(Context context) {
		super(context);
		c = context;
		showView = ((LayoutInflater) c
				.getSystemService(c.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.pw_report, null);
		tv_1=(TextView)showView.findViewById(R.id.tv_1);
		tv_1.setOnClickListener(this);
		tv_2=(TextView)showView.findViewById(R.id.tv_2);
		tv_2.setOnClickListener(this);
		tv_cancle=(TextView)showView.findViewById(R.id.tv_cancle);
		tv_cancle.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0==tv_1){// 分享
			aPopuwindow.dismiss();
			okClick.okClick("",1);
		}else if(arg0==tv_cancle){//取消
			okClick.okClick("",0);
			aPopuwindow.dismiss();
		}else if(arg0==tv_2){//购买
			okClick.okClick("",2);
			aPopuwindow.dismiss();
		}
	}

	//从底部浮出
	public void ShowPop(View v){
		aPopuwindow=new PopupWindow(showView,
				LayoutParams.FILL_PARENT,
				LayoutParams.MATCH_PARENT, true);
		aPopuwindow.setAnimationStyle(R.style.popwin_anim_style);
		// 使其聚集 ，要想监听菜单里控件的事件就必须要调用此方法
		aPopuwindow.setFocusable(true);
				// 设置允许在外点击消失
		aPopuwindow.setOutsideTouchable(false);
				// 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		aPopuwindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.prompt_popupwindow_bg));
				// 软键盘不会挡着popupwindow
		aPopuwindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
				// 设置菜单显示的位置
		aPopuwindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
	}
	
	public void setOkClick(OKClick click){
		this.okClick=click;
	}

}
