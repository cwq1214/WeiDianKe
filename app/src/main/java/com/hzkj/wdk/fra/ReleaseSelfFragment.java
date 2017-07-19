package com.hzkj.wdk.fra;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzkj.wdk.R;
import com.hzkj.wdk.act.SelCityAct;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.http.SimpleResponseCallback;
import com.hzkj.wdk.model.CardBean;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.QRCodeListItemBean;
import com.hzkj.wdk.model.UserInfo;
import com.hzkj.wdk.utils.DensityUtil;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.SharePreferenceUtil;
import com.hzkj.wdk.utils.UpLoadImageUtil;
import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.widget.CustomDialog;
import com.hzkj.wdk.widget.ShareDialog;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by chenweiqi on 2017/5/2.
 */

public class ReleaseSelfFragment extends BaseFragment {

    @BindView(R.id.selImg)
    ImageView selImg;
    @BindView(R.id.layout_selImg)
    LinearLayout layoutSelImg;
    @BindView(R.id.input_weixinAccount)
    EditText inputWeixinAccount;
    @BindView(R.id.input_name)
    EditText inputName;
    @BindView(R.id.input_des)
    EditText inputDes;
    @BindView(R.id.male)
    RadioButton male;
    @BindView(R.id.female)
    RadioButton female;
    @BindView(R.id.gender)
    RadioGroup gender;
    @BindView(R.id.jifen)
    TextView jifen;
    @BindView(R.id.huoqujifen)
    TextView huoqujifen;
    @BindView(R.id.cb_jifen)
    CheckBox cbJifen;
    @BindView(R.id.layout_jifenPay)
    LinearLayout layoutJifenPay;
    @BindView(R.id.yue)
    TextView yue;
    @BindView(R.id.chongzhi)
    TextView chongzhi;
    @BindView(R.id.cb_yue)
    CheckBox cbYue;
    @BindView(R.id.layout_yuePay)
    LinearLayout layoutYuePay;
    @BindView(R.id.payItem)
    LinearLayout payItem;
    @BindView(R.id.unPayItem)
    TextView unPayItem;
    @BindView(R.id.input_city)
    TextView inputCity;
    @BindView(R.id.done)
    TextView done;


    public ReleaseQRCodeFragment releaseQRCodeFragment;

    public String userScore = "0";
    public String qrcodeRemains = "0";
    public String payMoney = "0";
    public String payScore = "0";
    public String pay;


    View rootView;

    Uri localImgUri;
    String remotePath;
    public boolean isReleaseSelf;
    String localImagePath;

