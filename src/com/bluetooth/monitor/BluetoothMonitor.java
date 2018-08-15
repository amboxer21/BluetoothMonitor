package com.bluetooth.monitor;

import android.util.Log;
import android.widget.Toast;

import android.app.Activity;
 
import android.bluetooth.BluetoothAdapter;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;

import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.BroadcastReceiver;

public class BluetoothMonitor extends Activity {

  private boolean mBound;
  private Messenger mService = null;
  private static long backPressedTime = 0;
  private static Intent bluetoothMonitorIntent;

  private BroadcastReceiver BluetoothMonitorReceiver = new BroadcastReceiver() {    
    @Override
    public void onReceive(Context context, Intent intent) {

      final String action = intent.getAction();

      if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
          BluetoothAdapter.ERROR);
        switch(state) {
          case BluetoothAdapter.STATE_OFF:
            Toast.makeText(context, "Bluetooth off", Toast.LENGTH_LONG).show();
            break;
          case BluetoothAdapter.STATE_TURNING_OFF:
            Toast.makeText(context, "Turning Bluetooth off", Toast.LENGTH_LONG).show();
            break;
          case BluetoothAdapter.STATE_ON:
            Toast.makeText(context, "Bluetooth on", Toast.LENGTH_LONG).show();
            break;
          case BluetoothAdapter.STATE_TURNING_ON:
            Toast.makeText(context, "Turning Bluetooth on", Toast.LENGTH_LONG).show();
            break;
        }
      } 
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    Intent serviceIntent = new Intent(this, BluetoothMonitorService.class);
    startService(serviceIntent);

    registerReceiver(BluetoothMonitorReceiver,
      new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

  }

  @Override
  public void onBackPressed() {
    long mTime = System.currentTimeMillis();
    if(mTime - backPressedTime > 2000) {
      backPressedTime = mTime;
      Toast.makeText(this, "Press back again to close app.", Toast.LENGTH_SHORT).show();
    }
    else {
      super.onBackPressed();
    }
  }

  /*private ServiceConnection mConnection = new ServiceConnection() {

    @Override
    public void onServiceDisconnected(ComponentName name) {
      mService = null;
      mBound   = false;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      mService = new Messenger(service);
      mBound   = true;
    }
  };*/ 

}
