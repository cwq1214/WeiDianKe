package com.hzkj.wdk.fra;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzkj.wdk.R;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.UserInfo;
import com.hzkj.wdk.model.VipModel;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.SharePreferenceUtil;
import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.widget.ShareDialog;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by chenweiqi on 2017/4/28.
 */

public class PersonalCenterFra extends BaseFragment implements View.OnClickListener, View.OnTouchListener {
    SimpleProtocol simpleProtocol;

    SharePreferenceUtil spu;
    View rootView;

    UserInfo userInfo;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.img_avatar)
    ImageView imgAvatar;
    @BindView(R.id.text_phoneNumber)
    TextView textPhoneNumber;
    @BindView(R.id.text_myJiFen)
    TextView textMyJiFen;
    @BindView(R.id.layout_jiFenDuiHuan)
    LinearLayout layoutJiFenDuiHuan;
    @BindView(R.id.text_myYuE)
    TextView textMyYuE;
    @BindView(R.id.layout_YuETiXian)
    LinearLayout layoutYuETiXian;
    @BindView(R.id.text_vip)
    TextView textVip;
    @BindView(R.id.text_xufeiVip)
    TextView textXufeiVip;
    @BindView(R.id.text_ziZuanDays)
    TextView textZiZuanDays;
    @BindView(R.id.text_xuFeiZiZuan)
    TextView textXuFeiZiZuan;
    @BindView(R.id.layout_geRenMingPian)
    LinearLayout layoutGeRenMingPian;
    @BindView(R.id.layout_WeiXinQun)
    LinearLayout layoutWeiXinQun;
    @BindView(R.id.layout_GongZhongHao)
    LinearLayout layoutGongZhongHao;
    @BindView(R.id.layout_XinShouZhiNan)
    LinearLayout layoutXinShouZhiNan;
    @BindView(R.id.layout_ZhuanJiFen)
    LinearLayout layoutZhuanJiFen;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.layout_title)
    RelativeLayout layoutTitle;


    boolean showTitle = true;
    public QRCodePlFFragment qrCodePlFFragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_personal_center, null);
        // 拦截触摸事件，防止泄露下去
        rootView.setOnTouchListener(this);

        mActivity.FrameLayoutVisible(true);
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    private void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.backFragment();
            }
        });
    }

    @OnClick
    public void onBackClick() {
        mActivity.backFragment();
    }


    public void showTitle(boolean show){
        showTitle = show;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutTitle.setVisibility(showTitle?View.VISIBLE:View.GONE);

        view.setOnTouchListener(this);

        setListener();
        spu = mActivity.spu;

//        title.setText(spu.getPersonalCenterBtnName());


    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("r", "api/qrcode/getUserInfo");
        params.addQueryStringParameter("token", "" + spu.getToken());
        simpleProtocol = new SimpleProtocol(mActivity);
        simpleProtocol.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, new IResponseCallback<String>() {
            @Override
            public void onSuccess(String s) {
                LoadingD.hideDialog();
                System.out.println(s);
                UserInfo baseJson = new Gson().fromJson(s, new TypeToken<UserInfo>() {
                }.getType());
//                userInfo = baseJson.result;
                userInfo = baseJson;

                showInfo(userInfo);

                spu.setAvatar(userInfo.userImg);
                spu.setNickName(userInfo.nickname);
                spu.setShareLink(userInfo.share_link);
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                LoadingD.hideDialog();
                System.out.println(errorModel.getMsg());
            }

            @Override
            public void onStart() {
                LoadingD.hideDialog();
                LoadingD.showDialog(mActivity);
            }
        });

    }

    private void showInfo(UserInfo userInfo) {
        Glide.with(this).load(userInfo.userImg).asBitmap().transform(new CenterCrop(getContext()),new CropCircleTransformation(getContext())).into(imgAvatar);
//        Glide.with(this).load("https://imgsa.baidu.com/baike/c0%3Dbaike92%2C5%2C5%2C92%2C30/sign=5a659521b1de9c82b268f1dd0de8eb6f/f9198618367adab482d06a5b89d4b31c8701e4a2.jpg").asBitmap().transform(new CropCircleTransformation(getContext())).into(imgAvatar);
//        if (!TextUtils.isEmpty(userInfo.mobile)) {
        textPhoneNumber.setText(userInfo.mobile);
//        }else {
//            textPhoneNumber.setText("未设置昵称，点击设置");
//        }
        textMyYuE.setText("我的余额："+userInfo.remains);
        textMyJiFen.setText("我的积分："+userInfo.score);

        String day = "0";
        if (!TextUtils.isEmpty(userInfo.expiration)){
            float mis = Float.valueOf(userInfo.expiration)-(System.currentTimeMillis()/1000);
            if (mis<0){
                day = "0";
            }else {
                day = new DecimalFormat("0").format((mis / (24 * 60 * 60))-0.5) + "";
            }
        }

        if (userInfo.vip_types.equals("1")){
            textVip.setText("黄金会员：剩余"+day+"天");
        }else if (userInfo.vip_types.equals("2")){
            textVip.setText("钻石会员：剩余"+day+"天");
        }else {
            textVip.setText("未开通会员");
        }



        int qrcode_type = Integer.parseInt(userInfo.qrcode_type);
        String vipType = null;
        String date = "";
        if (qrcode_type == 0) {
            vipType = "未开通会员";
        } else {
            vipType = qrcode_type == 1 ? "VIP" : "紫钻";
            date = "：剩余" + userInfo.qrcode_expiration + "天";
        }
        textZiZuanDays.setText(vipType + date);
    }

    @OnClick({R.id.text_xufeiVip, R.id.text_xuFeiZiZuan,R.id.layout_geRenMingPian, R.id.layout_WeiXinQun, R.id.layout_GongZhongHao, R.id.layout_ZhuanJiFen, R.id.layout_XinShouZhiNan, R.id.layout_jiFenDuiHuan, R.id.layout_YuETiXian, R.id.img_avatar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_geRenMingPian:
                ReleaseQRCodeFragment releaseSelfCard = new ReleaseQRCodeFragment();
                releaseSelfCard.isSelfRelease = true;
                releaseSelfCard.startPageIndex = 0;
                mActivity.changeFragment(releaseSelfCard, false);
                break;
            case R.id.layout_WeiXinQun:
                ReleaseQRCodeFragment releaseWeiXin = new ReleaseQRCodeFragment();
                releaseWeiXin.isSelfRelease = true;
                releaseWeiXin.startPageIndex = 1;
                mActivity.changeFragment(releaseWeiXin, false);
                break;
            case R.id.layout_GongZhongHao:
                ReleaseQRCodeFragment releaseGongZongHao = new ReleaseQRCodeFragment();
                releaseGongZongHao.isSelfRelease = true;
                releaseGongZongHao.startPageIndex = 2;
                mActivity.changeFragment(releaseGongZongHao, false);
                break;
            case R.id.layout_ZhuanJiFen:
                new ShareDialog(mActivity).show();
                break;
            case R.id.layout_XinShouZhiNan:
                mActivity.backToHome();
                mActivity.changeHomeSel(3);
                break;
            case R.id.layout_jiFenDuiHuan:
                openQRCodePlFragment(2);
                break;
            case R.id.layout_YuETiXian:
                TiXianFra tf = new TiXianFra();
                Bundle b = new Bundle();
                b.putString("price", userInfo.remains);
                tf.setArguments(b);
                mActivity.changeFragment(tf, false);
                break;
            case R.id.img_avatar:
                mActivity.changeFragment(new ModifyUserNameAvatarFragment(), false);
                break;
            case R.id.text_xufeiVip:
                mActivity.changeFragment(new MemberTypeFra(),false);
                break;
            case R.id.text_xuFeiZiZuan:
                openQRCodePlFragment(1);
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }


    private void openQRCodePlFragment(int startPage) {
        List<Fragment> fragments = mActivity.getSupportFragmentManager().getFragments();
        for (int i = 0, max = fragments.size(); i < max; i++) {
            if (fragments.get(i) instanceof QRCodePlFFragment) {
                mActivity.backFragment();
                try {
                    ((QRCodePlFFragment) fragments.get(i)).viewPager.setCurrentItem(startPage);
                }catch (Exception e){
                    QRCodePlFFragment qrCodePlFFragment =new QRCodePlFFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("page",startPage);
                    qrCodePlFFragment.setArguments(bundle);
                    mActivity.changeFragment(qrCodePlFFragment,false);
                }
                return;
            }
        }

        QRCodePlFFragment fFragment = new QRCodePlFFragment();
        mActivity.changeFragment(fFragment, false);
        Bundle bundle = new Bundle();
        bundle.putInt("page", startPage);
        fFragment.setArguments(bundle);
    }


}
