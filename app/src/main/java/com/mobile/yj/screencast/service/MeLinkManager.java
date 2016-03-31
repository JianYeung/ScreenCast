package com.mobile.yj.screencast.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import com.mobile.yj.screencast.callback.ICallback;
import com.mobile.yj.screencast.callback.IDeviceCallback;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by dell on 2016/3/30.
 */
public class MeLinkManager {


    private static Context mContext;
    public AndroidUpnpService upnpService;
    private static AndroidUpnpService mUpnpservice;
    private Registry registry = null;
    private ControlPoint controlPoint = null;
    private static ICallback mICallback = null;
    private static IDeviceCallback mIDeviceCallback = null;
    private MediaServer mediaServer = null;

    private static MeLinkManager meLinkManager = null;

    private IRegistryListener registryListener = new IRegistryListener();

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                mediaServer = new MediaServer(getLocalIpAddress());
                mICallback.setMediaServer(mediaServer);
            } catch (ValidationException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }


            upnpService = (AndroidUpnpService) service;
            registry = upnpService.getRegistry();
            controlPoint = upnpService.getControlPoint();
            mICallback.setUpnpService(upnpService);
            Log.d("YJ", "sucessful");
            mIDeviceCallback.clearList();

            for (Device device : registry.getDevices()) {
                ((IRegistryListener) registryListener).deviceAdded(device);
            }
            registry.addListener(registryListener);
            controlPoint.search();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            upnpService = null;

            Log.d("YJ", "failed");
        }
    };


    private MeLinkManager() {
    }

    public static MeLinkManager newMeLinkManager(Context context) {
        mContext = context;
        if (meLinkManager == null) {
            meLinkManager = new MeLinkManager();
        }
        setIDeviceCallback((IDeviceCallback) mContext);
        setICallback((ICallback) mContext);
        return meLinkManager;
    }

    public static MeLinkManager getMeLinkManagerInstance() {
        return meLinkManager;
    }

    public void connect() {
        mContext.getApplicationContext().bindService(new Intent(mContext, IUpnpService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        Log.d("YJ8", "" + mUpnpservice);
    }


    public void destroy(final Registry registry) {
        registry.removeListener(registryListener);
    }

    public void disconnect() {
        mContext.getApplicationContext().unbindService(serviceConnection);
    }

    public static void setICallback(ICallback iCallback) {
        mICallback = iCallback;
    }

    public static void setIDeviceCallback(IDeviceCallback iDeviceCallback) {
        mIDeviceCallback = iDeviceCallback;
    }


    private InetAddress getLocalIpAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return InetAddress.getByName(String.format("%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff)));
    }

    class IRegistryListener extends DefaultRegistryListener {

        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {

            deviceRemoved(device);
        }

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            deviceRemoved(device);
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            deviceAdded(device);
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            deviceRemoved(device);
        }

        @SuppressWarnings("rawtypes")
        public void deviceAdded(final Device device) {
            mIDeviceCallback.addDevice(device);
        }

        @SuppressWarnings("rawtypes")
        public void deviceRemoved(final Device device) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mIDeviceCallback.deleteDevice(device);
                }
            });
        }
    }
}
