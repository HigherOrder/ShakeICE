package com.progfun.app.shakeice.ui.clicklisteners;

import com.progfun.app.shakeice.models.Contact;

/**
 * Created by p on 19/02/15.
 */
public interface IListenerOnLocationChanged {
  public void onLocationChanged(
    Contact itemCurrent,
    String status
  );
}
