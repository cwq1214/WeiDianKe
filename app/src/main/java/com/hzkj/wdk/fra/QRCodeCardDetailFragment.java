package com.hzkj.wdk.fra;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.hzkj.wdk.R;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.http.SimpleResponseCallback;
import com.hzkj.wdk.model.QRCodeListItemBean;
import com.hzkj.wdk.utils.Utils;
import com.lidroid.xutils.bitmap.download.Downloader;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tencent.weibo.sdk.android.network.ReqParam;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chenweiqi on 2017/5/2.
 */

public class QRCodeCardDetailFragment extends BaseFragment {


    View rootView;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.account)
    TextView account;
    @BindView(R.id.city)
    TextView city;
    @BindView(R.id.gender)
    TextView gender;
    @BindView(R.id.copy)
    TextView copy;
    @BindView(R.id.likes)
    TextView likes;
    @BindView(R.id.des)
    TextView des;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.save)
    LinearLayout save;
    @BindView(R.id.importData)
    LinearLayout importData;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.divider)
    View divider;

    int type;
    public QRCodeListItemBean.ItemBean bean;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_person_card_detail, container, false);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        System.out.println("bean null?"+bean==null?"null":"not null");
        if (type == 0) {
            title.setText("个人名片详情");
            //复制v信号

            city.setText(bean.userArea);
            gender.setText(bean.sex);
            account.setText(bean.wechatName);
        } else {
            copy.setVisibility(View.GONE);
            city.setVisibility(View.GONE);
            gender.setVisibility(View.GONE);
            account.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            importData.setVisibility(View.GONE);
            if (type==1)
                title.setText("v信群详情");
            if (type==2)
                title.setText("公众号详情");


        }
        likes.setText(bean.popularity);
        des.setText(bean.description);
        name.setText(bean.nickname);
        Glide.with(getContext()).load(bean.userImg).asBitmap().into(avatar);
        Glide.with(getContext()).load(bean.qrcode).asBitmap().into(img);


        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(bean.wechatName)){
                    return;
                }
                // 从API11开始android推荐使用android.content.ClipboardManager1
                // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(bean.wechatName);
                Toast.makeText(getContext(), "复制成功", Toast.LENGTH_LONG).show();
            }
        });
        //导入通讯录
        importData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(bean.mobile)){
                    return;
                }
                Utils.AddContacts(mActivity,bean.mobile);
                Toast.makeText(getContext(),"已导入",Toast.LENGTH_SHORT).show();
            }
        });
        //保存二维码
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String path = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM+File.separator+"Camera"+File.separator;
                //获取内部存储状态
                String state = Environment.getExternalStorageState();
                //如果状态不是mounted，无法读写
                if (TextUtils.isEmpty(bean.qrcode)){
                    return;
                }

                if (!state.equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(getContext(),"无法读写",Toast.LENGTH_SHORT).show();
                    return;
                }
                try {

                    img.setDrawingCacheEnabled(true);
                    if (img.getDrawingCache()==null){
                        return;
                    }
                    File file = new File(path +"/"+ bean.userId+"_code" + ".jpg");
                    if (!file.exists()){
                        file.createNewFile();
                    }
                    FileOutputStream out = new FileOutputStream(file);
                    img.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, out);
                    img.setDrawingCacheEnabled(false);
                    out.flush();
                    out.close();
                    Utils.toastShow(getContext(),"文件保存路径："+path + bean.userId+"_code" + ".jpg");
                    mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                } catch (Exception e) {
                    e.printStackTrace();

                    return;
                }


            }
        });
    }

    private void initView(){
    }

    @OnClick(R.id.back)
    public void onBackClick(){
        mActivity.backFragment();
    }



}
