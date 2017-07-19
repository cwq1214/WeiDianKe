package com.hzkj.wdk.fra;

import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkj.wdk.R;
import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.http.SimpleResponseCallback;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.SharePreferenceUtil;
import com.hzkj.wdk.utils.UpLoadImageUtil;
import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.widget.CustomDialog;
import com.hzkj.wdk.widget.CustomDialog2;
import com.hzkj.wdk.widget.NoScrollViewPager;
import com.hzkj.wdk.widget.adapter.FragmentAdapter;
import com.hzkj.wdk.widget.adapter.FragmentStateAdapter;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chenweiqi on 2017/5/2.
 */

public class ReleaseQRCodeFragment extends BaseFragment implements View.OnTouchListener {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    NoScrollViewPager viewPager;
    View rootView;
    SharePreferenceUtil spu;
    FragmentStateAdapter adapter;

    //是否自己发布名片
    public boolean isSelfRelease = false;

    public int startPageIndex=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_release_qrcode, container, false);
        ButterKnife.bind(this, rootView);
        rootView.setOnTouchListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            isSelfRelease = getArguments().getBoolean("isSelfRelease",false);
        }

        spu = mActivity.spu;

        viewPager.setOffscreenPageLimit(0);

        viewPager.setAdapter(adapter = new FragmentStateAdapter(getChildFragmentManager()));
        adapter.setFragments(getFragments());
        adapter.setFragmentTitles(getFragmentTitles());
        adapter.notifyDataSetChanged();

        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(startPageIndex);


    mActivity.getUserInfo(new MainActivity.GetUserInfoCallback() {
        @Override
        public void onSuccess() {
            if (TextUtils.isEmpty(spu.getAvatar())
                    ||TextUtils.isEmpty(spu.getNickName())) {
                final CustomDialog2 dialog = new CustomDialog2(getContext());
                dialog.setConfirmOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.backFragment();
                        mActivity.changeFragment(new ModifyUserNameAvatarFragment(), false);
                        dialog.dismiss();
                    }
                });
                dialog.setTitleText("温馨提示");
                dialog.setMessage1("检测到您未设置昵称或头像，请先设置");
                dialog.setCancelable(false);
                dialog.show();
            }
        }
    });

    }


    @OnClick(R.id.back)
    public void onBackClick() {
        mActivity.backFragment();
    }


    public List<Fragment> getFragments() {

        List<Fragment> fragments = new ArrayList<>();


        ReleaseSelfFragment selfFragment = new ReleaseSelfFragment();
        selfFragment.isReleaseSelf=true;
        selfFragment.releaseQRCodeFragment = this;

        fragments.add(selfFragment);

        ReleaseQunFragment qun = new ReleaseQunFragment();
        qun.isReleaseSelf=true;
        qun.releaseQRCodeFragment = this;

        qun.type = 2;
        fragments.add(qun);

        ReleaseQunFragment gongzhonghao = new ReleaseQunFragment();
        gongzhonghao.releaseQRCodeFragment = this;
        gongzhonghao.isReleaseSelf=true;
        gongzhonghao.type = 3;
        fragments.add(gongzhonghao);
        return fragments;

    }

    public List<String> getFragmentTitles() {
        List<String> title = new ArrayList<>();
        title.add("个人名片");
        title.add("v信群");
        title.add("公众号");
        return title;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }






}
