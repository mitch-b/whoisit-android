package com.mitchbarry.android.whoisit.core;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;

public class PhoneCallListener extends PhoneStateListener {
    private Context context = null;
    private int PREVIOUS_CALL_STATE;
    private static AudioManager audioManager;
    private static MediaPlayer mediaPlayer;
    private static int ringVolume = -100;
    private static int alarmVolume = -100;
    private static PhoneCallListener phoneCallListenerInstance;

    public PhoneCallListener(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static PhoneCallListener getPhoneCallListener(Context context)
    {
        if (phoneCallListenerInstance == null)
            phoneCallListenerInstance = new PhoneCallListener(context);
        return phoneCallListenerInstance;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        if (state != PREVIOUS_CALL_STATE) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    killMediaPlayer();
                    audioManager.setStreamMute(AudioManager.STREAM_RING, false);
                    audioManager.setStreamVolume(
                            AudioManager.STREAM_RING,
                            ringVolume,
                            AudioManager.FLAG_ALLOW_RINGER_MODES);
                    // if this setStreamMute isn't here, for some reason, it fails (silently) to adjust alarm back to volume
                    // TODO: someone please explain why ...
                    audioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
                    audioManager.setStreamVolume(
                            AudioManager.STREAM_ALARM,
                            alarmVolume,
                            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    killMediaPlayer();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d("WhoIsIt", String.format("Captured call from %s", incomingNumber));
                    // save values so I can restore them on STATE_IDLE
                    alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                    ringVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);

                    /*
                        Check if incomingNumber fits within user's defined number ranges
                     */
                    String contactRingtoneUriString = null;

                    Uri contactRingtoneUri = Uri.withAppendedPath(
                            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                            Uri.encode(incomingNumber));

                    if (contactRingtoneUri != null) {
                        String[] projection = new String[] {ContactsContract.PhoneLookup.CUSTOM_RINGTONE};
                        Cursor customRingCursor = this.context.getContentResolver().query(contactRingtoneUri, projection, null, null, null);
                        if (customRingCursor != null && customRingCursor.getCount() > 0) {
                            while (customRingCursor.moveToNext()) {
                                contactRingtoneUriString = customRingCursor.getString(customRingCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.CUSTOM_RINGTONE));
                                if (contactRingtoneUriString != null) {
                                    Log.d("WhoIsIt", "Contact has a custom ringtone: " + contactRingtoneUriString);
                                    break;
                                }
                            }
                        }
                    }
                    if (contactRingtoneUriString != null) {
                        try {
                            mediaPlayer = new MediaPlayer();
                            // mute current ringtone if any
                            audioManager.setStreamMute(AudioManager.STREAM_RING, true);
                            mediaPlayer.setDataSource(this.context, Uri.parse(contactRingtoneUriString));
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                            audioManager.setStreamVolume(
                                    AudioManager.STREAM_ALARM,
                                    ringVolume,
                                    AudioManager.FLAG_ALLOW_RINGER_MODES);
                            mediaPlayer.setWakeMode(this.context, PowerManager.PARTIAL_WAKE_LOCK);
                            mediaPlayer.setLooping(true);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                            audioManager.setStreamMute(AudioManager.STREAM_RING, false);
                        }
                    }
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

    private void killMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
