package com.mobile.yj.screencast.callback;

import com.mobile.yj.screencast.server.MediaServer;

import org.teleal.cling.android.AndroidUpnpService;

/**
 * Created by dell on 2016/3/31.
 */
public interface ICallback {
    public void setUpnpService(final AndroidUpnpService upnpService);
    public void setMediaServer(final MediaServer mediaServer);
}
