package utils;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Created by p on 22/07/15.
 */
public class Utilities {

  private final String TAG = this.getClass().getSimpleName();

  private Context c;

  public Utilities(Context context) {
    c = context;
  }

  public Boolean isTablet() {
    int sizeOfScreen = (c.getResources().getConfiguration().screenLayout &
            Configuration.SCREENLAYOUT_SIZE_MASK);
    return (sizeOfScreen >= Configuration.SCREENLAYOUT_SIZE_LARGE) ? true : false;
  }

  public void showMetricsDisplay() {
    DisplayMetrics metrics = c.getResources().getDisplayMetrics();
    Log.i(TAG, "showMetricsDisplay: Screen resolution = " + metrics.widthPixels + " x " + metrics.heightPixels);
    Log.i(TAG, "showMetricsDisplay: Density (dpi) = " + metrics.densityDpi);
    Log.i(TAG, "showMetricsDisplay: Density (pixels) = " + metrics.xdpi + " x " + metrics.ydpi);
  }

  public boolean isConnectedToNetwork() {
    boolean isConnected = false;
    ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo ni = cm.getActiveNetworkInfo();
    if (ni != null) {
      isConnected = ni.isConnected();
    }
    return isConnected;
  }

  public boolean isValidPhoneNumber(String numberEntered) {
    boolean numberIsValid = false;

    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    TelephonyManager tm = (TelephonyManager) c.getSystemService(c.TELEPHONY_SERVICE);
    String countryCode = tm.getNetworkCountryIso().toUpperCase();
    Log.e(TAG, "countryCode = " + countryCode);

    try {
      Phonenumber.PhoneNumber numberParsed = phoneUtil.parse(numberEntered, countryCode);
      numberIsValid = phoneUtil.isValidNumber(numberParsed);
    }
    catch (NumberParseException e) {
      Log.e(TAG, "Could not parse number - " + e.toString());
    }

    Log.e(TAG, "numberIsValid = " + numberIsValid);
    return numberIsValid;
  }

  public boolean isValidEmailAddress(CharSequence emailAddress) {
    return !TextUtils.isEmpty(emailAddress) &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
  }

}
