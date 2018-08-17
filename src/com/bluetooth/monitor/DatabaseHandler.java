package com.bluetooth.monitor;

import android.util.Log;

import java.util.List;
import java.util.ArrayList;
 
import android.content.Context;
import android.content.ContentValues;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
 
  private static final int DATABASE_VERSION = 1;
 
  private static final String KEY_ID        = "id";
  private static final String KEY_ADDRESS   = "address";
  private static final String KEY_DURATION  = "duration";

  private static final String TABLE_OPTIONS = "devices";
  private static final String DATABASE_NAME = "bluetooth_monitor";

  private static final String TAG = "BluetoothMonitor";
 
  public DatabaseHandler(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
 
  @Override
  public void onCreate(SQLiteDatabase db) {
    String CREATE_OPTIONS_TABLE = "CREATE TABLE " + TABLE_OPTIONS + "("
      + KEY_ID + " INTEGER PRIMARY KEY," 
      + KEY_ADDRESS + " TEXT DEFAULT '',"
      + KEY_DURATION + " INT DEFAULT 0);";
    db.execSQL(CREATE_OPTIONS_TABLE);
  }
 
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPTIONS);
    onCreate(db);
  }

  void addBluetoothMonitorDatabase(BluetoothMonitorDatabase bluetoothMonitorDatabase) {
    Log.d(TAG, "public int addBluetoothMonitorDatabase()");
    SQLiteDatabase db = this.getWritableDatabase();
 
    ContentValues values = new ContentValues();
    values.put(KEY_ADDRESS, bluetoothMonitorDatabase.getAddress()); 
    values.put(KEY_DURATION, bluetoothMonitorDatabase.getDuration()); 
 
    db.insert(TABLE_OPTIONS, null, values);
    db.close(); 
  }
 
  BluetoothMonitorDatabase getBluetoothMonitorDatabase(int id) {
    SQLiteDatabase db = this.getReadableDatabase();
 
    Cursor cursor = db.query(TABLE_OPTIONS, 
      new String[] { KEY_ID, KEY_ADDRESS, KEY_DURATION }, KEY_ID + "=?",
      new String[] { String.valueOf(id) }, null, null, null, null);
      if (cursor != null) {
        cursor.moveToFirst();
      }
 
      BluetoothMonitorDatabase bluetoothMonitorDatabase = new BluetoothMonitorDatabase(Integer.parseInt(cursor.getString(0)),
        cursor.getString(1), Integer.parseInt(cursor.getString(2)));

      if(cursor != null) {
        cursor.close();
      }
      return bluetoothMonitorDatabase;
  }
     
  public List<BluetoothMonitorDatabase> getAllBluetoothMonitorDatabase() {

    List<BluetoothMonitorDatabase> bluetoothMonitorDatabaseList = new ArrayList<BluetoothMonitorDatabase>();

    String selectQuery = "SELECT  * FROM " + TABLE_OPTIONS;
 
    SQLiteDatabase db  = this.getWritableDatabase();
    Cursor cursor      = db.rawQuery(selectQuery, null);

    if (cursor.moveToFirst()) {
      do {
        BluetoothMonitorDatabase bluetoothMonitorDatabase = new BluetoothMonitorDatabase();
        bluetoothMonitorDatabase.setID(Integer.parseInt(cursor.getString(0)));
        bluetoothMonitorDatabase.setAddress(cursor.getString(1));
        bluetoothMonitorDatabase.setDuration(Integer.parseInt(cursor.getString(2)));
        bluetoothMonitorDatabaseList.add(bluetoothMonitorDatabase);
      } while (cursor.moveToNext());
    }
    if(cursor != null) {
      cursor.close();
    }
    return bluetoothMonitorDatabaseList;
  }
 
  public int updateBluetoothMonitorDatabase(BluetoothMonitorDatabase bluetoothMonitorDatabase) {
    Log.d("FlashLight","public int updateBluetoothMonitorDatabase()");
    SQLiteDatabase db    = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(KEY_ADDRESS, bluetoothMonitorDatabase.getAddress());
    values.put(KEY_DURATION, bluetoothMonitorDatabase.getDuration());
 
    return db.update(TABLE_OPTIONS, values, KEY_ID + " = ?",
      new String[] { String.valueOf(bluetoothMonitorDatabase.getID()) });
  }
 
  public void deleteBluetoothMonitorDatabase(BluetoothMonitorDatabase bluetoothMonitorDatabase) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(TABLE_OPTIONS, KEY_ID + " = ?",
      new String[] { String.valueOf(bluetoothMonitorDatabase.getID()) });
    db.close();
  }

}
