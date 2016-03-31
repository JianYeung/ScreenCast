package com.mobile.yj.screencast.callback;

import org.teleal.cling.model.meta.Device;

/**
 * Created by dell on 2016/3/30.
 */
public interface IDeviceCallback {
    public void addDevice(final Device device);
    public void deleteDevice(final Device device);
    public void clearList();
}
