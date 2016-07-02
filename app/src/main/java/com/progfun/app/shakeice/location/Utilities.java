package com.progfun.app.shakeice.location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.progfun.app.shakeice.common.Common;
import com.progfun.app.shakeice.common.Constants;

/**
 * Created by p on 07/09/15.
 */
public class Utilities
{
  private final String TAG = this.getClass().getSimpleName();

  private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
  private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

  private Context c;

  private Utilities utilsLocation;

  private GoogleApiClient googleApiClient;

  private Location locationLast;
  private LocationRequest locationRequest;

  private String latLong;
  private String address;

  private ReceiverResultAddress receiverResultAddress;

  protected class ReceiverResultAddress
    extends ResultReceiver
  {

    public ReceiverResultAddress(android.os.Handler handler)
    {
      super(handler);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData)
    {
      Common common = new Common(c);
      utilsLocation = new Utilities(c);

      address = resultData.getString(Constants.RESULT_DATA_KEY);
      latLong = utilsLocation.getLocation();
        Log.i( TAG, "onReceiveResult: address = " + address + ", resultCode = " + resultCode );
    }
  }

  public Utilities(Context context) {
    c = context;

    locationRequest = LocationRequest.create();
  }


  public synchronized void buildGoogleApiClient()
  {
    googleApiClient = new GoogleApiClient.Builder(c).addConnectionCallbacks(
      new GoogleApiClient.ConnectionCallbacks()
      {
        @Override
        public void onConnected(Bundle bundle)
        {

          // Gets the best and most recent location currently available,
          // which may be null in rare cases when a location is not available.
          locationLast = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
          if (locationLast != null)
          {
            latLong = utilsLocation.getLocation();

            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent())
            {
                Log.i(TAG, "onConnected: Geocoder is not available");
              return;
            }
          }
          else
          { // Request Google Play Services for an update to the user's location.
            LocationServices.FusedLocationApi.requestLocationUpdates(
              googleApiClient,
              locationRequest,
              new LocationListener()
              {
                @Override
                public void onLocationChanged(Location location)
                {
                    Log.i(TAG, "onLocationChanged: ");
                  locationLast = location;
                  latLong = utilsLocation.getLocation();
                }
              }
            );
          }
        }

        @Override
        public void onConnectionSuspended(int i)
        {
          googleApiClient.connect();

            Log.i( TAG, "onConnectionSuspended: i = " + i );
        }
      }
    )
    .addOnConnectionFailedListener(
      new GoogleApiClient.OnConnectionFailedListener()
      {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult)
        {
          if (connectionResult.hasResolution())
          {
            try
            {
              // Start an Activity that tries to resolve the error
              connectionResult.startResolutionForResult(
                (Activity) c,
                CONNECTION_FAILURE_RESOLUTION_REQUEST
              );
            }
            catch (IntentSender.SendIntentException e)
            {
              e.printStackTrace();
            }
          }
          else
          {
            Log.i(
              TAG,
              "Location services connection failed with code " + connectionResult.getErrorCode()
            );
          }
            Log.i( TAG, "onConnectionFailed: connectionResult.toString() = " + connectionResult.toString() );
        }
      }
    )
    .addApi(LocationServices.API)
    .build();
  }

  public Location getLocationLast() {
    return locationLast;
  }

  public String getLocation()
  {
    String location;

    String latitude = "";
    String longitude = "";

    if (locationLast != null)
    {
      latitude = String.valueOf(locationLast.getLatitude());
      longitude = String.valueOf(locationLast.getLongitude());
      location = "latitude = " + latitude + ", longitude = " + longitude;
    }
    else
    {
      location = "latitude: UNAVAILABLE, longitude: UNAVAILABLE";
    }

      Log.i( TAG, "getLocation: latitude = " + latitude + ", longitude = " + longitude );
    return location;
  }

  public void startIntentServiceAddress()
  {
    Intent intent = new Intent(
      c,
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
    c.startService(intent);
  }

  public ReceiverResultAddress getReceiverResultAddress()
  { // Obtain an instance of the broadcast receiver.
    if (receiverResultAddress == null)
    {
      receiverResultAddress = new ReceiverResultAddress(
        new android.os.Handler()
        {
          public void handleMessage(Message msg)
          {
            Log.i(
              TAG,
              "getReceiverResultAddress: new Handler().handleMessage"
            );
          }
        }
      );
    }
    return receiverResultAddress;
  }

//  private boolean areAvailablePlayServices()
//  {
//    boolean areAvailablePlayServices = true;
//
//    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//
//    if (resultCode != ConnectionResult.SUCCESS)
//    {
//      if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
//      {
//        GooglePlayServicesUtil.getErrorDialog(
//          resultCode,
//          (Activity) c,
//          PLAY_SERVICES_RESOLUTION_REQUEST
//        )
//          .show();
//      }
//      else
//      {
//        Toast.makeText(
//          c,
//          "This device is not supported.",
//          Toast.LENGTH_LONG
//        )
//          .show();
//      }
//      areAvailablePlayServices = false;
//    }
//    return areAvailablePlayServices;
//  }
//
//  public void initiateFetchingOfPhysicalAddress()
//  {
//    // Only start the service to fetch the address if GoogleApiClient is
//    // connected.
//    if (googleApiClient.isConnected() && locationLast != null)
//    {
//      startIntentServiceAddressFetcher();
//    }
//    // If GoogleApiClient isn't connected, process the user's request by
//    // setting wasRequestedAddress to true. Later, when GoogleApiClient connects,
//    // launch the service to fetch the address. As far as the user is
//    // concerned, pressing the Fetch Address button
//    // immediately kicks off the process of getting the address.
//    wasRequestedAddress = true;
//




}
