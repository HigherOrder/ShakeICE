package com.progfun.app.shakeice.ui;

import android.app.Activity;
import android.os.Bundle;

import com.progfun.app.shakeice.R;

public class ViewAboutActivity extends Activity {

  public static final String URL_ABOUT = "file:///android_asset/About.html";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_about);

//    WebView wv = (WebView) findViewById(R.id.wvAbout);
//    wv.loadUrl(URL_ABOUT);
  }

}
