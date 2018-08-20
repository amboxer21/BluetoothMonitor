package com.bluetooth.monitor;

import android.util.Log;
import android.app.Service;
import android.widget.Toast;

import android.content.Intent;
import android.content.Context;;

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
	private static final String TAG = "BluetoothMonitorService";

  public  static final int MSG_STRING = 0;

  @Override
  public IBinder onBind(Intent intent) {
    return mMessenger.getBinder();
  }

  @Override
  public void onTaskRemoved(Intent rootIntent) {
    super.onTaskRemoved(rootIntent);
    Log.d(TAG,"onTaskRemoved()");
  }
 
  public void onCreate(Context context, Intent intent) throws NullPointerException {
    try {
      if(intent.getAction() != null) {
        intent = new Intent(context, BluetoothMonitor.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
      }
    }
    catch(NullPointerException e) {
      Log.d(TAG, "onCreate Null Pointer Exception e => " + e.toString());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(TAG, "Service started.");
    return Service.START_STICKY;
  }

  class IncomingHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case 0:
          if(msg.obj.toString().equals("updated")) {
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
