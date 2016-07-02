package com.progfun.app.shakeice.controllers;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ResultReceiver;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.progfun.app.shakeice.R;
import com.progfun.app.shakeice.common.Common;
import com.progfun.app.shakeice.common.Constants;
import com.progfun.app.shakeice.dal.DataAccessObject;
import com.progfun.app.shakeice.location.IntentServiceAddressFetcher;
import com.progfun.app.shakeice.ui.MainActivity;
import com.progfun.app.shakeice.ui.MapActivity;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */

public
class IntentServiceGForceWatcher
  extends IntentService
  implements SensorEventListener {

  private final String TAG = this.getClass()
                                 .getSimpleName();

  private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
  private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

  private final int ID_NOTIFICATION_PERSISTENT = 1728;
  private final int ID_NOTIFICATION = 1729;

  private final int DURATION_VIBRATOR_MILLISECONDS = 720;

  private double gForce = 0;

  private Context c;

  private Common common;

  private boolean enableAlert;

  private double thresholdGForce;

  private String messageAlert;

  private SensorManager sensorManager;
  private Sensor sensorAccelerometer;

  private boolean recordingValuesGforce = false;

  private ArrayList<Double> valuesGforce = new ArrayList<>();

  private final IBinder serviceBinder = new BinderShakeIce();

  private GoogleApiClient googleApiClient;

  private LocationRequest locationRequest;

  private Location locationLast;

  private ReceiverResultAddress receiverResultAddress;

  private String latitude;
  private String longitude;

  private String latLong;
  private String address;
  private String location;

  private boolean wasRequestedAddress = false;

  protected
  class ReceiverResultAddress
    extends ResultReceiver {

    public
    ReceiverResultAddress(Handler handler) {
      super(handler);
    }

    @Override
    protected
    void onReceiveResult(
      int resultCode,
      Bundle resultData
    ) {
      String TAG2 = "onReceiveResult: ";
      Log.i(TAG, TAG2);

      address = resultData.getString(Constants.RESULT_DATA_KEY);
      latLong = getLatLong();
      location = getString(R.string.error_location_unavailable);

      if (resultCode == Constants.SUCCESS_RESULT) { // The street address is available and valid; save it
        location = address;
      }
      else { // Save just the lat-long
        location = latLong;
      }

      Log.i(TAG, "onReceiveResult: resultCode = " + resultCode + ", address = " + address + ", latLong = " + latLong + ", location = " + location);

      sendAlerts(getString(R.string.s_sms_alert_location) + " " + location); // Send the alert containing the user's location.
      common.saveSpString(Constants.SP_KEY_LOCATION, location); // Send the alert containing the user's location.
      Toast.makeText(c, getString(R.string.s_toast_location) + " " + location, Toast.LENGTH_LONG).show();
    }

//    public
//    final Parcelable.Creator CREATOR = new Parcelable.Creator() {
//      public
//      ReceiverResultAddress createFromParcel(Parcel in) {
//        return new ReceiverResultAddress(new Handler());
//      }
//
//      public
//      ReceiverResultAddress[] newArray(int size) {
//        return new ReceiverResultAddress[size];
//      }
//
//    };
//
//    public
//    int describeContents() {
//      return 0;
//    }

  }

  /**
   * Class for clients to access. Because we know this service always
   * runs in the same process as its clients, we don't need to deal with
   * IPC.
   */
  public
  class BinderShakeIce
    extends Binder {

    public
    IntentServiceGForceWatcher getService() {
      String TAG2 = "getService: ";
      Log.i(TAG, TAG2);

      return IntentServiceGForceWatcher.this;
    }
  }

  /**
   * A constructor is required, and must call the super IntentService(String)
   * constructor with a name for the worker thread.
   */
  public
  IntentServiceGForceWatcher() {
    super("IntentServiceGForceWatcher");
  }

  /**
   * The IntentService calls this method from the default worker thread with
   * the intent that started the service. When this method returns, IntentService
   * stops the service, as appropriate.
   */
  @Override
  protected
  void onHandleIntent(Intent intent) {
    String TAG2 = "onHandleIntent: ";
    Log.i(TAG, TAG2);

  }

  @Override
  public
  void onCreate() {
    String TAG2 = "onCreate: ";
    Log.i(TAG, TAG2);

    super.onCreate();

    c = getApplicationContext();
    common = new Common(c);

    address = getString(R.string.error_location_unavailable);
    latLong = getString(R.string.error_lat_long_unavailable);

    buildGoogleApiClient();
    if (googleApiClient != null) {
      googleApiClient.connect();
    }

    locationRequest = LocationRequest.create();
  }

  @Override
  public
  int onStartCommand(
    Intent intent,
    int flags,
    int startId
  ) { // Overriding this instead of onHandleIntent because we want to be able to return START_STICKY.

    String TAG2 = "onStartCommand: ";
    Log.i(TAG, TAG2);

    sensorManager = (SensorManager) getSystemService(
      Context.SENSOR_SERVICE
    ); // Initialize the accelerometer values.
    sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensorManager.registerListener(
      this,
      sensorAccelerometer,
      SensorManager.SENSOR_DELAY_NORMAL
    );
    //    sensorManager.registerListener(this, sensorAccelerometer, Constants.DELAY_UPDATES_GFORCE_MICROSECONDS);

    showNotification(
      getString(R.string.s_notification_persistent_title),
      getString(R.string.s_notification_persistent_text),
      R.drawable.ic_notification,
      false,
      true,
      Notification.PRIORITY_MIN,
      ID_NOTIFICATION_PERSISTENT
    );

    return START_STICKY; // We want this service to continue running until it is explicitly stopped, so return sticky.
  }

  protected synchronized
  void buildGoogleApiClient() {
    String TAG2 = "buildGoogleApiClient: ";
    Log.i(TAG, TAG2);

    googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(
      new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public
        void onConnected(Bundle bundle) {
          String TAG2 = "onConnected: ";
          Log.i(TAG, TAG2);

          // Gets the best and most recent location currently available,
          // which may be null in rare cases when a location is not available.
          locationLast = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
          if (locationLast != null) {
            latLong = getLatLong();

            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
              Toast.makeText(c, "Geocoder is not available", Toast.LENGTH_LONG).show();
              return;
            }
          }
          else { // Request Google Play Services for an update to the user's location.
            LocationServices.FusedLocationApi.requestLocationUpdates(
              googleApiClient,
              locationRequest,
              new LocationListener() {
                @Override
                public
                void onLocationChanged(Location location) {
                  String TAG2 = "onLocationChanged: ";
                  Log.i(TAG, TAG2);

                  locationLast = location;
                  latLong = getLatLong();
                }
              }
            );
          }
          common.saveSpString(Constants.SP_KEY_LOCATION, getString(R.string.s_location_default)); // Reset the saved location to it's default value.
        }

        @Override
        public
        void onConnectionSuspended(int i) {
          String TAG2 = "onConnectionSuspended: ";
          Log.i(TAG, TAG2);

          googleApiClient.connect();
          Log.i(TAG, TAG2 + " i = " + i);
        }
      }

    ).addOnConnectionFailedListener(
      new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public
        void onConnectionFailed(ConnectionResult connectionResult) {
          String TAG2 = "onConnectionFailed: ";
          Log.i(TAG, TAG2);

          if (connectionResult.hasResolution()) {
            try {
              // Start an Activity that tries to resolve the error
              connectionResult.startResolutionForResult(
                (Activity) c,
                CONNECTION_FAILURE_RESOLUTION_REQUEST
              );
            }
            catch (IntentSender.SendIntentException e) {
              e.printStackTrace();
            }
          }
          else {
            Log.i(TAG, TAG2 + "Location services connection failed with code " + connectionResult.getErrorCode());
          }
          Log.i(TAG, TAG2 + "onConnectionFailed: connectionResult.toString() = " + connectionResult.toString());
        }
      }
    ).addApi(LocationServices.API).build();
  }

  private
  String getLatLong() {
    String TAG2 = "getLatLong: ";
    Log.i(TAG, TAG2);

    String latLong;
    if (locationLast != null) {
      latitude = String.valueOf(locationLast.getLatitude());
      longitude = String.valueOf(locationLast.getLongitude());
      latLong = "latitude: " + latitude + ",\n longitude: " + longitude;
    }
    else {
      latLong = getString(R.string.error_lat_long_unavailable);
    }

    Log.i(TAG, "getLatLong: latLong = " + latLong);

    return latLong;
  }

  protected
  void startIntentServiceAddressFetcher() {
    String TAG2 = "startIntentServiceAddressFetcher: ";
    Log.i(TAG, TAG2);

    Intent intent = new Intent(
      this,
      IntentServiceAddressFetcher.class
    );
    intent.putExtra(
      Constants.RECEIVER,
      getReceiverResultAddress()
    );
    intent.putExtra(
      Constants.LOCATION_DATA_EXTRA,
      locationLast
    );
    startService(intent);
  }

  private
  ReceiverResultAddress getReceiverResultAddress() { // Obtain an instance of the broadcast receiver.
    String TAG2 = "getReceiverResultAddress: ";
    Log.i(TAG, TAG2);

    if (receiverResultAddress == null) {
      receiverResultAddress = new ReceiverResultAddress(
        new Handler() {
          public
          void handleMessage(Message msg) {
            String TAG2 = "new ReceiverResultAddress().new Handler().handleMessage: ";
            Log.i(TAG, TAG2);

          }
        }
      );
    }

    return receiverResultAddress;
  }

  @Override
  public
  IBinder onBind(Intent intent) {
    String TAG2 = "onBind: ";
    Log.i(TAG, TAG2);

    return serviceBinder;
  }

  public
  boolean onUnBind(Intent intent) {
    String TAG2 = "onUnBind: ";
    Log.i(TAG, TAG2);

    if (googleApiClient.isConnected()) {
      LocationServices.FusedLocationApi.removeLocationUpdates(
        googleApiClient,
        new LocationListener() {
          @Override
          public
          void onLocationChanged(Location location) {
            String TAG2 = "onLocationChanged: ";
            Log.i(TAG, TAG2);

            locationLast = location;
            Log.i(TAG, TAG2 + "location = " + location.toString());
          }
        }
      );
      googleApiClient.disconnect();
    }

    return true;
  }

  @Override
  public
  void onDestroy() {
    String TAG2 = "onDestroy: ";
    Log.i(TAG, TAG2);

    super.onDestroy();

    googleApiClient = null;
    locationLast = null;
    locationRequest = null;
  }

  @Override
  public
  void onSensorChanged(SensorEvent sensorEvent) {
    String TAG2 = "onSensorChanged: ";
//    Log.i(TAG, TAG2);

    Sensor sensor = sensorEvent.sensor;
    if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      double x = sensorEvent.values[0];
      double y = sensorEvent.values[1];
      double z = sensorEvent.values[2];
      gForce = common.round((Math.sqrt(x * x + y * y + z * z) / Constants.FACTOR_REDUCTION), 2);

      thresholdGForce = common.round(common.fetchSpThresholdGForce(), 2);

      Log.i(TAG, TAG2 + "gForce = " + gForce + ", thresholdGForce = " + thresholdGForce);

      if (gForce > thresholdGForce) { // To avoid false-positives calculate the average of the values of the gforce measured over a specific period, like 4 sec.
        Log.w(TAG, "onSensorChanged: gForce has exceeded threshold: " + gForce);

        if (!recordingValuesGforce) {
          recordingValuesGforce = true;

          (new Handler()).postDelayed(
            new Runnable() {
              double averageValueGforce;

              @Override
              public
              void run() {
                String TAG2 = "new Runnable().run: ";
                Log.i(TAG, TAG2);

                recordingValuesGforce = false;
                calculateAverageValuesGforce();
              }

              private
              void calculateAverageValuesGforce() {
                String TAG2 = "calculateAverageValuesGforce: ";
                Log.i(TAG, TAG2);

                double sumValues = 0.0;
                for (Double value : valuesGforce) {
                  sumValues += value.doubleValue();
                }

                averageValueGforce = common.round(sumValues / valuesGforce.size(), 2);
                Log.e(
                  TAG,
                  TAG2 +
                    "postDelayed: " +
                    "thresholdGForce = " + thresholdGForce +
                    ", averageValueGforce = " + averageValueGforce +
                    ", sumValues = " + sumValues +
                    ", valuesGforce.size() = " + valuesGforce.size()
                );

                valuesGforce.clear();

                double thresholdGForceForAlert = thresholdGForce * Constants.CONSTANT_GFORCE;
                if (averageValueGforce > thresholdGForceForAlert) {
                  Log.e(
                    TAG,
                    TAG2 +
                      "postDelayed: " +
                      "thresholdGForceForAlert = " + thresholdGForceForAlert +
                      ", averageValueGforce = " + averageValueGforce +
                      ", sumValues = " + sumValues
                  );

                  sendAlerts(getString(R.string.s_sms_alert)); // Alert the selected contacts.
                  notifyThresholdExceeded();

                  String subText =
                    (common.fetchSpIsAlertEnabled()) ? getString(R.string.s_notification_text_sent_alert) : getString(R.string.s_notification_text_did_not_send_alert);
                  showNotification(
                    getString(R.string.s_notification_title_sent_alert),
                    subText /* + " (" + averageValueGforce + "g)" */,
                    R.drawable.ic_notification,
                    true,
                    false,
                    Notification.PRIORITY_HIGH,
                    ID_NOTIFICATION
                  );

                  final String location = getString(R.string.error_location_unavailable);
                  if (locationLast != null) // Assuming that this will always contain the current location of the user.
                  {
//                    (new android.os.Handler())
//                    .postDelayed( // Wait before fetching the latest street address.
//                                  new Runnable()
//                                  {
//                                    @Override
//                                    public void run()
//                                    {
//                                      Log.i(TAG, "postDelayed: ");
//                                      startIntentServiceAddressFetcher();
//                                    }
//                                  },
//                                  10000
//                    );

                    startIntentServiceAddressFetcher();
                  } // if (locationLast != null)
                  else {
                    sendAlerts(getString(R.string.s_sms_alert_location) + " " + location); // Send the alert containing the user's location.
                    Toast.makeText(c, getString(R.string.s_toast_location) + " " + location, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onSensorChanged(): (locationLast != null): else " + getString(R.string.s_toast_location) + " " + location);
                  } // else (locationLast != null)
                } // if (averageValueGforce > thresholdGForceForAlert)

              } // void calculateAverageValuesGforce()
            }, // new Runnable
            Constants.DURATION_RECORD_VALUES_GFORCE_MILLISECONDS
          ); // Record the values of gforce over this period and then calculate their average.

        } // if (!recordingValuesGforce)
      } // if (gForce > thresholdGForce)

      if (recordingValuesGforce) {
        valuesGforce.add(gForce); // Autoboxing to Double();
      }
    }
  }

  private
  void sendAlerts(String message) {
    String TAG2 = "sendAlerts: ";
    Log.i(TAG, TAG2);

    enableAlert = common.fetchSpIsAlertEnabled();
    Log.i(TAG, TAG2 + "enableAlert = " + enableAlert);

    if (enableAlert == true) {
      String numberPrimary = common.fetchSpNumberPrimary();
      Log.i(TAG, TAG2 + "numberPrimary = " + numberPrimary);

      if (!numberPrimary.equals(Constants.SP_VALUE_DEFAULT_NUMBER_PRIMARY)) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + numberPrimary));
        c.startActivity(intent);
      }
      else {
        Log.i(TAG, TAG2 + getString(R.string.s_toast_error_calling));
        Toast.makeText(c, getString(R.string.s_toast_error_calling), Toast.LENGTH_LONG).show();
      }

