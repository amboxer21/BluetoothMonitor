package com.bluetooth.monitor; 
 
public class BluetoothMonitorDatabase {
     
  int _id;
  int _duration;
  String _address;

  public BluetoothMonitorDatabase() { }
     
  public BluetoothMonitorDatabase(int id, String address, int duration) {
    this._id       = id;
    this._address  = address;
    this._duration = duration;
  }
     
  public int getID() {
    return this._id;
  }

  public String getAddress() {
    return this._address;
  }

  public int getDuration() {
    return this._duration;
  }

  public void setID(int id) {
    this._id = id;
  }

  public void setAddress(String address) {
    this._address = address;
  }

  public void setDuration(int duration) {
    this._duration = duration;
  }

}
