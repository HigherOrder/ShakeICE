package com.progfun.app.shakeice.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;

import com.progfun.app.shakeice.R;
import com.progfun.app.shakeice.common.Common;
import com.progfun.app.shakeice.common.Constants;

/**
 * Created by p on 18/11/14.
 */
public class FragmentDialogAdjustSensitivity
        extends DialogFragment
        implements View.OnClickListener {

  /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
  public interface IListenerFragmentDialogAdjustSensitivity {
    public void onClickCloseAdjustSensitivity(DialogFragment dialog);
  }

  // Use this instance of the interface to deliver action events
  IListenerFragmentDialogAdjustSensitivity listener;

  private final String TAG = this.getClass().getSimpleName();

  private Context c;

  private RadioButton rbLowVery;
  private RadioButton rbLow;
  private RadioButton rbMedium;
  private RadioButton rbHigh;
  private RadioButton rbHighVery;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    String TAG2 = "onCreateDialog: ";
    Log.i(TAG, TAG2);

    final Dialog dialog = new Dialog(getActivity());
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    //    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    dialog.setContentView(R.layout.dialog_adjust_sensitivity);
    dialog.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT));
    dialog.show();

    c = getActivity();

    rbLowVery = (RadioButton) dialog.findViewById(R.id.rbLowVery);
    rbLow = (RadioButton) dialog.findViewById(R.id.rbLow);
    rbMedium = (RadioButton) dialog.findViewById(R.id.rbMedium);
    rbHigh = (RadioButton) dialog.findViewById(R.id.rbHigh);
    rbHighVery = (RadioButton) dialog.findViewById(R.id.rbHighVery);

    rbLowVery.setOnClickListener(this);
    rbLow.setOnClickListener(this);
    rbMedium.setOnClickListener(this);
    rbHigh.setOnClickListener(this);
    rbHighVery.setOnClickListener(this);

    rbLowVery.setChecked(false);
    rbLow.setChecked(false);
    rbMedium.setChecked(false);
    rbHigh.setChecked(false);
    rbHighVery.setChecked(false); // Uncheck all radio buttons except the middle one which is checked by default.

    Common common = new Common(c);
    double thresholdGForce = common.round(common.fetchSpThresholdGForce(), 2);
    Log.i( TAG, "onCreateDialog: thresholdGForce = " + thresholdGForce);

    if (thresholdGForce == Constants.SP_VALUE_THRESHOLD_GFORCE_LOW_VERY) {
      rbHighVery.setChecked(true);
    }
    if (thresholdGForce == Constants.SP_VALUE_THRESHOLD_GFORCE_LOW) {
      rbHigh.setChecked(true);
    }
    else if (thresholdGForce == Constants.SP_VALUE_THRESHOLD_GFORCE_MEDIUM) {
      rbMedium.setChecked(true);
    }
    else if (thresholdGForce == Constants.SP_VALUE_THRESHOLD_GFORCE_HIGH) {
      rbLow.setChecked(true);
    }
    else if (thresholdGForce == Constants.SP_VALUE_THRESHOLD_GFORCE_HIGH_VERY) {
      rbLowVery.setChecked(true);
    }

    dialog.findViewById(R.id.tvClose).setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                dismiss();
              }
            });

    return dialog;
  }

  @Override
  public void onClick(View view) {
    String TAG2 = "onClick: ";
    Log.i(TAG, TAG2);
    
    boolean checked = ((RadioButton) view).isChecked(); // Is the button now checked?

    Common common = new Common(c);

    switch (view.getId()) { // Check which radio button was clicked
      case R.id.rbLowVery:
        if (checked) {
          rbLow.setChecked(false);
          rbMedium.setChecked(false);
          rbHigh.setChecked(false);
          rbHighVery.setChecked(false);
          common.saveSpDouble(Constants.SP_KEY_THRESHOLD_GFORCE, Constants.SP_VALUE_THRESHOLD_GFORCE_HIGH_VERY);
        }
        break;
      case R.id.rbLow:
        if (checked) {
          rbLowVery.setChecked(false);
          rbMedium.setChecked(false);
          rbHigh.setChecked(false);
          rbHighVery.setChecked(false);
          common.saveSpDouble(Constants.SP_KEY_THRESHOLD_GFORCE, Constants.SP_VALUE_THRESHOLD_GFORCE_HIGH);
        }
        break;
      case R.id.rbMedium:
        if (checked) {
          rbLowVery.setChecked(false);
          rbLow.setChecked(false);
          rbHigh.setChecked(false);
          rbHighVery.setChecked(false);
          common.saveSpDouble(Constants.SP_KEY_THRESHOLD_GFORCE, Constants.SP_VALUE_THRESHOLD_GFORCE_MEDIUM);
        }
        break;
      case R.id.rbHigh:
        if (checked) {
          rbLowVery.setChecked(false);
          rbLow.setChecked(false);
          rbMedium.setChecked(false);
          rbHighVery.setChecked(false);
          common.saveSpDouble(Constants.SP_KEY_THRESHOLD_GFORCE, Constants.SP_VALUE_THRESHOLD_GFORCE_LOW);
        }
        break;
      case R.id.rbHighVery:
        if (checked) {
          rbLowVery.setChecked(false);
          rbLow.setChecked(false);
          rbMedium.setChecked(false);
          rbHigh.setChecked(false);
          common.saveSpDouble(Constants.SP_KEY_THRESHOLD_GFORCE, Constants.SP_VALUE_THRESHOLD_GFORCE_LOW_VERY);
        }
        break;
    }
  }

  // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
  @Override
  public void onAttach(Activity activity) {
    String TAG2 = "onAttach: ";
    Log.i(TAG, TAG2);

    super.onAttach(activity);

    // Verify that the host activity implements the callback interface
    try {
      // Instantiate the NoticeDialogListener so we can send events to the host
      listener = (IListenerFragmentDialogAdjustSensitivity) activity;
    } catch (ClassCastException e) {
      // The activity doesn't implement the interface, throw exception
      throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
    }

  }


}