//      common.dialNumber(numberPrimary);

      ArrayList<String> numbers = (new DataAccessObject(c)).fetchNumbers(); // Fetch all selected contacts from the db.
      for (String number : numbers) {
        common.sendSms(number, message);
        Log.i(TAG, TAG2 + "SMS sent - message: " + message + ", number: " + number);
      }
    }
  }

  private
  void notifyThresholdExceeded() {
    String TAG2 = "notifyThresholdExceeded: ";
    Log.i(TAG, TAG2);

    Vibrator v = null;
    v = (Vibrator) c.getSystemService(c.VIBRATOR_SERVICE);
    if (v != null) { // Vibrate the device if it has a vibrator.
      v.vibrate(DURATION_VIBRATOR_MILLISECONDS);
    }
    else { // Beep, otherwise.
      try {
        Uri notification = RingtoneManager.getDefaultUri(
          RingtoneManager.TYPE_NOTIFICATION
        );
        Ringtone r = RingtoneManager.getRingtone(c, notification);
        r.play();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private
  void showNotification(
    String title,
    String body,
    int icon,
    boolean autoCancel,
    boolean onGoing,
    int priority,
    int id
  ) {
    String TAG2 = "showNotification: ";
    Log.i(TAG, TAG2);

    NotificationCompat.Builder mBuilder =
      new NotificationCompat.Builder(this)
        .setContentTitle(title)
        .setContentText(body)
        .setSmallIcon(icon)
        .setAutoCancel(autoCancel)
        .setOngoing(onGoing)
        .setPriority(priority);
    Intent resultIntent = new Intent(this, MapActivity.class);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this); // The stack builder object will contain an artificial back stack for the started Activity. This ensures that navigating backward from the Activity leads out of your application to the Home screen. */
    stackBuilder.addParentStack(
      MainActivity.class
    ); // Adds the back stack for the Intent (but not the Intent itself)
    stackBuilder.addNextIntent(
      resultIntent
    ); // Adds the Intent that starts the Activity to the top of the stack
    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
      0,
      PendingIntent.FLAG_UPDATE_CURRENT
    );
    mBuilder.setContentIntent(resultPendingIntent);
    NotificationManager mNotificationManager = (NotificationManager) getSystemService(
      Context.NOTIFICATION_SERVICE
    );
    mNotificationManager.notify(
      id,
      mBuilder.build()
    ); // The id allows you to update the notification later on.
  }

  private
  void startRecordingValuesGforce() {
  }

  @Override
  public
  void onAccuracyChanged(
    Sensor sensor,
    int accuracy
  ) {
  }

  public
  void setEnableAlert(boolean enableAlert) {
    this.enableAlert = enableAlert;
  }

  public
  void setThresholdGForce(double gForce) {
    thresholdGForce = gForce;
  }

  public
  void setMessageAlert(String message) {
    messageAlert = message;
  }

  public
  double getGForce() {
    String TAG2 = "getGForce: ";
    Log.i(TAG, TAG2);

    return gForce;
  }

}
