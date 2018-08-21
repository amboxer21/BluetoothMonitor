package com.bluetooth.monitor;

import android.util.Log;
import android.app.Service;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.content.Context;;

import android.os.Looper;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.os.Messenger;

import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BluetoothMonitorService extends Service {

  public  static final int MSG_STRING = 0;
  private DatabaseHandler databaseHandler;
  private static final String TAG = "BluetoothMonitorService";

  @Override
  public IBinder onBind(Intent intent) {
    return mMessenger.getBinder();
  }

  @Override
  public void onTaskRemoved(Intent rootIntent) {
    super.onTaskRemoved(rootIntent);
    Log.d(TAG,"onTaskRemoved()");
  }

  public static class ShutDownPhone { 

    private static Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public ShutDownPhone(Context context) {
      this.context = context;
    }

    public void startTimer(final int seconds) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            Thread.sleep(10000); // Sleep for 10 seconds - Use this for testing.
            //Thread.sleep(seconds * 1000);
            handler.post(new Runnable() {
              @Override
              public void run() {
                try {
                  Toast.makeText(context, "Shutting phone down now!", Toast.LENGTH_LONG).show();
                }
                catch(Exception e) { }
              }
            });
          }
          catch(Exception e) { }
        }
      }).start();
    }

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
		databaseHandler = new DatabaseHandler(this);
    return Service.START_STICKY;
  }

  class IncomingHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case 0:
        break;
        default:
          super.handleMessage(msg);
      }
    }
  }
 
  final Messenger mMessenger = new Messenger(new IncomingHandler());

}
