package com.hzkj.wdk.fra;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hzkj.wdk.R;
import com.hzkj.wdk.widget.adapter.FragmentAdapter;
import com.hzkj.wdk.widget.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by chenweiqi on 2017/4/28.
 */

public class GuaJiBaoFenFragment extends BaseFragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    FragmentStateAdapter fragmentAdapter;
    public QRCodePlFFragment parent;

    View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_qrcode_guajibaofen,null);
        findById();
        init();
        return rootView;
    }

    private void findById(){
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
    }
    private void init(){
        viewPager.setAdapter(fragmentAdapter = new FragmentStateAdapter(getChildFragmentManager()));

        fragmentAdapter.setFragments(getFragments());
        fragmentAdapter.setFragmentTitles(getFragmentTitles());
        fragmentAdapter.notifyDataSetChanged();


        tabLayout.setupWithViewPager(viewPager);
    }

    private List<Fragment> getFragments(){
        List<Fragment> fragments = new ArrayList<>();
        QRCodeContentListFragment personal = new QRCodeContentListFragment();
        personal.parent = this;
        personal.setType(QRCodeContentListFragment.TYPE_PERSON);
        fragments.add(personal);
        QRCodeContentListFragment weXinQun = new QRCodeContentListFragment();
        weXinQun.parent = this;

        weXinQun.setType(QRCodeContentListFragment.TYPE_GROUP);
        fragments.add(weXinQun);
        QRCodeContentListFragment gongzhonghao = new QRCodeContentListFragment();
        gongzhonghao.parent = this;

        gongzhonghao.setType(QRCodeContentListFragment.TYPE_SUBSCRIPTION);
        fragments.add(gongzhonghao);

        return fragments;
    }

    private List<String> getFragmentTitles(){
        List<String> fragmentTitles = new ArrayList<>();
        fragmentTitles.add("个人名片");
        fragmentTitles.add("v信群");
        fragmentTitles.add("公众号");
        return fragmentTitles;
    }

}
