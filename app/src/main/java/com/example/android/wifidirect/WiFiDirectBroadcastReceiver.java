package com.example.android.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private Intent intent1;
    private WifiP2pManager manager;
    private Channel channel;
    private WiFiDirectActivity activity;
//    private int TIME=1000;
//    private int i=1;
    private WifiP2pDevice mDevice;
    public Handler handler;

    private Timer timer;
    private TimerTask timerTask;
    private boolean isActive;
    public DeviceListFragment fragment;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
            WiFiDirectActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        timer = new Timer();
        final WiFiDirectActivity activity1 = activity;
//        fragment = (DeviceListFragment) activity1.getFragmentManager().findFragmentById(R.id.frag_list);
        timerTask = new UpdateTask();
        handler=new Handler(){
        	public void handleMessage(Message msg) {
    			super.handleMessage(msg);
                if(msg.what == 0x123){
                    fragment = (DeviceListFragment) activity1.getFragmentManager()
                            .findFragmentById(R.id.frag_list);
                    Log.d("test2", "intent1 == null ? " + (intent1 == null));
                    if(fragment!=null){
                        fragment.updateThisDevice(mDevice);
                    }

                    }
    			}
        };
    }
    
	/*
	 * ����Ƿ�֧��Wi-Fi Direct�����������һ���õ�λ���ǣ�
	 * ����WIFI_P2P_STATE_CHANGED_ACTION���͵�Intent�Ĺ㲥�������У�
	 * ��Wi-Fi Direct��״̬֪ͨ�����Activity����������Ӧ�ķ�Ӧ��
	 */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("receive","可以接收");
        intent1=intent;
        if(mDevice == null){
            mDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
        }
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                activity.setIsWifiP2pEnabled(true);

            } else {
                activity.setIsWifiP2pEnabled(false);
            }
            Log.d(WiFiDirectActivity.TAG, "P2P state changed - " + state);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (manager != null) {
                manager.requestPeers(channel, (PeerListListener) activity.getFragmentManager()
                        .findFragmentById(R.id.frag_list));
            }
            Log.d(WiFiDirectActivity.TAG, "P2P peers changed");
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            if (manager == null) {
                return;
            }
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
            } else {
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing

        	//i++;
//            if(i>0){
//                handler.postDelayed(runnable, TIME);
//                i = 0;
//            }
			//
        }
	}

    public void startTimer(){
        if(!isActive){
            if((timer != null) && (timerTask != null)){
                timer.schedule(timerTask, 0, 1000);
                isActive = true;
            }else{
                timer = new Timer();
                timerTask = new UpdateTask();
                timer.schedule(timerTask, 0, 1000);
                isActive = true;
            }
        }else {
            Toast.makeText(activity, "Timer is active!", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopTimer(){
        if((timer != null) && (timerTask != null)){
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
            isActive = false;
        }
    }

    class UpdateTask extends TimerTask{

        @Override
        public void run() {
            handler.sendEmptyMessage(0x123);
        }
    }
    
//	Runnable runnable=new Runnable()
//	{
//		@Override
//		public void run() {
//			 DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
//	                    .findFragmentById(R.id.frag_list);
//			 Log.d("test2", "intent1 == null ? " + (intent1 == null));
//			 fragment.updateThisDevice(mDevice);
//	         handler.postDelayed(this, TIME);
//		}
//	};
}
