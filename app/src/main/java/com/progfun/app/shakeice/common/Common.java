package com.progfun.app.shakeice.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by p on 22/11/14.
 */
public class Common {

  private final String TAG = this.getClass().getSimpleName();

  public final String SP_FILE = "sp-shakeice";

  private Context c;

  private SharedPreferences sp;
  private SharedPreferences.Editor spEditor;

  public Common(Context context) {
    String TAG2 = "Common: ";
    Log.i(TAG, TAG2);

    c = context;
    sp = c.getSharedPreferences(Constants.SP_FILE, Context.MODE_PRIVATE);
    spEditor = sp.edit();
  }

  public Boolean isTablet() {
    String TAG2 = "isTablet: ";
    Log.i(TAG, TAG2);

    int sizeOfScreen = (c.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
    return (sizeOfScreen >= Configuration.SCREENLAYOUT_SIZE_LARGE) ? true : false;
  }

  public void saveSpString(String key, String val) {
    String TAG2 = "saveSpString: ";
    Log.i(TAG, TAG2);

    spEditor.putString(key, val);
    spEditor.commit();
  }

  public void saveSpDouble(String key, double val) {
    String TAG2 = "sendAlerts: ";
    Log.i(TAG, TAG2);

    spEditor.putFloat(
      key,
      (float) val
    );
    spEditor.commit();
  }

  public void saveSpBoolean(String key, boolean val) {
    String TAG2 = "saveSpBoolean: ";
    Log.i(TAG, TAG2);

    spEditor.putBoolean(
      key,
      val
    );
    spEditor.commit();
  }

  public boolean fetchSpIsFirstLaunch() {
    String TAG2 = "fetchSpIsFirstLaunch: ";
    Log.i(TAG, TAG2);

    return sp.getBoolean(Constants.SP_KEY_WAS_LAUNCHED_BEFORE, Constants.SP_VALUE_DEFAULT_WAS_LAUNCHED_BEFORE);
  }

  public String fetchSpUsersPhoneNumber() {
    String TAG2 = "fetchSpUsersPhoneNumber: ";
    Log.i(TAG, TAG2);

    return sp.getString(
      Constants.SP_KEY_USERS_PHONE_NUMBER,
      Constants.SP_VALUE_DEFAULT_USERS_PHONE_NUMBER
    );
  }

  public String fetchSpGForceMax() {
    String TAG2 = "fetchSpGForceMax: ";
    Log.i(TAG, TAG2);

    return sp.getString(
      Constants.SP_KEY_GFORCE_MAX,
      Constants.SP_VALUE_DEFAULT_GFORCE_MAX
    );
  }

  public String fetchSpGForceMin() {
    String TAG2 = "fetchSpGForceMin: ";
    Log.i(TAG, TAG2);

    return sp.getString(Constants.SP_KEY_GFORCE_MIN, Constants.SP_VALUE_DEFAULT_GFORCE_MIN);
  }

  public String fetchSpLocation() {
    String TAG2 = "fetchSpLocation: ";
    Log.i(TAG, TAG2);

    return sp.getString(
      Constants.SP_KEY_LOCATION,
      Constants.SP_VALUE_DEFAULT_LOCATION
    );
  }

  public boolean fetchSpIsAlertEnabled() {
    String TAG2 = "fetchSpIsAlertEnabled: ";
    Log.i(TAG, TAG2);

    return sp.getBoolean(
      Constants.PREF_KEY_SMS_ENABLE,
      Constants.PREF_VALUE_DEFAULT_SMS_ENABLE
    );
  }

  public double fetchSpThresholdGForce() {
    String TAG2 = "fetchSpThresholdGForce: ";
//    Log.i(TAG, TAG2);

    return sp.getFloat(
      Constants.SP_KEY_THRESHOLD_GFORCE,
      (float) Constants.SP_VALUE_THRESHOLD_GFORCE_MEDIUM
    );
  }

  public String fetchSpMessageForAlert() {
    String TAG2 = "fetchSpMessageForAlert: ";
    Log.i(TAG, TAG2);

    return sp.getString(
      Constants.SP_KEY_MESSAGE_FOR_ALERT,
      Constants.SP_VALUE_DEFAULT_MESSAGE_FOR_ALERT
    );
  }

  public String fetchSpNumberPrimary() {
    String TAG2 = "fetchSpNumberPrimary: ";
    Log.i(TAG, TAG2);

    return sp.getString(Constants.SP_KEY_NUMBER_PRIMARY, Constants.SP_VALUE_DEFAULT_NUMBER_PRIMARY);
  }
  
  public boolean has(String key) {
    String TAG2 = "has: ";
    Log.i(TAG, TAG2);

    return sp.contains(key);
  }

  public void delete(String key) {
    String TAG2 = "delete: ";
    Log.i(TAG, TAG2);

    spEditor.remove(key);
    spEditor.commit();
  }

  public void sendSms(String number, String message) {
    String TAG2 = "sendSms: ";
    Log.i(TAG, TAG2);

    Log.i(TAG, "sendSms: number = " + number + ", message = " + message);
    try {
      SmsManager sm = SmsManager.getDefault();
      sm.sendTextMessage(number /* number to send SMS to */, null /* source address - number to send SMS to */, message /* Text of the SMS */, null /* sentIntent */, null /* deliveryIntent */);
      Log.i(TAG, "sendSms: " + String.format("%s %s %s", number, " sent ", message));
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void dialNumber(String number) {
    String TAG2 = "dialNumber: ";
    Log.i(TAG, TAG2);

    try {
      Intent intent = new Intent(Intent.ACTION_CALL);
      intent.setData(Uri.parse("tel:" + number));
      c.startActivity(intent);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }
  
  public double round(double value, int places) {
    String TAG2 = "round: ";
//    Log.i(TAG, TAG2);

    if (places < 0)
      throw new IllegalArgumentException();

    long factor = (long) Math.pow(10, places);
    value = value * factor;
    long tmp = Math.round(value);
    return (double) tmp / factor;
  }

}
