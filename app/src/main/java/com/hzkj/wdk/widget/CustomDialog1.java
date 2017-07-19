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


public class CustomDialog1 extends AlertDialog {

	private View mView,d_line,d_line1;
	private TextView contentTextView1,contentTextView2;
	private Context c;
	private LinearLayout ll_bottom;

	public CustomDialog1(Context context) {
		// TODO Auto-generated constructor stub
		super(context, R.style.dialog_round);
		c=context;
		mView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.dialog_custom1, null);
		contentTextView1 = (TextView)mView.findViewById(R.id.customdialog_message1);
		contentTextView2=(TextView)mView.findViewById(R.id.customdialog_message2);
	}

	public void setClick1(View.OnClickListener listener){
		contentTextView1.setOnClickListener(listener);
	}

	public void setClick2(View.OnClickListener listener){
		contentTextView2.setOnClickListener(listener);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(mView);
	}
}
