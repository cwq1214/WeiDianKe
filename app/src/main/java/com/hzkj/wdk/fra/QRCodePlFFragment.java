package com.hzkj.wdk.fra;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzkj.wdk.R;
import com.hzkj.wdk.widget.adapter.FragmentAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenweiqi on 2017/4/28.
 */

public class QRCodePlFFragment extends BaseFragment implements View.OnClickListener {

    ImageView img_baofen,img_vip,img_jifen;
    TextView text_baofen,text_vip,text_jifen;
    LinearLayout layout_baofen,layout_jifen,layout_vip,layout_personal;
    ImageView back;
    ViewPager viewPager;
    FragmentAdapter adapter;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.pbtnName)
    TextView pbtnName;
    @BindView(R.id.img_ren)
    ImageView imgRen;

    View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_qrcode_platform,null);
        ButterKnife.bind(this,rootView);
        findById();
        setListener();
        initViewPager();
        mActivity.FrameLayoutVisible(true);
        return rootView;
    }

    private void findById(){
        img_baofen = (ImageView) rootView.findViewById(R.id.img_baofen);
        img_vip = (ImageView) rootView.findViewById(R.id.img_vip);
        img_jifen = (ImageView) rootView.findViewById(R.id.img_jifen);
        text_baofen = (TextView) rootView.findViewById(R.id.text_baofen);
        text_vip = (TextView) rootView.findViewById(R.id.text_vip);
        text_jifen = (TextView) rootView.findViewById(R.id.text_jifen);
        layout_baofen = (LinearLayout) rootView.findViewById(R.id.layout_baofen);
        layout_jifen = (LinearLayout) rootView.findViewById(R.id.layout_jifen);
        layout_vip = (LinearLayout) rootView.findViewById(R.id.layout_vip);
        layout_personal = (LinearLayout) rootView.findViewById(R.id.layout_personal);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        back = (ImageView) rootView.findViewById(R.id.back);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (adapter!=null)
            title.setText(adapter.getPageTitle(0));

        if (getArguments()!=null) {
            int startpage = getArguments().getInt("page", 0);
            viewPager.setCurrentItem(startpage);
        }

//        title.setText(mActivity.spu.getQrCodeBtnName());
//        pbtnName.setText(mActivity.spu.getPersonalCenterBtnName());
    }

    private void setListener(){
        back.setOnClickListener(this);
        layout_baofen.setOnClickListener(this);
        layout_jifen.setOnClickListener(this);
        layout_vip.setOnClickListener(this);
        layout_personal.setOnClickListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("sel "+position);
                if (position==0){
                    img_baofen.setImageDrawable(getResources().getDrawable(R.drawable.money_sel));
                    img_jifen.setImageDrawable(getResources().getDrawable(R.drawable.gift_nor));
                    img_vip.setImageDrawable(getResources().getDrawable(R.drawable.crown_nor));
                    imgRen.setImageDrawable(getResources().getDrawable(R.drawable.person_nor));
                    text_vip.setTextColor(Color.parseColor("#a9b7b7"));
                    text_jifen.setTextColor(Color.parseColor("#a9b7b7"));
                    text_baofen.setTextColor(Color.parseColor("#e2333c"));
                    pbtnName.setTextColor(Color.parseColor("#a9b7b7"));
                }else if (position==1){
                    img_baofen.setImageDrawable(getResources().getDrawable(R.drawable.money_nor));
                    img_jifen.setImageDrawable(getResources().getDrawable(R.drawable.gift_nor));
                    img_vip.setImageDrawable(getResources().getDrawable(R.drawable.crown_sel));
                    imgRen.setImageDrawable(getResources().getDrawable(R.drawable.person_nor));
                    text_vip.setTextColor(Color.parseColor("#e2333c"));
                    text_jifen.setTextColor(Color.parseColor("#a9b7b7"));
                    text_baofen.setTextColor(Color.parseColor("#a9b7b7"));
                    pbtnName.setTextColor(Color.parseColor("#a9b7b7"));

                }else if (position==2){
                    img_baofen.setImageDrawable(getResources().getDrawable(R.drawable.money_nor));
                    img_jifen.setImageDrawable(getResources().getDrawable(R.drawable.gift_sel));
                    img_vip.setImageDrawable(getResources().getDrawable(R.drawable.crown_nor));
                    imgRen.setImageDrawable(getResources().getDrawable(R.drawable.person_nor));
                    text_vip.setTextColor(Color.parseColor("#a9b7b7"));
                    text_jifen.setTextColor(Color.parseColor("#e2333c"));
                    text_baofen.setTextColor(Color.parseColor("#a9b7b7"));
                    pbtnName.setTextColor(Color.parseColor("#a9b7b7"));

                }else if (position==3){
                    img_baofen.setImageDrawable(getResources().getDrawable(R.drawable.money_nor));
                    img_jifen.setImageDrawable(getResources().getDrawable(R.drawable.gift_nor));
                    img_vip.setImageDrawable(getResources().getDrawable(R.drawable.crown_nor));
                    imgRen.setImageDrawable(getResources().getDrawable(R.drawable.person_sel));
                    text_vip.setTextColor(Color.parseColor("#a9b7b7"));
                    text_jifen.setTextColor(Color.parseColor("#a9b7b7"));
                    text_baofen.setTextColor(Color.parseColor("#a9b7b7"));
                    pbtnName.setTextColor(Color.parseColor("#e2333c"));
                }
                title.setText(adapter.getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void initViewPager(){
        viewPager.setAdapter(adapter = new FragmentAdapter(getChildFragmentManager()));
        adapter.setFragments(getFragments());
        adapter.setFragmentTitles(Arrays.asList(new String[]{"二维码平台","会员服务","积分商城","个人中心"}));
        adapter.notifyDataSetChanged();
        text_baofen.setText("二维码平台");


    }

    private List<Fragment> getFragments(){
        List<Fragment> fragments =new ArrayList<>();
        GuaJiBaoFenFragment guaJiBaoFenFragment = new GuaJiBaoFenFragment();
        guaJiBaoFenFragment.parent = this;
        fragments.add(guaJiBaoFenFragment);
        fragments.add(new QRCodeVIPFragment());
        fragments.add(new QRCodeJiFenFragment());

        PersonalCenterFra personalCenterFra  = new PersonalCenterFra();
        personalCenterFra.showTitle(false);
        personalCenterFra.qrCodePlFFragment = this;
        fragments.add(personalCenterFra);
        return fragments;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                mActivity.backFragment();
                break;
            case R.id.layout_baofen:
                viewPager.setCurrentItem(0);
                break;
            case R.id.layout_jifen:
                viewPager.setCurrentItem(2);
                break;
            case R.id.layout_vip:
                viewPager.setCurrentItem(1);
                break;
            case R.id.layout_personal:
                viewPager.setCurrentItem(3);
//                openPersonalCenter();
                break;
        }
    }

    private void openPersonalCenter(){
        List<Fragment> fragments = mActivity.getSupportFragmentManager().getFragments();
        for (Fragment fragment:fragments){
            if (fragment instanceof PersonalCenterFra){
                mActivity.backFragment();
                return;
            }
        }
        mActivity.changeFragment(new PersonalCenterFra(),false);

    }


}
