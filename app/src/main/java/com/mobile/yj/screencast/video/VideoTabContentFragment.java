package com.mobile.yj.screencast.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by dell on 2016/3/29.
 */
public class VideoTabContentFragment extends Fragment{
    private Context mContext;

    public VideoTabContentFragment() {

    }

    @SuppressLint("ValidFragment")
    public VideoTabContentFragment(Context context) {
        mContext = context;
    }
}
