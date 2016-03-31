package com.mobile.yj.screencast.image;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.mobile.yj.screencast.R;
import com.mobile.yj.screencast.service.IUpnpService;
import com.mobile.yj.screencast.utils.DeviceDisplay;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.UDAServiceId;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;
import org.teleal.cling.support.avtransport.callback.Play;
import org.teleal.cling.support.avtransport.callback.SetAVTransportURI;

import org.teleal.cling.support.connectionmanager.callback.GetProtocolInfo;
import org.teleal.cling.support.model.ProtocolInfos;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by dell on 2016/3/30.
 */
public class SampleActivity extends Activity {

    private final String TAG = SampleActivity.class.getSimpleName();

    private final int LEFT = 0;
    private final int RIGHT = 1;

    private String s = "AVTransport";
    private String s1 = "ConnectionManager";

    private int mCurrentPosition = 0;

    private List<String> imagePathList = null;

    private ImageView mImageView = null;
    private Menu mOptionsMenu = null;
    private Dialog listDialog = null;
    private ListView deviceList = null;
    private Device mDevice = null;
    private ArrayAdapter<DeviceDisplay> listAdapter = null;

    private MediaServer mediaServer;
    private AndroidUpnpService upnpService = null;
    private Registry registry = null;
    private ControlPoint controlPoint = null;

