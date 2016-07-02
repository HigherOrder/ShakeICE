package com.progfun.app.shakeice.ui;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import com.progfun.app.shakeice.R;
import com.progfun.app.shakeice.common.Common;
import com.progfun.app.shakeice.common.Constants;
import com.progfun.app.shakeice.controllers.IntentServiceGForceWatcher;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public
class MainActivity
  extends AppCompatActivity {

  private final String TAG = this.getClass() .getSimpleName();

  private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
  private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

  private Context c;

  private IntentService intentService;

  private boolean isServiceBound = false;

  ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

  private Common common;

  private double gForce = 0.0;

  private TextView tvCurrentForceValue;

  @Override
  protected
  void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    DisplayMetrics metrics = getResources().getDisplayMetrics();
    Log.i(TAG, "Screen resolution = " + metrics.widthPixels + " x " + metrics.heightPixels);
    Log.i(TAG, "Density (dpi) = " + metrics.densityDpi);
    Log.i(TAG, "Density (pixels) = " + metrics.xdpi + " x " + metrics.ydpi);

    c = getApplicationContext(); // Get the app's context.

    common = new Common(c);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    TextView tvTitle = (TextView) toolbar.findViewById(R.id.tvBody);
    tvTitle.setTextColor(getResources().getColor(R.color.text_title));
    //    tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/timeburner_regular.ttf"));

    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowHomeEnabled(true);
    actionBar.setDisplayShowTitleEnabled(false);

    tvCurrentForceValue = (TextView) findViewById(R.id.tvCurrentForceValue);
  }

  private ServiceConnection serviceConnection = new ServiceConnection() {

    public
    void onServiceConnected(
      ComponentName className,
      IBinder service
    ) {
      // This is called when the connection with the service has been
      // established, giving us the service object we can use to
      // interact with the service.  Because we have bound to a explicit
      // service that we know is running in our own process, we can
      // cast its IBinder to a concrete class and directly access it.
      IntentServiceGForceWatcher.BinderShakeIce b = (IntentServiceGForceWatcher.BinderShakeIce) service;
      intentService = b.getService();
      Log.i(
        TAG,
        "Connected to IntentServiceGForceWatcher."
      );
      isServiceBound = true;

      // As long as we are bound to the service, fetch the gForce value periodically.
      startUpdatingGForce();
    }

    public
    void onServiceDisconnected(ComponentName className) {
      // This is called when the connection with the service has been
      // unexpectedly disconnected -- that is, its process crashed.
      // Because it is running in our same process, we should never
      // see this happen.
      Log.i(TAG, "Disconnected from IntentServiceGForceWatcher.");

      stopUpdatingGForce(); // Stop fetching the gForce value periodically if we are not bound to the service,

      intentService = null;
      isServiceBound = false;
    }
  };

  public
  void startUpdatingGForce() {
    Log.i(TAG, "startUpdatingGForce: ....");

    scheduledThreadPoolExecutor.scheduleAtFixedRate(
      new Runnable() {
        @Override
        public
        void run() {
          gForce = ((IntentServiceGForceWatcher) intentService).getGForce();
          mHandler.obtainMessage(1)
                  .sendToTarget();
        }
      },
      0,
      Constants.INTERVAL_UPDATES_GFORCE_MILLISECONDS,
      TimeUnit.MILLISECONDS
    );
  }

  public Handler mHandler = new Handler() {
    public
    void handleMessage(Message msg) {
      String sGForce = String.format(
        "%.2f",
        gForce
      );
      //      Log.i(TAG, "onSensorChanged: gForce = " + sGForce);
      tvCurrentForceValue.setText(sGForce);
      //      if (gForce > gForceMax) {
      //        gForceMax = gForce;
      //
      //        sGForceMax = String.format("%.2f", gForceMax);
      //        MainActivity.tvMaxForceValue.setText(sGForceMax);
      //
      //        // Save the gForceMax for future reference.
      //        common.saveSpString(Constants.SP_KEY_GFORCE_MAX, sGForceMax);
      //      }
      //
      //      if (gForce < gForceMin) {
      //        gForceMin = gForce;
      //
      //        sGForceMin = String.format("%.2f", gForceMin);
      //        MainActivity.tvMinForceValue.setText(sGForceMin);
      //
      //        // Save the gForceMax for future reference.
      //        common.saveSpString(Constants.SP_KEY_GFORCE_MIN, sGForceMin);
      //      }
    }
  };

  private
  void stopUpdatingGForce() {
    Log.i(
      TAG,
      "stopUpdatingGForce: ...."
    );
    scheduledThreadPoolExecutor.purge();
    scheduledThreadPoolExecutor.shutdown();
  }

  @Override
  protected
  void onStart() {
    super.onStart();

    doBindService();
  }

  void doBindService() {
    Log.i(TAG, "doBindService: binding to the service....");

    // Establish a connection with the service.  We use an explicit
    // class name because we want a specific service implementation that
    // we know will be running in our own process (and thus won't be
    // supporting component replacement by other applications).
    bindService(
      new Intent(c, IntentServiceGForceWatcher.class), serviceConnection,
      Context.BIND_AUTO_CREATE
    );
  }

  @Override
  protected
  void onResume() {
    super.onResume();

    if (!common.has(Constants.SP_KEY_THRESHOLD_GFORCE)) {
      common.saveSpDouble(
        Constants.SP_KEY_THRESHOLD_GFORCE,
        Constants.SP_VALUE_THRESHOLD_GFORCE_MEDIUM
      );
    }
      Log.i(TAG, "onResume: " + Constants.SP_KEY_THRESHOLD_GFORCE + " = " + common.fetchSpThresholdGForce());
  }

  @Override
  protected
  void onPause() {
    super.onPause();
  }

  @Override
  protected
  void onStop() {
    super.onStop();
  }

  private
  void doUnbindService() {
    if (isServiceBound) {
      Log.i(TAG, "doUnbindService: unbinding from service....");
      unbindService(serviceConnection); // Detach our existing connection.
    }
  }

  @Override
  protected
  void onDestroy() {
    super.onDestroy();

    doUnbindService();

    scheduledThreadPoolExecutor = null;

    // TODO - Unregister the receiver.
    // unregisterReceiver(receiverSms);
    //receiverSms = null;
  }

}
