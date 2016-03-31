package com.mobile.yj.screencast;

import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.mobile.yj.screencast.adapter.IViewPagerAdapter;
import com.mobile.yj.screencast.audio.AudioTabContentFragment;
import com.mobile.yj.screencast.image.ImageTabContentFragment;
import com.mobile.yj.screencast.video.VideoTabContentFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2016/3/29.
 */
public class MainActivity extends FragmentActivity {

    private final String TAG = this.getClass().getSimpleName();

    List<Fragment> mFragmentList = new ArrayList<Fragment>();
    List<String> mTabTitleList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager vp = (ViewPager) findViewById(R.id.viewPager);

        mFragmentList.add(new ImageTabContentFragment(this));
        mFragmentList.add(new AudioTabContentFragment(this));
        mFragmentList.add(new VideoTabContentFragment(this));

        mTabTitleList.add(getString(R.string.imageTabName));
        mTabTitleList.add(getString(R.string.audioTabName));
        mTabTitleList.add(getString(R.string.videoTabName));

        vp.setAdapter(new IViewPagerAdapter(getSupportFragmentManager(),
                mFragmentList, mTabTitleList));
        vp.setOffscreenPageLimit(mFragmentList.size());
        Log.d(TAG, "app start:");
    }


}
