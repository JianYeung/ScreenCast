package com.mobile.yj.screencast.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.registry.Registry;

/**
 * Created by dell on 2016/3/30.
 */
public class MeLinkManager {


    private Context mContext;
    private AndroidUpnpService upnpService;
    private Registry registry = null;
    private ControlPoint controlPoint = null;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            upnpService = (AndroidUpnpService) service;
            registry = upnpService.getRegistry();
            controlPoint = upnpService.getControlPoint();
            Log.d("YJ","sucessful");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            upnpService = null;
            Log.d("YJ","failed");
        }
    };


    public MeLinkManager(Context context) {
        mContext = context;
        mContext.getApplicationContext().bindService(new Intent(mContext, IUpnpService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public Registry getRegistry()
    {
       return registry;
    }

    public ControlPoint getControlPoint(){
        return controlPoint;
    }
}
