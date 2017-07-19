package com.hzkj.wdk.fra;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hzkj.wdk.R;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.http.SimpleResponseCallback;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.utils.Utils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tencent.weibo.sdk.android.network.ReqParam;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chenweiqi on 2017/5/8.
 */

public class GetSelfCodeFragment extends BaseFragment {


    View rootView;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.save)
    TextView save;
    @BindView(R.id.img)
    ImageView img;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_getselfcode, container, false);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        ButterKnife.bind(this, rootView);
        mActivity.FrameLayoutVisible(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
    }

    @OnClick(R.id.back)
    public void onBackClick(){
        mActivity.backFragment();
    }

    private void getData(){
        Log.e("url",SERVER_URL+"?r=api/Qrcode/getShareImg&token="+mActivity.spu.getToken());
        Glide.with(getContext()).load(SERVER_URL+"?r=api/Qrcode/getShareImg&token="+mActivity.spu.getToken()).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(img);
    }

    @OnClick(R.id.save)
    public void onSaveClick(){
        String path = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM+File.separator+"Camera"+File.separator;
        //获取内部存储状态
        String state = Environment.getExternalStorageState();
        //如果状态不是mounted，无法读写
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getContext(),"无法读写",Toast.LENGTH_SHORT).show();
            return;
        }
        try {

            img.setDrawingCacheEnabled(true);
            if (img.getDrawingCache()==null){
                return;
            }
            File file = new File(path +"/"+ mActivity.spu.getToken()+"_scode" + ".jpg");
            if (!file.exists()){
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file);
            img.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, out);
            img.setDrawingCacheEnabled(false);
            out.flush();
            out.close();
            Utils.toastShow(getContext(),"文件保存路径："+path +"/"+ mActivity.spu.getToken()+"_scode" + ".jpg");

            mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

        } catch (Exception e) {
            e.printStackTrace();

            return;
        }
    }
}
