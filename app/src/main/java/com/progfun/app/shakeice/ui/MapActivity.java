package com.progfun.app.shakeice.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.progfun.app.shakeice.R;
import com.progfun.app.shakeice.common.Common;
import com.progfun.app.shakeice.common.Constants;
import com.progfun.app.shakeice.controllers.IntentServiceGForceWatcher;
import com.progfun.app.shakeice.location.IntentServiceAddressFetcher;
import com.progfun.app.shakeice.ui.dialogs.FragmentDialogAdjustSensitivity;
import com.progfun.app.shakeice.ui.dialogs.FragmentDialogCloseApp;
import com.progfun.app.shakeice.ui.dialogs.FragmentDialogDisableSms;
import com.progfun.app.shakeice.ui.dialogs.FragmentDialogViewHelp;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public
class MapActivity
  extends AppCompatActivity
  implements FragmentDialogDisableSms.IListenerFragmentDialogDisableSms,
             FragmentDialogAdjustSensitivity.IListenerFragmentDialogAdjustSensitivity,
             FragmentDialogViewHelp.IListenerFragmentDialogViewHelp,
             FragmentDialogCloseApp.IListenerFragmentDialogCloseApp,
  OnMapReadyCallback {

  private final String TAG = this.getClass().getSimpleName();

  private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

  private Context c;

  private GoogleMap map;

  private Common common;

  private MenuItem miEnableSms;
  private MenuItem miDisableSms;

  private TextView tvAddress;
  private TextView tvLatitude;
  private TextView tvLongitude;
  private TextView tvNoLocation;
  private TextView tvNoAddress;

  private GoogleApiClient googleApiClient;

  private LocationRequest locationRequest;

  private Location locationLast;

  private ReceiverResultAddress receiverResultAddress;

  private double dLatitude;
  private double dLongitude;

  private String latitude;
  private String longitude;

  private String address;
  private String latLong;
  private String location;

  ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
  ScheduledThreadPoolExecutor scheduledThreadPoolExecutor2 = new ScheduledThreadPoolExecutor(1);

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
//      Log.i(TAG, TAG2);

      address = resultData.getString(Constants.RESULT_DATA_KEY);
      latLong = getLatLong();
      location = getString(R.string.error_location_unavailable);

      /* Add a marker at the current geolocation and move the camera. */
      LatLng latLng = new LatLng(dLatitude, dLongitude);
      map.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.s_you_are_here)));
//      map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

      map.animateCamera(
        CameraUpdateFactory.newLatLngZoom(
          new LatLng(dLatitude, dLongitude),
          13
        )
      );

      CameraPosition cameraPosition = new CameraPosition.Builder()
        .target(new LatLng(dLatitude, dLongitude)) // Centers the map to this location
        .zoom(17) // Sets the zoom
        .bearing(0) // Sets the orientation of the camera. 0 is north
        .tilt(30) // Sets the tilt of the camera in degrees
        .build(); // Creates a CameraPosition from the builder
      map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

      if (resultCode == Constants.SUCCESS_RESULT) { // The street address is available and valid.
        location = address;
      }
      else { // The lat-long is available only.
        location = getString(R.string.error_address_unavailable);
      }

