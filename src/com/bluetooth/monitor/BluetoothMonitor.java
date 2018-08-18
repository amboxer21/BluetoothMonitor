package com.bluetooth.monitor;

import java.util.List;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import android.app.Activity;
import java.lang.reflect.Field;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;

import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.BroadcastReceiver;

import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;

public class BluetoothMonitor extends Activity implements OnItemSelectedListener {

  private static Spinner dropdown;
  private static ArrayAdapter adapter;
  private static BluetoothDevice bluetoothDevice;
  private static BluetoothAdapter bluetoothAdapter;

  private boolean mBound;
  private boolean message;
  private Messenger mService = null;
  private static long backPressedTime = 0;
  private static Intent bluetoothMonitorIntent;

  private static String bluetoothMacAddress = "";

  private static final String TAG = "BluetoothMonitor";

  private BroadcastReceiver BluetoothMonitorReceiver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {

      final String action = intent.getAction();

      if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
        switch(state) {
          case BluetoothAdapter.STATE_OFF:
            Toast.makeText(context, "Bluetooth off", Toast.LENGTH_LONG).show();
            break;
          case BluetoothAdapter.STATE_ON:
            Toast.makeText(context, "Bluetooth on", Toast.LENGTH_LONG).show();
            break;
        }
      }
      if(action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
        bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        Toast.makeText(context, "Connected to "+bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
				adapter.add(bluetoothDevice.getName());
				adapter.notifyDataSetChanged();
				dropdown.setAdapter(adapter);
      }
      else if(action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
        bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        Toast.makeText(context, "Disconnected from "+bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
				adapter.remove(bluetoothDevice.getName());
				adapter.notifyDataSetChanged();
				dropdown.setAdapter(adapter);
      }
    }
  };

  private String getBluetoothMacAddress() {
    return BluetoothAdapter.getDefaultAdapter().getAddress().toString();
  }

  private String getBluetoothName() {
    return BluetoothAdapter.getDefaultAdapter().getName().toString();
  }

  @Override
  protected void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putBoolean("message", message);
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    message = savedInstanceState.getBoolean("message");
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    if(savedInstanceState != null) {
      message = savedInstanceState.getBoolean("message");
    }

    Intent serviceIntent = new Intent(this, BluetoothMonitorService.class);
    startService(serviceIntent);

    IntentFilter filters = new IntentFilter();
    filters.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    filters.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
    filters.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

    registerReceiver(BluetoothMonitorReceiver, filters);

    dropdown = (Spinner)findViewById(R.id.device_list_menu);
    dropdown.setOnItemSelectedListener(this);
    List<String> options = new ArrayList<String>();
    options.add(getBluetoothName());
    adapter  = new ArrayAdapter<String>(this, R.layout.device_list, R.id.device_list_textview, options);
    dropdown.setAdapter(adapter);
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

  public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
    switch(position) {
      case 0:
        // Whatever you want to happen when the first item gets selected
        break;
      case 1:
        // Whatever you want to happen when the second item gets selected
        break;
      case 2:
        // Whatever you want to happen when the thrid item gets selected
        break;
    }
  }

  public void onNothingSelected(AdapterView<?> parent) { }

}
