package com.progfun.app.shakeice.ui;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.progfun.app.shakeice.R;

public class ViewPolicyActivity extends Activity {

  public static final String URL_POLICY = "file:///android_asset/PrivacyPolicy.html";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_policy);

    WebView wv = (WebView) findViewById(R.id.wvPolicy);
    wv.loadUrl(URL_POLICY);

  }

}
