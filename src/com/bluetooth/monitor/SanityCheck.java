package com.bluetooth.monitor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.content.Context;

import android.util.Log;
import android.app.Activity;
import android.widget.Toast;

public class SanityCheck {

  private static Matcher matcher;
  private static Pattern pattern;

  private static Activity activity;

  private static boolean canStartVal;

  private static String tag(String method_name) {
    return "BluetoothMonitor SanityCheck() " + method_name;
  }

  public SanityCheck(Activity activity) {
    this.activity = activity;
  }

  public static void toast(String text) {
    Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
  }

  public static boolean canStart() {
    return canStartVal;
  }

  public static void canStartSetter(boolean start) {
    canStartVal = start;
  }

  public static boolean isEmpty(String duration) {
    if(duration == null) {
      Log.d(tag("isEmpty"), "isEmpty(null)");
      canStartVal = false;
      return true;
    }
    else if(duration.length() == 0) {
      toast("Duration cannot be empty!");
      Log.d(tag("isEmpty"), "isEmpty(true)");
      canStartVal = false;
      return true;
    } 
    else {
      Log.d(tag("isEmpty"), "!isEmpty(false)");
      canStartVal = true;
      return false;
    }
  }

  public static boolean isLengthCorrect(String string, int length, String message) {
    if(string.length() == length) {
      canStartVal = true;
      return true;
    }
    else {
      toast(message);
      canStartVal = false;
      return false;
    }
  }

  public static boolean isGreaterThan(String duration, int number, String message) {
    if(Integer.parseInt(duration) > number) {
      toast(message);
      canStartVal = false;
      return true;
    }
    else {
      canStartVal = true;
      return false;
    }
  }

  public static boolean isLessThan(String duration, int number, String message) {
    if(Integer.parseInt(duration) < number) {
      toast(message);
      canStartVal = false;
      return true;
    }
    else {
      canStartVal = true;
      return false;
    }
  }

}
