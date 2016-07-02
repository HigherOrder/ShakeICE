package com.progfun.app.shakeice.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.progfun.app.shakeice.R;
import com.progfun.app.shakeice.common.Common;
import com.progfun.app.shakeice.common.Constants;

public class ReceiverSms
        extends BroadcastReceiver {

  /* Constants */

  private static final String TAG = Context.class.getCanonicalName();

    public ReceiverSms() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

      //---get the SMS message passed in---
      Bundle bundle = intent.getExtras();
      SmsMessage[] msgs = null;
      String messageReceived = "";

      if (bundle != null) {
        //---retrieve the SMS message received---
        Object[] pdus = (Object[]) bundle.get("pdus");
        msgs = new SmsMessage[pdus.length];

        for (int i = 0; i < msgs.length; i++) {
          msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
          messageReceived += msgs[i].getMessageBody().toString();
          messageReceived += "\n";
        }

        // Validate the sender's phone number
        String sendersPhoneNumber = msgs[0].getOriginatingAddress();
        Log.i(TAG, "ReceiverSms: " + sendersPhoneNumber + " says: " + messageReceived);

        // First verify that the first token of the message is what we would expect.
        // Save the verified number if required.
        Common common = new Common(context);
        if ( msgs[0].equals(context.getString(R.string.s_message_number_verification_test)) &&
                (!sendersPhoneNumber.equals((common.fetchSpUsersPhoneNumber()))) ){
          common.saveSpString(Constants.SP_KEY_USERS_PHONE_NUMBER, sendersPhoneNumber);
        }
      }

    }


}
