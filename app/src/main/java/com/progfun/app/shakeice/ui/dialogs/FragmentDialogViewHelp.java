package com.progfun.app.shakeice.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.progfun.app.shakeice.R;

/**
 * Created by p on 18/11/14.
 */
public class FragmentDialogViewHelp
        extends DialogFragment {

  /* The activity that creates an instance of this dialog fragment must
   * implement this interface in order to receive event callbacks.
   * Each method passes the DialogFragment in case the host needs to query it. */
  public interface IListenerFragmentDialogViewHelp {
    public void onClickCloseViewHelp(DialogFragment dialog);
  }

  // Use this instance of the interface to deliver action events
  IListenerFragmentDialogViewHelp listener;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    final Dialog dialog = new Dialog(getActivity());
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    //    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    dialog.setContentView(R.layout.dialog_view_help);
    dialog.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT));
    dialog.show();

    TextView tvBody = (TextView) dialog.findViewById(R.id.tvBody);
    tvBody.setText(Html.fromHtml(getString(R.string.s_dialog_view_help)));

    TextView tvOk = (TextView) dialog.findViewById(R.id.tvClose);
    tvOk.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                listener.onClickCloseViewHelp(FragmentDialogViewHelp.this);
                dismiss();
              }
            });

    return dialog;
  }


  // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
  @Override
  public void onAttach(Activity activity) {

    super.onAttach(activity);

    // Verify that the host activity implements the callback interface
    try {
      // Instantiate the NoticeDialogListener so we can send events to the host
      listener = (IListenerFragmentDialogViewHelp) activity;
    } catch (ClassCastException e) {
      // The activity doesn't implement the interface, throw exception
      throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
    }

  }


}
