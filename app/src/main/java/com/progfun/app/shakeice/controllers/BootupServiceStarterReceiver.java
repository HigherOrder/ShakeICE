package com.progfun.app.shakeice.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by p on 01/11/14.
 */
public class BootupServiceStarterReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context c, Intent i) {
    if (Intent.ACTION_BOOT_COMPLETED.equals(i.getAction())) {
      Intent serviceIntent = new Intent(c, IntentServiceGForceWatcher.class);
      c.startService(serviceIntent);
    }
  }



}
