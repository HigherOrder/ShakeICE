package com.progfun.app.shakeice.ui;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;

import com.progfun.app.shakeice.R;

/**
 * A simple {@link Fragment} subclass.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FragmentPreferences
        extends PreferenceFragment {


  public FragmentPreferences() {
    // Required empty public constructor
  }

   @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // Load the preferences from an XML resource
      addPreferencesFromResource(R.xml.pref_general);
    }

//  @Override
//  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//    TextView textView = new TextView(getActivity());
//    textView.setText(R.string.hello_blank_fragment);
//    return textView;
//  }


}