    private RegistryListener registryListener = new IRegistryListener();

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                mediaServer = new MediaServer(getLocalIpAddress());
            } catch (ValidationException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            upnpService = (AndroidUpnpService) service;
            registry = upnpService.getRegistry();
            controlPoint = upnpService.getControlPoint();
            listAdapter.clear();
            for (Device device : registry.getDevices()) {
                ((IRegistryListener) registryListener).deviceAdded(device);
            }
            registry.addListener(registryListener);
            controlPoint.search();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            upnpService = null;
        }
    };


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LEFT:
                    Log.d(TAG, "left message.");
                    mCurrentPosition += 1;
                    mCurrentPosition %= imagePathList.size();
                    shareNextPhoto(mCurrentPosition);
                    break;
                case RIGHT:
                    Log.d(TAG, "right message.");
                    mCurrentPosition = mCurrentPosition - 1 + imagePathList.size();
                    mCurrentPosition %= imagePathList.size();
                    shareNextPhoto(mCurrentPosition);
                    break;
                default:
                    return;
            }
            showPhoto(mCurrentPosition);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_details);

        getActionBar().setTitle("我的手机");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle mBundle = getIntent().getExtras();
        imagePathList = mBundle.getStringArrayList("imagePathList");
        mCurrentPosition = mBundle.getInt("position");
        //initData();
        initPhotoView();
        initDeviceListView();
    }

   /* public void initData() {
        Bundle mBundle = getIntent().getExtras();
        imagePathList = mBundle.getStringArrayList("imagePathList");
        mCurrentPosition = mBundle.getInt("position");
    }*/

    public void initPhotoView() {
        /*getActionBar().setTitle("我的手机");
        getActionBar().setDisplayHomeAsUpEnabled(true);*/
        mImageView = (ImageView) findViewById(R.id.img);
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            float beginX = 0, endX = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        beginX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        endX = event.getX();
                        if (beginX > endX) {
                            mHandler.sendEmptyMessage(LEFT);
                        } else if (beginX < endX) {
                            mHandler.sendEmptyMessage(RIGHT);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        showPhoto(mCurrentPosition);
    }

    public void initDeviceListView() {
        getApplicationContext().bindService(
                new Intent(this, IUpnpService.class), serviceConnection,
                Context.BIND_AUTO_CREATE);
        listAdapter = new ArrayAdapter<DeviceDisplay>(this,
                android.R.layout.simple_list_item_1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;

        MenuItem mi = mOptionsMenu.add(R.string.startSlide);
        mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        mi.setIcon(android.R.drawable.ic_menu_slideshow);
        mi.setVisible(false);

        mi = mOptionsMenu.add(R.string.push);
        mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        mi.setIcon(android.R.drawable.ic_menu_share);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        } else if ((item.getTitle().equals(getString(R.string.push)))
                && (registry != null)) {
            registry.removeAllRemoteDevices();
            controlPoint.search();

            showDialog();
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((registry != null) && (registry.isPaused())) {
            registry.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (registry != null) {
            registry.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registry != null) {
            registry.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registry != null) {
            registry.removeListener(registryListener);
        }
        getApplicationContext().unbindService(serviceConnection);
    }


    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择设备：");
        final LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.listview_decive, null);
        deviceList = (ListView) v.findViewById(R.id.devicelist);
        deviceList.setAdapter(listAdapter);
        builder.setView(v);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("搜索", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (registry != null) {
                    registry.removeAllRemoteDevices();
                    controlPoint.search();
                    listAdapter.notifyDataSetChanged();
                }
            }
        });

        listDialog = builder.show();
        listDialog.show();
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("rawtypes")
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "跳转到" + position + "页播放", Toast.LENGTH_SHORT).show();
                DeviceDisplay devicePlay = listAdapter.getItem(position);
                Device device = devicePlay.getDevice();
                mDevice = device;
                // sharePhoto(mCurrentPosition);
                Log.d("YJ!", device + "");
                Log.d("YJ1", "" + mCurrentPosition);
                sharePhoto(mCurrentPosition);
                listDialog.dismiss();
            }
        });
    }

    public void showPhoto(int currentPosition) {
        String path = imagePathList.get(currentPosition);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = 4;
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        mImageView.setImageBitmap(bm);
    }

    public void sharePhoto(int currentPosition) {

        String url = "http://" + mediaServer.getAddress()
                + imagePathList.get(currentPosition);
        Log.e("URL", url);
        Uri.parse(url);
        GetInfo(mDevice);
        executeAVTransportURI(mDevice, url);
        executePlay(mDevice);
    }

    public void shareNextPhoto(int currentPosition) {

        String url = "http://" + mediaServer.getAddress()
                + imagePathList.get(currentPosition);
        Log.e("URL", url);
        Uri.parse(url);
        GetInfo(mDevice);
        executeAVTransportURI(mDevice, url);
        executePlay(mDevice);
    }


    public void executeAVTransportURI(Device device, String uri) {

        ServiceId AVTransportId = new UDAServiceId(s);
        Service service = device.findService(AVTransportId);
        ActionCallback callback = new SetAVTransportURI(service, uri) {

            @Override
            public void failure(ActionInvocation arg0, UpnpResponse arg1,
                                String arg2) {
                Log.e("SetAVTransportURI", "failed^^^^^^^");
            }

        };
        controlPoint.execute(callback);

    }

    public void executePlay(Device device) {
        ServiceId AVTransportId = new UDAServiceId(s);
        Service service = device.findService(AVTransportId);
        ActionCallback playcallback = new Play(service) {

            @Override
            public void failure(ActionInvocation arg0, UpnpResponse arg1,
                                String arg2) {
                Log.e("Play", "failed^^^^^^^");
            }

        };

        controlPoint.execute(playcallback);

    }


    public void GetInfo(Device device) {
        ServiceId AVTransportId = new UDAServiceId(s1);
        Service service = device.findService(AVTransportId);
        ActionCallback getInfocallback = new GetProtocolInfo(service) {

            @Override
            public void failure(ActionInvocation arg0, UpnpResponse arg1,
                                String arg2) {
                Log.v("GetProtocolInfo", "failed^^^^^^^");
            }

            @Override
            public void received(ActionInvocation actionInvocation,
                                 ProtocolInfos sinkProtocolInfos,
                                 ProtocolInfos sourceProtocolInfos) {
                Log.v("sinkProtocolInfos", sinkProtocolInfos.toString());
                Log.v("sourceProtocolInfos", sourceProtocolInfos.toString());
            }

        };
        controlPoint.execute(getInfocallback);
    }

    private InetAddress getLocalIpAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
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
            runOnUiThread(new Runnable() {
                public void run() {
                    DeviceDisplay d = new DeviceDisplay(device);
                    int position = listAdapter.getPosition(d);
                    if (position >= 0) {
                        listAdapter.remove(d);
                        listAdapter.insert(d, position);
                    } else {
                        listAdapter.add(d);
                    }
                    listAdapter.notifyDataSetChanged();
                }
            });
        }

        @SuppressWarnings("rawtypes")
        public void deviceRemoved(final Device device) {
            runOnUiThread(new Runnable() {
                public void run() {
                    listAdapter.remove(new DeviceDisplay(device));
                }
            });
        }
    }

}
