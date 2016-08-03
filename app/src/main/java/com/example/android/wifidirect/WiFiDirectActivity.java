package com.example.android.wifidirect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.wifidirect.DeviceListFragment.DeviceActionListener;

public class WiFiDirectActivity extends Activity implements  DeviceActionListener, OnClickListener {

    public static final String TAG = "wifidirectdemo";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private WiFiDirectBroadcastReceiver receiver = null;
    private Button start;
    private Button stop;
	private Handler mHandler;
	private Timer timer;
	private OneTask task;
	private TextView time1;


    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);


        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

		start = (Button) findViewById(R.id.button1);
		start.setOnClickListener(this);
		stop = (Button) findViewById(R.id.button2);
		stop.setOnClickListener(this);
		time1 = (TextView) findViewById(R.id.time);

		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				}
		};

    }

    class OneTask extends TimerTask{
		@Override
		public void run() {
			  if (!isWifiP2pEnabled) {
                  Toast.makeText(WiFiDirectActivity.this, R.string.p2p_off_warning,
                          Toast.LENGTH_SHORT).show();
              }
              //final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
              //        .findFragmentById(R.id.frag_list);

              manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                  @Override
                  public void onSuccess() {
                      //Toast.makeText(WiFiDirectActivity.this, "Discovery Initiated",
                             // Toast.LENGTH_SHORT).show();
              	    time1.setText(getDate());

                  }
                  @Override
                  public void onFailure(int reasonCode) {
                      Toast.makeText(WiFiDirectActivity.this, "Discovery Failed : " + reasonCode,
                              Toast.LENGTH_SHORT).show();
                  }
              });			
             // mHandler.sendEmptyMessage(0x123);
		}
	}
    
    /**�����Activity��onResume()������ע��㲥������������Ҫ�����Activity��onPause()����ע������*/
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.atn_direct_enable:
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                return true;
            case R.id.atn_direct_discover:
                if (!isWifiP2pEnabled) {
                    Toast.makeText(WiFiDirectActivity.this, R.string.p2p_off_warning,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                        .findFragmentById(R.id.frag_list);
                fragment.onInitiateDiscovery();
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(WiFiDirectActivity.this, "Discovery Initiated",
                                Toast.LENGTH_SHORT).show();
                	    time1.setText(getDate());

                    }
                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WiFiDirectActivity.this, "Discovery Failed : " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeWifiDirectName(final String newName){
    	Method m=null;
    	try{
    		m=manager.getClass().getMethod("setDeviceName",new Class[]{channel.getClass(),
    				String.class,WifiP2pManager.ActionListener.class});
    	}catch (NoSuchMethodException e) {
    	    e.printStackTrace();
    	}
    	try{
    		if(m!=null){
    			m.invoke(manager, channel,newName,new WifiP2pManager.ActionListener() {
					@Override
					public void onSuccess() {
						Log.d(TAG, "Name changed to "+newName);
					}
					@Override
					public void onFailure(int reason) {
						Log.d(TAG, "The name was not changed");
					}
				});
    		}
    	}catch (IllegalAccessException e) {
    	    e.printStackTrace();
    	    } catch (InvocationTargetException e) {
    	    e.printStackTrace();
    	    }
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			//timer.schedule(task,500,2000);
//            if(timer == null){
//                timer = new Timer();
//                if (task == null){
//                    task=new OneTask();
//                }
//                timer.schedule(task,500,1000);
//            }
            receiver.startTimer();

            break;
		case R.id.button2:
			//timer.cancel();
//            if (timer != null){
//                timer.cancel();
//                timer.purge();
//                timer	= null;
//                if (task != null){
//                    task.cancel();
//                    task = null;
//                }
//            }
            receiver.stopTimer();
			break;
		}
	}
	public static final String getDate(){        
        Calendar cal = Calendar.getInstance();        
        java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String cdate = sdf.format(cal.getTime());                
        System.out.println("times is :"+cdate);        
        return cdate;    
  }
}
