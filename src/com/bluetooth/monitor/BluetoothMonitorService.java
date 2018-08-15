package com.bluetooth.monitor;

import android.util.Log;
import android.app.Service;
import android.widget.Toast;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.os.Messenger;

import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BluetoothMonitorService extends Service {

  private static boolean is_screen_off = true;

  @Override
  public IBinder onBind(Intent intent) {
    return mMessenger.getBinder();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d("BluetoothMonitorService", "Service started.");

    Bundle extras = intent.getExtras(); 

    if(extras != null) {
      if(extras.getString("state").equals("on")) {
        is_screen_off = true;
        Log.d("BluetoothMonitorService is_screen_off", "true");
      }
      else {
        is_screen_off = false;
        Log.d("BluetoothMonitorService is_screen_off", "false");
      }
    }
    return Service.START_STICKY;
  }

  class IncomingHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case 0:
          if(msg.obj.toString().equals("off")) {
            Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
          }
          else if(msg.obj.toString().equals("on")) {
            Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
          }
        break;
        default:
          super.handleMessage(msg);
      }
    }
  }
 
  final Messenger mMessenger = new Messenger(new IncomingHandler());

}
