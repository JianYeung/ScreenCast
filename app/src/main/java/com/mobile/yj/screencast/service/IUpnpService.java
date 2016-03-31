package com.mobile.yj.screencast.service;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.android.AndroidUpnpServiceConfiguration;
import org.teleal.cling.android.AndroidUpnpServiceImpl;
import org.teleal.cling.android.AndroidWifiSwitchableRouter;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDAServiceType;
import org.teleal.cling.protocol.ProtocolFactory;

/**
 * Created by dell on 2016/3/30.
 */
public class IUpnpService extends AndroidUpnpServiceImpl {
    public IUpnpService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected AndroidUpnpServiceConfiguration createConfiguration(WifiManager wifiManager) {
        return new AndroidUpnpServiceConfiguration(wifiManager) {
            @Override
            public int getRegistryMaintenanceIntervalMillis() {
                return 7000;
            }

            @Override
            public ServiceType[] getExclusiveServiceTypes() {
                return new ServiceType[]{new UDAServiceType("AVTransport"),
                        new UDAServiceType("ConnectionManager")};
            }
        };
    }

    @Override
    protected AndroidWifiSwitchableRouter createRouter(UpnpServiceConfiguration configuration, ProtocolFactory protocolFactory, WifiManager wifiManager, ConnectivityManager connectivityManager) {
        return super.createRouter(configuration, protocolFactory, wifiManager, connectivityManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    protected boolean isListeningForConnectivityChanges() {
        return super.isListeningForConnectivityChanges();
    }
}