    public String city;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView==null) {
            rootView = inflater.inflate(R.layout.fragment_tab_release_self_qrcode, container, false);
            ButterKnife.bind(this, rootView);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chongzhi.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        huoqujifen.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        if (isReleaseSelf) {
            getSelfData();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
//        getSelfData();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //需要放在onResume的方法放在该处执行
            getData();
        } else {
            //界面不可见的时候执行的方法
        }
    }

    @OnClick(R.id.huoqujifen)
    public void onHuoQuJiFenClick(){
        final CustomDialog cd=new CustomDialog(mActivity,"如何获得积分","任意添加一个好友获得积分\n通过每天签到获取积分\n通过邀请好友,分享获取积分","","确定");
        cd.setCancelOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cd.cancel();
            }
        });
        cd.setConfirmOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cd.cancel();
                new ShareDialog(mActivity).show();

            }
        });
        cd.show();
    }

    @OnClick(R.id.input_city)
    public void onClickClick(){
//        SelCityFragment fragment = new SelCityFragment();
//        fragment.cityCallback = new SelCityFragment.SelCityCallback() {
//            @Override
//            public void sel(final String result) {
////                mActivity.runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
//                        city = result;
//                        inputCity.setText(city);
////                    }
////                });
//
//            }
//        };
//        mActivity.changeFragment(fragment,false);
        Intent intent = new Intent(getContext(), SelCityAct.class);
        startActivityForResult(intent,33);
    }

    @OnClick(R.id.chongzhi)
    public void onChongZhiClick() {
        mActivity.backFragment();
        FraPaymentType paymentType = new FraPaymentType();
        paymentType.isRecharge = true;
        mActivity.changeFragment(paymentType, false);

    }

    @OnClick(R.id.layout_selImg)
    public void selImgClick() {
        Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//使用以上这种模式，并添加以上两句
                /* 取得相片后返回本画面 */
        startActivityForResult(intent, 88);
    }

    @OnClick(R.id.layout_yuePay)
    public void onPayMoneyClick() {
        cbYue.setChecked(true);
        cbJifen.setChecked(false);
    }

    @OnClick(R.id.layout_jifenPay)
    public void onPayScoreClick() {
        cbYue.setChecked(false);
        cbJifen.setChecked(true);
    }


    public String getInputWeiXinAccount() {
        return inputWeixinAccount.getText().toString();
    }


    public String getInputDes() {
        return inputDes.getText().toString();
    }

    public String getInputCity() {
        return inputCity.getText().toString();
    }

    public String getGender() {
        if (male.isChecked())
            return "男";
        if (female.isChecked()){
            return "女";
        }
        return null;
//        return ((RadioButton) gender.findViewById(gender.getCheckedRadioButtonId())).getText().toString();
    }


    public String getPayWay() {
        if (cbYue.isChecked())
            return "yue";
        if (cbJifen.isChecked())
            return "jifen";
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 88 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            localImgUri = uri;
            Glide.with(this).load(uri).downloadOnly(new SimpleTarget<File>() {
                @Override
                public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                    localImagePath = resource.getPath();
                }
            });
            Glide.with(this).load(uri).bitmapTransform(new CenterCrop(getContext()),new RoundedCornersTransformation(getContext(), DensityUtil.dip2px(getContext(),5),0)).into(selImg);


        }

        if (requestCode == 33 && resultCode == Activity.RESULT_OK){
            inputCity.setText(data.getStringExtra("city"));
        }
    }


    void getData() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("r", "api/qrcode/createInfo");
        params.addQueryStringParameter("token", "" + mActivity.spu.getToken());
        params.addQueryStringParameter("type", "" + 1);

        new SimpleProtocol(getContext()).getData(HttpRequest.HttpMethod.GET, SERVER_URL, params, new SimpleResponseCallback(mActivity) {
            @Override
            public void onSuccess(String o) {
                super.onSuccess(o);
                try {
                    JSONObject result = new JSONObject(o);

                    pay = result.getString("pay");//是否需要积分/现金，0代表不需要，1代表需要

                    userScore = result.getString("userScore");//用户现有积分
                    qrcodeRemains = result.getString("qrcodeRemains");//用户二维码平台账户余额
                    payMoney = result.getString("payMoney");//应付现金
                    payScore = result.getString("payScore");//应付积分

                    if (pay.equals("0")) {
                        payItem.setVisibility(View.GONE);
                        unPayItem.setVisibility(View.VISIBLE);
                    } else {
                        payItem.setVisibility(View.VISIBLE);
                        unPayItem.setVisibility(View.GONE);

                        jifen.setText("积分发布，需" + payScore + "积分，剩余" + userScore + "积分");
                        yue.setText("付费发布，需" + payMoney + "元，剩余" + qrcodeRemains + "元  ");
                    }
                    if (!TextUtils.isEmpty(city)){
                        inputCity.setText(city);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                super.onFailure(errorModel);
            }
        });
    }

    void getSelfData() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("r", "api/qrcode/getSelfPerson");
        params.addQueryStringParameter("token", mActivity.spu.getToken());
        new SimpleProtocol(getContext()).getData(HttpRequest.HttpMethod.GET, SERVER_URL, params, new SimpleResponseCallback(mActivity) {
            @Override
            public void onSuccess(String o) {
                super.onSuccess(o);

                    CardBean cardBean = new Gson().fromJson(o, new TypeToken<CardBean>() {
                    }.getType());

                    if (cardBean.haveCreate){
                        inputDes.setText(cardBean.description);
                        inputWeixinAccount.setText(cardBean.wechatName);
                        inputCity.setText(cardBean.userArea);
                        if (cardBean.sex.equals("男")) {
                            male.setChecked(true);
                        } else if (cardBean.sex.equals("女")) {
                            female.setChecked(true);
                        }
                        remotePath = cardBean.qrcode;
                        Glide.with(getContext()).load(cardBean.qrcode).bitmapTransform(new CenterCrop(getContext()),new RoundedCornersTransformation(getContext(), DensityUtil.dip2px(getContext(),5),0)).into(selImg);
                        done.setText("更新");

                    }else {
                        done.setText("确认发布");

                    }

            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                super.onFailure(errorModel);
                done.setText("确认发布");
            }
        });
    }

    public String getRemotePath() {
        return remotePath;
    }


    @OnClick(R.id.done)
    public void onDoneClick(){
        Fragment fragment = this;
        final RequestParams params ;
        String imgPath;
        Uri imgUri;
        String localPath=null;
        String imgName=null;
            params = new RequestParams();
//            String name = ((ReleaseSelfFragment) fragment).getInputName();
            String des = ((ReleaseSelfFragment) fragment).getInputDes();
            String account = ((ReleaseSelfFragment) fragment).getInputWeiXinAccount();
            String gender = ((ReleaseSelfFragment) fragment).getGender();
            String city = ((ReleaseSelfFragment) fragment).getInputCity();

            if(TextUtils.isEmpty(account)){
                Utils.toastShow(getContext(),"v信号不能为空");
                return;
            }
            if (TextUtils.isEmpty(city)){
                Utils.toastShow(getContext(),"城市不能为空");
                return;
            }

            params.addQueryStringParameter("r","api/qrcode/updateInfo");
            params.addQueryStringParameter("token",mActivity.spu.getToken());
            params.addQueryStringParameter("type",1+"");
            params.addQueryStringParameter("description",des);
            params.addQueryStringParameter("wechatName",account);
            params.addQueryStringParameter("sex",gender);
            params.addQueryStringParameter("userArea",city);
            if (((ReleaseSelfFragment) fragment).getPayWay()==null){
                Utils.toastShow(getContext(),"请选择支付方式");
                return;
            }
            if (((ReleaseSelfFragment) fragment).getPayWay()==null){
                Utils.toastShow(getContext(),"请选择支付方式");
                return;
            }
            if (((ReleaseSelfFragment) fragment).getPayWay().equals("jifen")) {
                params.addQueryStringParameter("payType", "" + 1);
            }
            if (((ReleaseSelfFragment) fragment).getPayWay().equals("yue")) {
                params.addQueryStringParameter("payType", "" + 2);
            }
            if (localImagePath == null){
                imgPath = ((ReleaseSelfFragment) fragment).getRemotePath();
                if (TextUtils.isEmpty(imgPath)){
                    Utils.toastShow(getContext(),"图片不能为空");
                    return;
                }
                params.addQueryStringParameter("qrcode",imgPath);
            }else {
                localPath = localImagePath;
                imgName = mActivity.spu.getToken()+"_"+(System.currentTimeMillis()/1000);
                params.addQueryStringParameter("qrcode",UpLoadImageUtil.domainName+imgName);

            }



        if (!TextUtils.isEmpty(localPath)&&!TextUtils.isEmpty(imgName)) {
            Utils.toastShow(mActivity,"开始上传");
            LoadingD.hideDialog();
            LoadingD.showDialog(getContext()).setCancelable(false);
            UpLoadImageUtil.uploadImage(getContext(), localPath, imgName, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    if (info.isOK()) {
                        submitData(params);
                    } else {
                        Utils.toastShow(getContext(), "图片上传失败");
                    }
                    LoadingD.hideDialog();
                }
            });
        }else {
            submitData(params);
        }



    }
    private void submitData(RequestParams params){
        new SimpleProtocol(getContext()).getData(HttpRequest.HttpMethod.GET,SERVER_URL,params,new SimpleResponseCallback(mActivity){
            @Override
            public void onSuccess(String o) {
                super.onSuccess(o);
                if (o.equals("修改成功")){
                    mActivity.backFragment();
                }
                Utils.toastShow(mActivity,o);
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                super.onFailure(errorModel);
                Utils.toastShow(mActivity,errorModel.getMsg());

            }
        });
    }
}
