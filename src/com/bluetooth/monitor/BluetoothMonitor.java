package com.bluetooth.monitor;

import java.util.List;
import android.util.Log;
import java.util.ArrayList;
import android.app.Activity;
import java.lang.reflect.Field;

import android.view.View;
import android.view.View.OnClickListener;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemSelectedListener;

public class BluetoothMonitor extends Activity implements OnItemSelectedListener {

  private Spinner dropdown;
  private Button configure;
  private EditText duration;

  private boolean mBound;
  private boolean message;

  private String sAddress;
  private String sDuration;
  private String sAddressDb;
  private String sDurationDb;
  private String bluetoothMacAddress = "";

  private ArrayAdapter adapter;
  private SanityCheck sanityCheck;
  private Messenger mService = null;
  private Intent bluetoothMonitorIntent;
  private BluetoothDevice bluetoothDevice;
  private DatabaseHandler databaseHandler;
  private BluetoothAdapter bluetoothAdapter;
  private BluetoothMonitorService bluetoothMonitorService;

  private long backPressedTime = 0;
  private final String TAG = "BluetoothMonitor BluetoothMonitor()";

  public  static final int MSG_STRING = 0;
  
  private static Message msg;

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
        AppendDeviceToList(bluetoothDevice.getName());
        Toast.makeText(context, "Connected to "+bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
      }
      else if(action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
        bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        sAddress = bluetoothDevice.getName();
				removeDeviceFromList(bluetoothDevice.getName());
        Toast.makeText(context, "Disconnected from "+bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
      }
    }
  };

  private void AppendDeviceToList(String bluetoothDeviceName) {
		adapter.add(bluetoothDeviceName);
		adapter.notifyDataSetChanged();
		dropdown.setAdapter(adapter);
  }

  private void removeDeviceFromList(String bluetoothDeviceName) {
		adapter.remove(bluetoothDeviceName);
		adapter.notifyDataSetChanged();
		dropdown.setAdapter(adapter);
  }
 
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

    configure = (Button)findViewById(R.id.button);
    dropdown  = (Spinner)findViewById(R.id.device_list_menu);
    duration  = (EditText)findViewById(R.id.edit_timer_duration);

    sAddress = getBluetoothName();
    bluetoothMonitorService = new BluetoothMonitorService();

    RelativeLayout mView = (RelativeLayout) findViewById(R.id.bluetooth_monitor);
    sanityCheck = new SanityCheck(this);
    databaseHandler = new DatabaseHandler(getApplicationContext());
    getSetDatabaseInfo(mView);

    Intent serviceIntent = new Intent(this, BluetoothMonitorService.class);
    startService(serviceIntent);

    IntentFilter filters = new IntentFilter();
    filters.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    filters.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
    filters.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

    registerReceiver(BluetoothMonitorReceiver, filters);

    dropdown.setOnItemSelectedListener(this);
    List<String> options = new ArrayList<String>();
    options.add(getBluetoothName());
    adapter  = new ArrayAdapter<String>(this, R.layout.device_list, R.id.device_list_textview, options);
    dropdown.setAdapter(adapter);

    configure.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        bluetoothMonitorSanityCheck(view);
      }
    });
  }

  public void bluetoothMonitorSanityCheck(View view) {
    try {
      sDuration = duration.getText().toString();
    }
    catch(Exception e) {
      e.printStackTrace();
      Log.e(TAG, "entrySanityCheck() Exception e => " + e.getMessage());
    }
    /*if(sAddress.equals(getBluetoothName())) {
      Toast.makeText(getApplicationContext(), "Chose a device other than your own!", Toast.LENGTH_LONG).show(); 
    }
    else if(sanityCheck.isEmpty(sDuration)) {*/
    if(sanityCheck.isEmpty(sDuration)) {
      return;
    }
    else if(sanityCheck.isLessThan(sDuration,300,"Duration must be greater than 300 seconds!")) {
      return;
    }
    else {
     getSetDatabaseInfo(view);
    }
  }

  public void getSetDatabaseInfo(View view) throws NullPointerException {

    List<BluetoothMonitorDatabase> bluetoothMonitorDatabase;

    try {
      bluetoothMonitorDatabase = databaseHandler.getAllBluetoothMonitorDatabase();
    }
    catch(NullPointerException e) {
      Log.d(TAG, "bluetoothMonitorDatabase == null.");
      return;
    }

    for(BluetoothMonitorDatabase item : bluetoothMonitorDatabase) {
      sAddressDb  = item.getAddress().toString();
      sDurationDb = String.valueOf(item.getDuration()).toString();

      if(sDurationDb != null) {
        duration.setText(sDurationDb);
      }
    }
    if(!sanityCheck.isEmpty(sDuration)) {
      if(sDurationDb != null && sAddressDb != null) {
        Log.d(TAG, "Updating db with -> sAddressDb = " + sAddress + ": sDurationDb = " + sDuration);
        Toast.makeText(getApplicationContext(), "Updating database.", Toast.LENGTH_LONG).show();
        databaseHandler.updateBluetoothMonitorDatabase(new BluetoothMonitorDatabase(1, sAddress, Integer.parseInt(sDuration)));
        bluetoothMonitorService.stopTimer();
        bluetoothMonitorService.startTimer(Integer.parseInt(sDuration));
      }
      else {
        Log.d(TAG, "Creating db with -> sAddressDb = " + sAddress + ": sDurationDb = " + sDuration);
        Toast.makeText(getApplicationContext(), "Creating database.", Toast.LENGTH_LONG).show();
        databaseHandler.addBluetoothMonitorDatabase(new BluetoothMonitorDatabase(1, sAddress, Integer.parseInt(sDuration)));
        bluetoothMonitorService.stopTimer();
        bluetoothMonitorService.startTimer(Integer.parseInt(sDuration));
      }
    }

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
    String item = parent.getItemAtPosition(position).toString();
    if(item != getBluetoothName()) {
      sAddress = item;
      Log.d(TAG, "Item = " + item);
      Log.d(TAG, "getBluetoothName() = " + getBluetoothName());
      Toast.makeText(getApplicationContext(), "Item selected: " + item, Toast.LENGTH_LONG).show();
    }
    else {
      Log.d(TAG, "Item selected: " + getBluetoothName());
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) { }

}
