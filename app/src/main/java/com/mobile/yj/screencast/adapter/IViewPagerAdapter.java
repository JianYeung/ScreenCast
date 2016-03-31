package com.mobile.yj.screencast.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by dell on 2016/3/30.
 */
public class IViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;
    private List<String> mTabTitleList;

    public IViewPagerAdapter(FragmentManager fm, List<Fragment> flist,
                             List<String> slist) {
        super(fm);
        mFragmentList = flist;
        mTabTitleList = slist;
    }

    @Override
    public Fragment getItem(int pos) {
        return (mFragmentList == null || mFragmentList.size() == 0) ? null
                : mFragmentList.get(pos);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (mTabTitleList.size() > position) ? mTabTitleList.get(position)
                : "";
    }

    @Override
    public int getCount() {
        return mFragmentList == null ? 0 : mFragmentList.size();
    }
}
