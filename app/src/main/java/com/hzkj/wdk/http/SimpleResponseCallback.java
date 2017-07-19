package com.hzkj.wdk.http;

import android.content.Context;

import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.utils.LoadingD;

/**
 * Created by chenweiqi on 2017/4/28.
 */

public class SimpleResponseCallback extends IResponseCallback<String> {
    Context context;
    public SimpleResponseCallback(Context context) {
        this.context = context;
    }


    @Override
    public void onSuccess(String o) {
        System.out.println(o.toString());
        LoadingD.hideDialog();
    }


    @Override
    public void onFailure(ErrorModel errorModel) {
        System.out.println(errorModel.getMsg());
        LoadingD.hideDialog();

    }

    @Override
    public void onStart() {
        LoadingD.hideDialog();
        LoadingD.showDialog(context);


    }
}
