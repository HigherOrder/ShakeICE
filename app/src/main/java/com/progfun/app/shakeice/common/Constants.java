package com.progfun.app.shakeice.common;

/**
 * Created by p on 03/11/14.
 */

public final class Constants {

  public static final boolean LOG = true;

  public static final String SP = "sp_desisafe";
  public static final String SP_FILE = "sp_file";

  public static final String YES = "y";
  public static final String NO = "n";

  public static final String ZERO = "0";
  public static final String ONE = "1";

  public static final String SP_KEY_WAS_LAUNCHED_BEFORE = "SP_KEY_WAS_LAUNCHED_BEFORE";
  public static final boolean SP_VALUE_DEFAULT_WAS_LAUNCHED_BEFORE = true;

//  public static final String SP_KEY_IS_USERS_PHONE_NUMBER_ENTERED = "is_users_phone_number_entered";
//  public static final boolean SP_VALUE_DEFAULT_IS_USERS_PHONE_NUMBER_ENTERED = false;

  public static final String SP_KEY_USERS_PHONE_NUMBER = "sp_users_phone_number";
  public static final String SP_VALUE_DEFAULT_USERS_PHONE_NUMBER = "000000000000";

  public static final String SP_KEY_GFORCE_MAX = "sp_gforce_max";
  public static final String SP_VALUE_DEFAULT_GFORCE_MAX = "1.00";

  public static final String SP_KEY_GFORCE_MIN = "sp_gforce_min";
  public static final String SP_VALUE_DEFAULT_GFORCE_MIN = "1.00";

  public static final String SP_KEY_ALERT_NUMBERS_ADDED = "SP_KEY_ALERT_NUMBERS_ADDED ";
  public static final boolean SP_VALUE_DEFAULT_ALERT_NUMBERS_ADDED = false;

  public static final String PREF_KEY_SMS_ENABLE = "PREF_KEY_SMS_ENABLE";
  public static final boolean PREF_VALUE_DEFAULT_SMS_ENABLE = true;

  public static final String SP_KEY_THRESHOLD_GFORCE = "SP_KEY_THRESHOLD_GFORCE";

  public static final String SP_KEY_LOCATION = "SP_KEY_LOCATION";
  public static final String SP_VALUE_DEFAULT_LOCATION = "SP_VALUE_DEFAULT_LOCATION";

  public static final String SP_KEY_MESSAGE_FOR_ALERT = "SP_KEY_MESSAGE_FOR_ALERT";
  public static final String SP_VALUE_DEFAULT_MESSAGE_FOR_ALERT = "SP_VALUE_DEFAULT_MESSAGE_FOR_ALERT";

  public static final String SP_KEY_NUMBER_PRIMARY = "SP_KEY_NUMBER_PRIMARY";
  public static final String SP_VALUE_DEFAULT_NUMBER_PRIMARY = "+99999999999999";

  public static final double SP_VALUE_THRESHOLD_GFORCE_LOW_VERY = 1.20;
  public static final double SP_VALUE_THRESHOLD_GFORCE_LOW = 1.44;
  public static final double SP_VALUE_THRESHOLD_GFORCE_MEDIUM = 1.80;
  public static final double SP_VALUE_THRESHOLD_GFORCE_HIGH = 2.16;
  public static final double SP_VALUE_THRESHOLD_GFORCE_HIGH_VERY = 2.88;

  public static final double FACTOR_REDUCTION = 10.0;
  public static final double CONSTANT_GFORCE = 1.20;

  public static final int INTERVAL_UPDATES_GFORCE_MILLISECONDS = 252;
  public static final int INTERVAL_CALLS_TO_ADDRESS_FETCHER = 30000;
  public static final int DELAY_CALL_TO_ADDRESS_FETCHER_FIRST = 1000;

  public static int TIMEOUT_SPLASH_SCREEN = 2000;

  public static final int DURATION_RECORD_VALUES_GFORCE_MILLISECONDS = 3600;

  public static final String FRAGMENT_DIALOG_ACTIVITY_SELECTED_NAME = "FRAGMENT_DIALOG_ACTIVITY_SELECTED_NAME";
  public static final String FRAGMENT_DIALOG_ACTIVITY_SELECTED_DESCRIPTION = "FRAGMENT_DIALOG_ACTIVITY_SELECTED_DESCRIPTION";
  public static final String FRAGMENT_DIALOG_ACTIVITY_SELECTED_IMPACT = "FRAGMENT_DIALOG_ACTIVITY_SELECTED_IMPACT";

  public static final int SUCCESS_RESULT = 0;
  public static final int FAILURE_RESULT = 1;
  public static final String PACKAGE_NAME = "com.progfun.app.shakeice";
  public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
  public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
  public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

  /**
   * Determines whether to always show the simplified settings UI, where
   * settings are presented in a single list. When false, settings are shown
   * as a master/detail two-pane view on tablets. When true, a single pane is
   * shown on tablets.
   */
  public static final boolean ALWAYS_SIMPLE_PREFS = false;
}
