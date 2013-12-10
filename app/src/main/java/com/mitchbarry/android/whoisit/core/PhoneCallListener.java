package com.mitchbarry.android.whoisit.core;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneCallListener extends PhoneStateListener {
    private Context context = null;
    private int PREVIOUS_CALL_STATE;
    private Uri DEFAULT_RINGTONE;
    private Uri CURRENT_RINGTONE;
    private static PhoneCallListener phoneCallListenerInstance;

    public PhoneCallListener(Context context) {
        this.context = context;
        DEFAULT_RINGTONE = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
    }

    public static PhoneCallListener getPhoneCallListener(Context context)
    {
        if (phoneCallListenerInstance == null)
            phoneCallListenerInstance = new PhoneCallListener(context);
        return phoneCallListenerInstance;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        /*
            Check if incomingNumber fits within user's defined number ranges
         */
        // if (match) { ... }, otherwise, don't mess with it
        if (state != PREVIOUS_CALL_STATE) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d("WhoIsIt", "From talking/ringing to quiet ... revert ringtone to default if changed");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d("WhoIsIt", "We're in a call. Tap in for NSA.");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d("WhoIsIt", String.format("Captured call from %s - change ringtone to new (or default if non-matching) one!", incomingNumber));
                    break;
                default:
                    break;
            }
            Log.d("WhoIsIt", String.format("%s -> %s", getReadableStateName(PREVIOUS_CALL_STATE), getReadableStateName(state)));
        }
        PREVIOUS_CALL_STATE = state;
    }

    private String getReadableStateName(int state)
    {
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                return "IDLE";
            case TelephonyManager.CALL_STATE_OFFHOOK:
                return "OFF_HOOK";
            case TelephonyManager.CALL_STATE_RINGING:
                return "RINGING";
            default:
                return "???";
        }
    }
}
