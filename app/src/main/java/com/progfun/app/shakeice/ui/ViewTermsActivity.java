package com.progfun.app.shakeice.ui;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.progfun.app.shakeice.R;

public class ViewTermsActivity
        extends Activity {

  public static final String URL_TERMS = "file:///android_asset/TermsOfService.html";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_terms);

    WebView wv = (WebView) findViewById(R.id.wvTerms);
    wv.loadUrl(URL_TERMS);

  }

}
