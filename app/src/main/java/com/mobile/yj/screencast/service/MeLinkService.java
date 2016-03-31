package com.mobile.yj.screencast.service;

import android.content.Context;

import org.teleal.cling.model.meta.Device;

import java.util.ArrayList;

/**
 * Created by dell on 2016/3/30.
 */
public class MeLinkService {

    private static MeLinkService meLinkService = null; // singleton
    private static MeLinkManager meLinkManage = null;
    private static ArrayList<Device> mDeviceList = null;
    private ICallback mICallback = null;


    private MeLinkService() {
    }

    public static MeLinkService newMeLinkService(Context context) {
        if (meLinkService == null) {
            meLinkManage = new MeLinkManager(context);
            meLinkService = new MeLinkService();
        }
        return meLinkService;
    }

    public static MeLinkService getMeLinkServiceInstance() {
        return meLinkService;
    }

    public static ArrayList<Device> getDeviceList() {
        return mDeviceList;
    }

    public static MeLinkManager getMeLinkManageInstance() {
        return meLinkManage;
    }

    public void setICallback(ICallback iCallback) {
        mICallback = iCallback;
    }
}
