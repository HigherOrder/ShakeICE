package com.progfun.app.shakeice.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.progfun.app.shakeice.R;

/**
 * Created by p on 18/11/14.
 */
public class FragmentDialogSelectActivity
        extends DialogFragment {


  /* The activity that creates an instance of this dialog fragment must
   * implement this interface in order to receive event callbacks.
   * Each method passes the DialogFragment in case the host needs to query it. */
  public interface SelectContactsDialogListener {
    public void onDialogPositiveClick(DialogFragment dialog);
    public void onDialogNegativeClick(DialogFragment dialog);
  }

  // Use this instance of the interface to deliver action events
  SelectContactsDialogListener selectContactsDialogListener;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    // Use the Builder class for convenient dialog construction
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    builder.setMessage(R.string.s_dialog_select_activity)
            .setPositiveButton(R.string.s_ok, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                selectContactsDialogListener.onDialogPositiveClick(FragmentDialogSelectActivity.this);
              }
            })
            .setNegativeButton(R.string.s_cancel, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                selectContactsDialogListener.onDialogNegativeClick(FragmentDialogSelectActivity.this);
              }
            });

    // Create the AlertDialog object and return it
    return builder.create();
  }


  // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
  @Override
  public void onAttach(Activity activity) {

    super.onAttach(activity);

    // Verify that the host activity implements the callback interface
    try {
      // Instantiate the NoticeDialogListener so we can send events to the host
      selectContactsDialogListener = (SelectContactsDialogListener) activity;
    } catch (ClassCastException e) {
      // If the activity doesn't implement the interface, throw exception
      throw new ClassCastException(activity.toString() +
              " The activity doesn't implement the interface SelectContactsDialogListener");
    }

  }


}
