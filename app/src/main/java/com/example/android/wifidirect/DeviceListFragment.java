package com.example.android.wifidirect;


import android.Manifest;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;


public class DeviceListFragment extends ListFragment implements PeerListListener {

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ProgressDialog progressDialog = null;
    View mContentView = null;
    private WifiP2pDevice device;
    private LocationManager mLocationManager;
    private Location mLocation;
    private double lat;
    private double lng;
    private double time;
    private double speed;
    private double bearing;


    private Criteria criteria = new Criteria();
    private Gps myGps = new Gps();
    private Gps otherGps = new Gps();
    //用了CP1而不是CP
    private CollisionPrediction1 cp = new CollisionPrediction1();
    private int collisionFlag;
    String provider;

    DecimalFormat dfSpeed = new DecimalFormat("00.0");
    DecimalFormat dfBear = new DecimalFormat("000");
    DecimalFormat dfLa = new DecimalFormat("0.000000");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = mLocationManager.getBestProvider(criteria, true); // 获取GPS信息
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, mLocationListener);
        mLocation = mLocationManager.getLastKnownLocation(provider);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_devices, peers));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_list, null);
        return mContentView;
        
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    public final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            updateToNewLocation(null);
        }

        @Override
        public void onLocationChanged(Location location) {
            updateToNewLocation(location);
        }
    };

    private Location updateToNewLocation(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            time = location.getTime();
            speed = location.getSpeed();
            bearing = location.getBearing();
            //String timeStamp = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(time);
            if (lat < 0.1){
                speed=11.1f;
                lat = 111.000000000;
                lng = 111.11111111;
                bearing=111;
            }
        }else{


        }
        return location;

    }
  
    private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {
        private List<WifiP2pDevice> items;
        public WiFiPeerListAdapter(Context context, int textViewResourceId,
                List<WifiP2pDevice> objects) {
            super(context, textViewResourceId, objects);
            items = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_devices, null);
            }
            WifiP2pDevice device = items.get(position);
            if (device != null) {
                TextView top = (TextView) v.findViewById(R.id.device_name);
                TextView bottom = (TextView) v.findViewById(R.id.device_address);
                TextView collision = (TextView) mContentView.findViewById(R.id.collisionFlag);
//                if (top != null) {
//                    top.setText(device.deviceName);
//                }
//                if (bottom != null) {
//                    bottom.setText(device.deviceAddress);
//                }
                if(top!=null && bottom!=null){
                    //textView 显示ssid及mac地址
                    top.setText(device.deviceName);
                    bottom.setText(device.deviceAddress);
                    if(device.deviceName.indexOf("C2X")!=-1){
                        otherGps.setCarId("otherCar");
                        otherGps.setGpsTime(1);
                        otherGps.setSpeed(Double.parseDouble(device.deviceName.substring(3,7)));
                        otherGps.setAngle(Double.parseDouble(device.deviceName.substring(7,10)));
                        otherGps.setLatitude(Double.parseDouble("39."+device.deviceName.substring(10,16)));
                        otherGps.setLongitude(Double.parseDouble("116."+device.deviceName.substring(16,22)));
                        otherGps.geodeticToCartesian();

//                        Toast.makeText(this.getContext(),"otherGps Lat: " + otherGps.getLatitude()
//                                + " " +  "otherGps Long: " + otherGps.getLongitude()
//                                + " " +  "otherGps Speed: " + otherGps.getSpeed()
//                                + " " +  "otherGps Angle: " + otherGps.getAngle()
//                                + " " +  "otherGps Time: " + otherGps.getGpsTime(),Toast.LENGTH_SHORT).show();
                        myGps.setCarId("myGps");
                        myGps.setGpsTime(1);
                        myGps.setSpeed(speed);
                        myGps.setAngle(bearing);
                        myGps.setLatitude(lat);
                        myGps.setLongitude(lng);
                        myGps.geodeticToCartesian();
                        cp.setData1(myGps);
                        cp.setData2(otherGps);
                        collisionFlag = cp.collisionDirection();
                        if (collisionFlag == 0)
                            collision.setText("normal");
                        else if(collisionFlag == 1)
                            collision.setText("left");
                        else if(collisionFlag == 2)
                            collision.setText("right");
                        else
                            collision.setText("error");


                    }else{
                       // Toast.makeText(this.getContext(), device.deviceName, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            return v;
        }
    }
    //更改deviceName
    public void updateThisDevice(WifiP2pDevice device) {
        if(device!= null){
            this.device = device;
            TextView view = (TextView) mContentView.findViewById(R.id.my_name);

            // 以下均为本机的时间以及gps信息
            String sLatitude=String.valueOf(dfLa.format(lat));
            String sLongitude=String.valueOf(dfLa.format(lng));
            //没有gps信号的情况下不会闪退
            if(sLatitude=="0.0"||sLongitude=="0.0"){
                sLatitude="111.000000000";
                sLongitude="111.11111111";
            }
            String rename = "C2X";         //所有WHIP结构均以“C2X”开头；时间，保留分和秒，共4位 +getDate()
            rename+=String.valueOf(dfSpeed.format(speed));//速度保留一位小数，算小数点4位，单位m/s
            rename+=String.valueOf(dfBear.format(bearing));//方向角取整，3位
//            rename+="0000011111";
            //Log.d("test3", sLatitude);

            rename+=sLatitude.substring(sLatitude.indexOf(".")+1,sLatitude.indexOf(".")+7);//纬度，只显示小数点后6位
            rename+=sLongitude.substring(sLongitude.indexOf(".")+1,sLongitude.indexOf(".")+7);//经度，只显示小数点后6位
            rename+=getDate();

            view.setText(rename);
            ((DeviceActionListener) getActivity()).changeWifiDirectName(rename);
            TextView view_1 = (TextView) mContentView.findViewById(R.id.my_address);
            Log.d("test2", "device == null ? "+ (device == null));
            Log.d("test2","updateThisDevice");
            view_1.setText(device.deviceAddress);
        }

    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0) {
            Log.d(WiFiDirectActivity.TAG, "No devices found");
            return;
        }else {
            Log.d(WiFiDirectActivity.TAG, "get peers,peers' size: "+peers.size());
        }
    }

    public void clearPeers() {
        peers.clear();
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "finding peers", true,
                true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                    }
                });
        Thread t = new Thread(new Runnable() {
        	public void run() {
        		try {
        			Thread.sleep(2000);//������ʾ10���ȡ��ProgressDialog
        			} catch (InterruptedException e) {
        				e.printStackTrace();
        				}
        		progressDialog.dismiss();
        		}
        	});
        t.start();
        }

    public interface DeviceActionListener {
        void changeWifiDirectName(String newName);
    }

    
    public static final String getDate(){        
          Calendar cal = Calendar.getInstance();        
          java.text.SimpleDateFormat sdf = new SimpleDateFormat("ss");
          String cdate = sdf.format(cal.getTime());                
          System.out.println("times is :"+cdate);        
          return cdate;    
    }
}
