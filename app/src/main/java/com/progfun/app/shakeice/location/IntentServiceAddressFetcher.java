package com.progfun.app.shakeice.location;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.progfun.app.shakeice.R;
import com.progfun.app.shakeice.common.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by p on 26/08/15.
 */
public class IntentServiceAddressFetcher
        extends IntentService {

  private final String TAG = this.getClass().getSimpleName();

  private Context c;

  protected ResultReceiver mReceiver;

  public IntentServiceAddressFetcher() {
    super("IntentServiceAddressFetcher");
  }
  @Override
  protected void onHandleIntent(Intent intent) {
    c = getApplicationContext();
    Geocoder geocoder = new Geocoder(c, Locale.getDefault());

    String errorMessage = "";


    mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
    Location location = intent.getParcelableExtra(
            Constants.LOCATION_DATA_EXTRA);

    if (geocoder == null || location == null) { // Handle case where no address was found.
      if (errorMessage.isEmpty()) {
        Log.e(TAG, errorMessage + getString(R.string.error_parameters_null));
      }
      deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
      return;
    }

    List<Address> addresses = null;

    try {
      addresses = geocoder.getFromLocation(
              location.getLatitude(), location.getLongitude(),
              // In this sample, get just a single address.
              1);
//        Log.i( TAG, "onHandleIntent: addresses = " + addresses.toString() );
    }
    catch (IOException ioException) { // Catch network or other I/O problems.
      Log.e(TAG, errorMessage, ioException);
      ioException.printStackTrace();
    }
    catch (IllegalArgumentException illegalArgumentException) { // Catch invalid latitude or longitude values.
      Log.e(
              TAG, errorMessage + ". " +
                      "Latitude = " + location.getLatitude() +
                      ", Longitude = " +
                      location.getLongitude(), illegalArgumentException);
      illegalArgumentException.printStackTrace();
    }

    if (addresses == null || addresses.size() == 0) { // Handle case where no address was found.
      if (errorMessage.isEmpty()) {
        Log.e(TAG, errorMessage + "Address was not found");
      }
      deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
    }
    else { // Fetch the address lines using getAddressLine, join them, and send them to the thread.
      Address address = addresses.get(0);
      ArrayList<String> addressFragments = new ArrayList<String>();
      for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
        addressFragments.add(address.getAddressLine(i));
      }
//        Log.i( TAG, "onHandleIntent: addressFragments = " + addressFragments.toString() );
//      Log.i(TAG, "Address was found.");
      deliverResultToReceiver(
              Constants.SUCCESS_RESULT, TextUtils.join(
                      System.getProperty("line.separator"), addressFragments));
    }
  }

  private void deliverResultToReceiver(int resultCode, String message) {
    Bundle bundle = new Bundle();
    bundle.putString(Constants.RESULT_DATA_KEY, message);

    if (mReceiver != null) {
      mReceiver.send(resultCode, bundle);
    }
    else {
      Log.e(TAG, "Receiver passed via extra is null.");
//      Toast.makeText(c, "Receiver passed via extra is null", Toast.LENGTH_LONG).show();
    }
  }

}
