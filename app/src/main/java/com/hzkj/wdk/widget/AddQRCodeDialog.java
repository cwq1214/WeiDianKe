package com.hzkj.wdk.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzkj.wdk.R;
import com.hzkj.wdk.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chenweiqi on 2017/5/12.
 */

public class AddQRCodeDialog extends AlertDialog {

    View mView;

    @BindView(R.id.save)
    TextView save;
    @BindView(R.id.add)
    TextView add;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.d_line)
    View dLine;
    @BindView(R.id.d_line2)
    View dLine2;

    public AddQRCodeDialog(Context context) {
        super(context, R.style.dialog_round);
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_add_qrcode, null);
        ButterKnife.bind(this, mView);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(mView);

        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = Utils.deviceWidth;
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);
    }

    public void canAddToContacts(boolean can) {
        if (can){
            dLine2.setVisibility(View.VISIBLE);
            add.setVisibility(View.VISIBLE);
        }else {
            dLine2.setVisibility(View.GONE);
            add.setVisibility(View.GONE);
        }
    }

    public void setOnSaveClick(View.OnClickListener onSaveClick){
        save.setOnClickListener(onSaveClick);
    }

    public void setOnAddClick(View.OnClickListener onAddClick){
        add.setOnClickListener(onAddClick);
    }

    @OnClick(R.id.cancel)
    public void onCancelClick(){
        dismiss();
    }
}
