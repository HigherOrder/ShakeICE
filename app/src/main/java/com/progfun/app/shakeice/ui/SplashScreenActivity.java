package com.progfun.app.shakeice.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.progfun.app.shakeice.R;
import com.progfun.app.shakeice.common.Common;
import com.progfun.app.shakeice.common.Constants;

/**
 * This activity will present a custom image or the company logo to
 * the user for a few seconds before launching the main activity.
 */
public class SplashScreenActivity
  extends Activity {

  private final String TAG = this.getClass().getSimpleName();

  private Context c;

  private Common common;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    
    String TAG2 = "onCreate: ";
    Log.i(TAG, TAG2);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);
    
    c = getApplicationContext();
    common = new Common(c);
    
    (new Handler()).postDelayed(
            new Runnable() {
              @Override
              public void run() {
                String TAG2 = "new Runnable().run: ";
                Log.i(TAG, TAG2);

                Intent i;
                
//                if (!common.has(Constants.SP_KEY_HAS_BEEN_LAUNCHED_BEFORE)) { // This is the first time that the user has launched the app, so set the preference accordingly.
//                  i = new Intent(getApplicationContext(), WelcomeScreenActivity.class);
//                  common.saveSharedPreferenceBoolean(Constants.SP_KEY_HAS_BEEN_LAUNCHED_BEFORE, true);
//                }
//                else {
//                  i = new Intent(getApplicationContext(), MainActivity.class);
//                }
                
                i = new Intent(c, MapActivity.class);
                startActivity(i);
                finish();
              }
            }, Constants.TIMEOUT_SPLASH_SCREEN);
  }

}

