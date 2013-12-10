package com.mitchbarry.android.whoisit.core;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.TelephonyManager;

import static android.telephony.PhoneStateListener.LISTEN_CALL_STATE;

/**
 * Created by Mitchell on 12/9/13.
 */
public class PhoneCallReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PhoneCallListener phoneListener = PhoneCallListener.getPhoneCallListener(context);
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, LISTEN_CALL_STATE);
    }
}
