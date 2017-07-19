package com.hzkj.wdk.fra;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hzkj.wdk.R;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.http.SimpleResponseCallback;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.utils.DensityUtil;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.SharePreferenceUtil;
import com.hzkj.wdk.utils.UpLoadImageUtil;
import com.hzkj.wdk.utils.Utils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by chenweiqi on 2017/4/28.
 */

public class ModifyUserNameAvatarFragment extends BaseFragment implements View.OnTouchListener {


    View rootView;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.img_selImg)
    ImageView imgSelImg;
    @BindView(R.id.layout_selImg)
    LinearLayout layoutSelImg;
    @BindView(R.id.input_nickName)
    EditText inputNickName;
    @BindView(R.id.done)
    TextView done;

    String localImagePath;
    String nickName;
    String remotePath;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_modify_username_avatar, null);
        ButterKnife.bind(this, rootView);
        rootView.setOnTouchListener(this);
        mActivity.FrameLayoutVisible(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inputNickName.setText(mActivity.spu.getNickName());
        remotePath = mActivity.spu.getAvatar();
        Glide.with(getContext()).load(remotePath).bitmapTransform(new CenterCrop(getContext()),new RoundedCornersTransformation(getContext(), DensityUtil.dip2px(getContext(),5),0)).into(imgSelImg);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

    @OnClick(R.id.layout_selImg)
    public void onSelImageClick(){
        Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//使用以上这种模式，并添加以上两句

                /* 取得相片后返回本画面 */
        startActivityForResult(intent, 88);
    }

    @OnClick(R.id.done)
    public void onDoneClick(){
        if (TextUtils.isEmpty(inputNickName.getText().toString())){
            Utils.toastShow(getContext(), "请输入昵称");
            return;
        }
        if (TextUtils.isEmpty(localImagePath)&&TextUtils.isEmpty(remotePath)){
            Utils.toastShow(getContext(),"请上传头像");
            return;
        }
        nickName = inputNickName.getText().toString();
        if (TextUtils.isEmpty(localImagePath)){
            sendData(remotePath,nickName);
        }else {
            uploadImage(localImagePath, new SharePreferenceUtil(getContext()).getToken() + "_" + (System.currentTimeMillis() / 1000));
        }
    }

    @OnClick(R.id.back)
    public void onBackClick(){
        mActivity.backFragment();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==88&&resultCode== Activity.RESULT_OK){
            Uri uri = data.getData();
//            localImagePath = UpLoadImageUtil.getRealFilePath(getContext(),uri);
//            if (TextUtils.isEmpty(localImagePath)){
                Glide.with(this).load(uri).downloadOnly(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                        localImagePath = resource.getPath();
                    }
                });
//            }
            Glide.with(this).load(uri).bitmapTransform(new CenterCrop(getContext()),new RoundedCornersTransformation(getContext(), DensityUtil.dip2px(getContext(),5),0)).into(imgSelImg);

        }
    }

    private void uploadImage(String path, final String remotePath){
        this.remotePath = remotePath;
        LoadingD.hideDialog();
        LoadingD.showDialog(mActivity);
        UpLoadImageUtil.uploadImage(getContext(), path,remotePath, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()){
                    Utils.toastShow(getContext(),"图片上传成功");
                    sendData(remotePath,nickName);
                }else {
                    System.out.println(response);
                    Utils.toastShow(getContext(),"图片上传失败");
                }
                LoadingD.hideDialog();
            }
        });
    }

    private void sendData(String remotePath,String nickName){
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("r","api/qrcode/updateUserInfo");
        if (!TextUtils.isEmpty(remotePath)) {
            if (remotePath.startsWith("http://")) {
                params.addQueryStringParameter("userImg", remotePath);
            } else {
                params.addQueryStringParameter("userImg", UpLoadImageUtil.domainName + remotePath);
            }
        }else {
            params.addQueryStringParameter("userImg","");

        }
        params.addQueryStringParameter("nickname",nickName);
        params.addQueryStringParameter("token",mActivity.spu.getToken());
        new SimpleProtocol(getContext()).getData(HttpRequest.HttpMethod.GET,SERVER_URL,params,new SimpleResponseCallback(mActivity){
            @Override
            public void onSuccess(String o) {
                super.onSuccess(o);
                if (o.equals("修改成功")){
                    mActivity.backFragment();
                }else {
                    Utils.toastShow(getContext(),"修改失败");
                }
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                super.onFailure(errorModel);
                Utils.toastShow(getContext(),"修改失败");
            }
        });
    }
}
