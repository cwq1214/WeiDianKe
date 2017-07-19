package com.hzkj.wdk.widget;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzkj.wdk.R;
import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.fra.GetSelfCodeFragment;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.UserInfo;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.Utils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hzkj.wdk.utils.JiaFenConstants.SERVER_URL;

/**
 * Created by chenweiqi on 2017/5/8.
 */

public class ShareDialog extends AlertDialog {


    View rootView;
    @BindView(R.id.weixin)
    LinearLayout weixin;
    @BindView(R.id.pengyouquan)
    LinearLayout pengyouquan;
    @BindView(R.id.link)
    LinearLayout link;
    @BindView(R.id.qq)
    LinearLayout qq;
    @BindView(R.id.qzone)
    LinearLayout qzone;
    @BindView(R.id.img)
    LinearLayout img;
    UMShareListener shareListener;
    Activity activity;
    public String title;
    public  String desc;

    public ShareDialog(Activity context) {
        super(context, R.style.dialog_round);
        this.activity = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_share, null);
        ButterKnife.bind(this, rootView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(rootView);




        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (Utils.deviceWidth- TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        32, getContext().getResources().getDisplayMetrics())); ;
        lp.height = lp.width*5/6;
        dialogWindow.setAttributes(lp);
    }


    @OnClick({R.id.weixin, R.id.pengyouquan, R.id.link, R.id.qq, R.id.qzone, R.id.img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.weixin:
                share(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.pengyouquan:
                share(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case R.id.link:
                String shareLink = ((MainActivity) activity).spu.getShareLink();
                if (TextUtils.isEmpty(shareLink)){
                    Utils.toastShow(getContext(),"链接为空");
                    break;
                }
                ClipboardManager manager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setText(shareLink);
                Utils.toastShow(getContext(),"复制成功");
                break;
            case R.id.qq:
                share(SHARE_MEDIA.QQ);
                break;
            case R.id.qzone:
                share(SHARE_MEDIA.QZONE);
                break;
            case R.id.img:
                GetSelfCodeFragment fragment = new GetSelfCodeFragment();
                ((MainActivity) activity).changeFragment(fragment,false);
                break;
        }

        dismiss();
    }

    private void share(SHARE_MEDIA share_media){
        UMImage image = new UMImage(activity, R.drawable.ic_launcher);

        new ShareAction(activity).setPlatform(share_media)
                .withText(TextUtils.isEmpty(desc)?"免费下载.免费使用.被动加粉坐等被加为好友！突破单日加粉限制.快速建立5千好友朋友圈！":desc)
                .withTargetUrl("http://weidianke.qb1611.cn/wjb")
                .withTitle(TextUtils.isEmpty(title)?"被动加粉，3天加满5千好友":title)
                .withMedia(image)
                .setCallback(shareListener)
                .share();
    }

    @Override
    public void show() {
        getData();
//        super.show();
    }

    private void getData() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("r", "api/qrcode/getUserInfo");
        params.addQueryStringParameter("token", "" + ((MainActivity)activity).spu.getToken());
        SimpleProtocol simpleProtocol = new SimpleProtocol(activity);
        simpleProtocol.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, new IResponseCallback<String>() {
            @Override
            public void onSuccess(String s) {
                LoadingD.hideDialog();
                System.out.println(s);
                UserInfo baseJson = new Gson().fromJson(s, new TypeToken<UserInfo>() {
                }.getType());
//                userInfo = baseJson.result;


                ((MainActivity)activity).spu.setAvatar(baseJson.userImg);
                ((MainActivity)activity).spu.setStatus(baseJson.nickname);
                ((MainActivity)activity).spu.setShareLink(baseJson.share_link);
                ShareDialog.super.show();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                LoadingD.hideDialog();
                System.out.println(errorModel.getMsg());
            }

            @Override
            public void onStart() {
                LoadingD.hideDialog();
                LoadingD.showDialog(activity);
            }
        });

    }
}