//      Log.i(TAG, "onReceiveResult: resultCode = " + resultCode + ", address = " + address + ", latLong = " + latLong + ", location = " + location);

      tvAddress.setText(location);

      tvLatitude.setText(latitude);
      tvLongitude.setText(longitude);
      
      if (location.equals(address)) {
        tvNoLocation.setVisibility(View.INVISIBLE);
        tvNoAddress.setVisibility(View.INVISIBLE);
      }
      else if (latLong.equals(getString(R.string.error_lat_long_unavailable))) {
        tvNoLocation.setVisibility(View.VISIBLE);
        tvNoAddress.setVisibility(View.INVISIBLE);
      }
      else {
        tvNoLocation.setVisibility(View.INVISIBLE);
        tvNoAddress.setVisibility(View.VISIBLE);
      }
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

  private
  String getLatLong() {
    String TAG2 = "getLatLong: ";
//    Log.i(TAG, TAG2);

    String latLong;
    if (locationLast != null) {
      latitude = String.valueOf(dLatitude);
      longitude = String.valueOf(dLongitude);
      latLong = "latitude: " + latitude + ",\n longitude: " + longitude;
    }
    else {
      latLong = getString(R.string.error_lat_long_unavailable);
    }

//      Log.i(TAG, "getLatLong: latLong = " + latLong);
    return latLong;
  }

  @Override
  protected
  void onCreate(Bundle savedInstanceState) {
    String TAG2 = "onCreate: ";
    Log.i(TAG, TAG2);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
    
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment =
      (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    c = getApplicationContext(); // Get the app's context.

    Intent serviceIntent = new Intent(c, IntentServiceGForceWatcher.class); // Start the background service to monitor the G-forces.
    c.startService(serviceIntent);
    
    common = new Common(c);
    if (!common.has(Constants.SP_KEY_ALERT_NUMBERS_ADDED)) { // If the user has launched the app for the 1st time show him the Welcome screen.
      Intent i = new Intent(c, ContactsSelectionActivity.class);
      startActivity(i);
      finish();
    }

    buildGoogleApiClient();
    if (googleApiClient != null) {
      googleApiClient.connect();
    }
    locationRequest = LocationRequest.create();
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    TextView tvTitle = (TextView) toolbar.findViewById(R.id.tvBody);
    tvTitle.setTextColor(getResources().getColor(R.color.text_title));
    //    tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/timeburner_regular.ttf"));

    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowHomeEnabled(true);
    actionBar.setDisplayShowTitleEnabled(false);
    
    tvAddress = (TextView) findViewById(R.id.tvAddress);
    if (common.has(Constants.SP_KEY_LOCATION)) {
      tvAddress.setText(common.fetchSpLocation());
    }

    tvLatitude = (TextView) findViewById(R.id.tvLatitude);
    tvLongitude = (TextView) findViewById(R.id.tvLongitude);
    tvNoLocation = (TextView) findViewById(R.id.tvNoLocation);
    tvNoAddress = (TextView) findViewById(R.id.tvNoAddress);

  }

  protected synchronized
  void buildGoogleApiClient() {
    String TAG2 = "buildGoogleApiClient: ";
//    Log.i(TAG, TAG2);

    googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(
      new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public
        void onConnected(Bundle bundle) { // Get the best and most recent location currently available, which may be null in rare cases when a location is not available.
          String TAG2 = "onConnected: ";
//          Log.i(TAG, TAG2);

          locationLast = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
          if (locationLast != null) {
            dLatitude = locationLast.getLatitude();
            dLongitude = locationLast.getLongitude();

            latLong = getLatLong();
//            Log.i(TAG, "onConnected: latLong = " + latLong);

            if (!Geocoder.isPresent()) { // Return if a Geocoder is unavailable.
//              Log.i(TAG, "onConnected: Geocoder is unavailable");
              Toast.makeText(c, getString(R.string.failure_location_on_connection_failed), Toast.LENGTH_LONG).show();
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
//                  Log.i(TAG, TAG2);

                  locationLast = location;
                  latLong = getLatLong();
//                  Log.i(TAG, "onLocationChanged: latLong = " + latLong);
                }
              }
            );
          }
        }

        @Override
        public
        void onConnectionSuspended(int i) {
          String TAG2 = "onConnectionSuspended: ";
          Log.i(TAG, TAG2);

          googleApiClient.connect();

          Log.i(TAG, "onConnectionSuspended: i = " + i);
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
              Log.i(TAG, "GoogleApiClient.OnConnectionFailedListener.onConnectionFailed: Resolution is available: " + connectionResult.getErrorCode());
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
          else { // The error does not have any resolution.
            Log.i(TAG, "GoogleApiClient.OnConnectionFailedListener.onConnectionFailed: Resolution is unavailable: " + connectionResult.getErrorCode());
          }
          Log.i(TAG, "GoogleApiClient.OnConnectionFailedListener.onConnectionFailed: connectionResult.toString() = " + connectionResult.toString());

          Toast.makeText(c, getString(R.string.failure_location_on_connection_failed), Toast.LENGTH_LONG).show();
        }
      }
    )
                                                       .addApi(LocationServices.API)
                                                       .build();
  }

  @Override
  protected
  void onStart() {
    super.onStart();

    address = getString(R.string.error_address_unavailable);
    latLong = getString(R.string.error_lat_long_unavailable);
    location = getString(R.string.error_location_unavailable);
  }

  private
  ReceiverResultAddress getReceiverResultAddress() { // Obtain an instance of the broadcast receiver.
    if (receiverResultAddress == null) {
      receiverResultAddress = new ReceiverResultAddress(
        new Handler() {
          public
          void handleMessage(Message msg) {
//            Log.i(TAG, "getReceiverResultAddress: new Handler().handleMessage");
          }
        }
      );
    }
    return receiverResultAddress;
  }

  @Override
  protected
  void onResume() {
    super.onResume();

    startUpdatingLocation();
  }

  public
  void startUpdatingLocation() {
    scheduledThreadPoolExecutor2.scheduleAtFixedRate(
      new Runnable() {
        @Override
        public
        void run() {
//            Log.i(TAG, "startUpdatingLocation: location before update: ");

          mHandler3.obtainMessage(1)
                   .sendToTarget();
        }
      },
      Constants.DELAY_CALL_TO_ADDRESS_FETCHER_FIRST,
      Constants.INTERVAL_CALLS_TO_ADDRESS_FETCHER,
      TimeUnit.MILLISECONDS
    );
  }

  public Handler mHandler3 = new Handler() {
    public
    void handleMessage(Message msg) {
//        Log.i(TAG, "startUpdatingLocation: new Handler: handleMessage: location before update = " + location);
      startIntentServiceAddressFetcher();
    }
  };

  protected
  void startIntentServiceAddressFetcher() {
//      Log.i(TAG, "startIntentServiceAddressFetcher: ");

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
  void stopUpdatingLocation() {
    Log.i(TAG, "stopUpdatingLocation: ....");
    scheduledThreadPoolExecutor2.purge();
    scheduledThreadPoolExecutor2.shutdown();
  }

  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public
  void onMapReady(GoogleMap googleMap) {
    map = googleMap;

    startUpdatingLocation();
  }

  @Override
  public
  void onDestroy() {
    String TAG2 = "onDestroy: ";
    Log.i(TAG, TAG2);

    super.onDestroy();

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

    googleApiClient = null;
    locationLast = null;
    locationRequest = null;

    stopUpdatingLocation();

    scheduledThreadPoolExecutor = null;
    scheduledThreadPoolExecutor2 = null;

  }

  @Override
  public
  boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(
      R.menu.menu_map,
      menu
    ); // Inflate the menu; this adds items to the action bar if it is present.

    miDisableSms = menu.findItem(R.id.action_disable_alerting);
    miEnableSms = menu.findItem(R.id.action_enable_alerting);

    if (!common.fetchSpIsAlertEnabled()) {
      miDisableSms.setEnabled(false);
      miDisableSms.setVisible(false);
      miEnableSms.setEnabled(true);
      miEnableSms.setVisible(true);
    }

    return true;
  }

  @Override
  public
  boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    Intent i;
    switch (item.getItemId()) {
      case R.id.action_disable_alerting:
        showDialogDisableSendingSmsAlerts();
        break;
      case R.id.action_enable_alerting:
        common.saveSpBoolean(
          Constants.PREF_KEY_SMS_ENABLE,
          true
        );
        miEnableSms.setEnabled(false);
        miEnableSms.setVisible(false);
        miDisableSms.setEnabled(true);
        miDisableSms.setVisible(true);
        Toast.makeText( c, R.string.s_toast_sending_sms_enabled, Toast.LENGTH_LONG ) .show();
        break;
      case R.id.action_help:
        showDialogViewHelp();
        break;
      case R.id.action_contacts:
        i = new Intent( c, ContactsManagementActivity.class );
        startActivity(i);
        break;
      case R.id.action_share:
        i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(
          Intent.EXTRA_TEXT,
          getString(R.string.s_intent_share_body)
        ); // Set the body of the sharing message.
        startActivity(
          Intent.createChooser(
            i,
            getString(R.string.s_intent_share_title)
          )
        ); // Set the title of the sharing screen.
        break;
      case R.id.action_close_app:
        showDialogCloseApp();
        break;
      case R.id.action_sensitivity:
        showDialogAdjustSensitivity();
        break;
      //      case R.id.action_edit_user_phone_number:
      //        i = new Intent(c, NumberEntryActivity.class);
      //        startActivity(i);
      //        break;
      case R.id.action_gforce:
        i = new Intent( c, MainActivity.class );
        startActivity(i);
        break;
      case R.id.action_about:
        i = new Intent( c, ViewAboutActivity.class );
        startActivity(i);
        break;
      case R.id.action_privacy:
        i = new Intent( c, ViewPolicyActivity.class );
        startActivity(i);
        break;
      case R.id.action_terms:
        i = new Intent( c, ViewTermsActivity.class );
        startActivity(i);
        break;
    }

    return super.onOptionsItemSelected(item);
  }


  private
  void showDialogAdjustSensitivity() {
    DialogFragment fragment = new FragmentDialogAdjustSensitivity();
    fragment.show(
      getSupportFragmentManager(),
      ""
    );
  }

  private
  void showDialogDisableSendingSmsAlerts() {
    DialogFragment fragment = new FragmentDialogDisableSms();
    fragment.show(
      getSupportFragmentManager(),
      ""
    );
  }

  @Override
  public
  void onClickYesDisableSms(DialogFragment dialog) {
    Log.d(TAG, "onClickPositiveDialogDisableSms: ");
    common.saveSpBoolean(
      Constants.PREF_KEY_SMS_ENABLE,
      false
    );
    miDisableSms.setEnabled(false);
    miDisableSms.setVisible(false);
    miEnableSms.setEnabled(true);
    miEnableSms.setVisible(true);
    Toast.makeText(c, R.string.s_toast_sending_sms_disabled, Toast.LENGTH_LONG).show();
  }

  @Override
  public
  void onClickNoDisableSms(DialogFragment dialog) {
    Log.d(TAG, "onClickNegativeDialogDisableSms: ");
  }

  private
  void showDialogViewHelp() {
    FragmentDialogViewHelp f = new FragmentDialogViewHelp();
    f.show(getSupportFragmentManager(), "");
  }

  private
  void showDialogCloseApp() {
    DialogFragment fragment = new FragmentDialogCloseApp();
    fragment.show(
      getSupportFragmentManager(),
      ""
    );
  }

  @Override
  public
  void onClickYesCloseApp(DialogFragment dialog) {
    Log.d(TAG, "onDialogYesCloseApp: ");
    try {
      Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS); //Open the specific App Info page:
      intent.setData(Uri.parse("package:" + c.getPackageName()));
      startActivity(intent);
    }
    catch (ActivityNotFoundException e) {
      //e.printStackTrace();
      Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS); //Open the main Apps page:
      startActivity(intent);
    }
  }

  @Override
  public
  void onClickNoCloseApp(DialogFragment dialog) {
    Log.d(TAG, "onClickNoCloseApp: ");
  }

  @Override
  public
  void onClickCloseViewHelp(DialogFragment dialog) {
      Log.d(TAG, "onClickCloseViewHelp: ");
  }

  @Override
  public
  void onClickCloseAdjustSensitivity(DialogFragment dialog) {
    finish();
  } 
  
}
