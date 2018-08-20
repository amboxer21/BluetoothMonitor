package com.bluetooth.monitor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.content.Context;

import android.app.Activity;
import android.widget.Toast;

public class SanityCheck {

  private static Matcher matcher;
  private static Pattern pattern;

  private static Activity activity;

  private static boolean canStartVal;

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

  public static boolean isGreaterThan(int duration, int number, String message) {
    if(duration > number) {
      toast(message);
      canStartVal = false;
      return true;
    }
    else {
      canStartVal = true;
      return false;
    }
  }

  public static boolean isLessThan(int duration, int number, String message) {
    if(duration < number) {
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
