package com.hzkj.wdk.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.hzkj.wdk.R;


/**
 * 加载dialog
 * @author admin
 *
 */
public class LoadingD {
	//private static ProgressBar progress;
	//static ImageView spaceshipImage;
	static int aminNum=1;
	public static Dialog myProgressDialog;
	public static Dialog showDialog(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);
		LinearLayout layout=(LinearLayout) v.findViewById(R.id.dialog_view);
		//spaceshipImage=(ImageView) v.findViewById(R.id.img);
		//progress=(ProgressBar)v.findViewById(R.id.progress);
		myProgressDialog=new Dialog(context, R.style.loading_dialog);

		myProgressDialog.setCancelable(true);// 可以用“返回键”取消
		myProgressDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
		myProgressDialog.setCanceledOnTouchOutside(false);
		myProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
			}
		});
		myProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
			}
		});
		myProgressDialog.dismiss();
		myProgressDialog.show();
		return myProgressDialog;
	}
	public static void hideDialog() {
		try {
			if (myProgressDialog != null) {
				myProgressDialog.cancel();
				myProgressDialog.dismiss();
				myProgressDialog.hide();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
