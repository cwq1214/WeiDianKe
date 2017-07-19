package com.hzkj.wdk.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hzkj.wdk.R;
import com.hzkj.wdk.model.UserModel;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.SharePreferenceUtil;
import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.utils.UtilsLog;

/**
 * 签到
 */
public class DialogSignText extends Dialog implements JiaFenConstants{

	private View mView,d_line,d_line1;
	private TextView contentTextView,contentTextView1;
	private Button confirmBtn,cancelBtn;
	private Context c;
	private LinearLayout ll_bottom;
	private SharePreferenceUtil spu;
	private EditText et_code;
	private OkClick okClick;
	public interface OkClick{
		public void onClick(int b);
	}

	public DialogSignText(Context context) {
		// TODO Auto-generated constructor stub
		super(context, R.style.dialog_round);
		c=context;
		spu=new SharePreferenceUtil(context);
		mView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.dialog_edittext, null);
		contentTextView = (TextView)mView.findViewById(R.id.customdialog_message);
		contentTextView1=(TextView)mView.findViewById(R.id.customdialog_message1);
		if(Utils.configModel!=null);
		contentTextView1.setText(Utils.configModel.getSign_auth_des());
		confirmBtn = (Button)mView.findViewById(R.id.customdialog_confirm);
		cancelBtn = (Button)mView.findViewById(R.id.customdialog_cancel);
		d_line=(View)mView.findViewById(R.id.d_line);
		ll_bottom=(LinearLayout)mView.findViewById(R.id.ll_bottom);
		d_line1=(View)mView.findViewById(R.id.d_line1);
		et_code=(EditText)mView.findViewById(R.id.et_code);

		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		confirmBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(Utils.configModel==null){
					Utils.toastShow(c,"数据验证失败,请刷新数据再试");
					return;
				}
				if(et_code.getText().toString().equals(Utils.configModel.getSign_auth_code())){
					okClick.onClick(0);
					dismiss();
					spu.setSignVersion(true);
				}else{
					Utils.toastShow(c,"验证码错误");
				}
			}
		});
		et_code.setFocusable(true);
	}

	public void setOkclick(OkClick listener){
		okClick=listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(mView);
	}
}
