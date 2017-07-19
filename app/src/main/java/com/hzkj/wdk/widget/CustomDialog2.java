package com.hzkj.wdk.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzkj.wdk.R;


public class CustomDialog2 extends AlertDialog {

	private View mView,d_line,d_line1;
	private TextView contentTextView,contentTextView1;
	private Button confirmBtn,cancelBtn;
	private Context c;
	private LinearLayout ll_bottom;

	public CustomDialog2(Context context) {
		// TODO Auto-generated constructor stub
		super(context, R.style.dialog_round);
		c=context;
		mView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.dialog_custom2, null);
		contentTextView = (TextView)mView.findViewById(R.id.customdialog_message);
		contentTextView1=(TextView)mView.findViewById(R.id.customdialog_message1);
		confirmBtn = (Button)mView.findViewById(R.id.customdialog_confirm);
		cancelBtn = (Button)mView.findViewById(R.id.customdialog_cancel);
		d_line=(View)mView.findViewById(R.id.d_line);
		ll_bottom=(LinearLayout)mView.findViewById(R.id.ll_bottom);
		d_line1=(View)mView.findViewById(R.id.d_line1);


	}

	public void showCD(String text){
		contentTextView1.setText(text);
		this.show();
	}

	public void setConfirmOnClickListener(View.OnClickListener listener){
		confirmBtn.setOnClickListener(listener);
	}

	public void setCancelOnClickListener(View.OnClickListener listener){
		cancelBtn.setOnClickListener(listener);
	}
	

	public void setBtnColor(int color){
		cancelBtn.setTextColor(c.getResources().getColor(color));
	}

	public void setTitleText(String text){
		contentTextView.setText(text);
	}

	public void setMessage1(String msg){
		contentTextView1.setText(msg);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(mView);
	}
}
