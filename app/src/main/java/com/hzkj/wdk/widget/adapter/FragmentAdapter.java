package com.hzkj.wdk.widget.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by chenweiqi on 2017/4/28.
 */

public class FragmentAdapter extends FragmentPagerAdapter {
    List<Fragment> fragments;
    List<String> fragmentTitles;
    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        if (fragments!=null)
            return fragments.size();
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (fragmentTitles!=null)
            return fragmentTitles.get(position);
        return "";
    }

    public void setFragmentTitles(List<String> fragmentTitles) {
        this.fragmentTitles = fragmentTitles;
    }

    public void setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    public List<Fragment> getFragments() {
        return fragments;
    }
}
