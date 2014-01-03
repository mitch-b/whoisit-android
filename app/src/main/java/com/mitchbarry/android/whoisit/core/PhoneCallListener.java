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

import java.io.IOException;

public class PhoneCallListener extends PhoneStateListener {
    private final String TAG = this.getClass().getSimpleName();

    private Context context = null;
    private int PREVIOUS_CALL_STATE;
    private static AudioManager audioManager;
    private static MediaPlayer mediaPlayer;
    private static int ringVolume = -100;
    private static int alarmVolume = -100;
    private static boolean audioStreamsModified = false;
    private static PhoneCallListener phoneCallListenerInstance;

    public PhoneCallListener(Context context) {
        this.context = context;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static PhoneCallListener getPhoneCallListener(Context context)
    {
        if (phoneCallListenerInstance == null)
            phoneCallListenerInstance = new PhoneCallListener(context);
        return phoneCallListenerInstance;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        boolean silentMode = this.audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT ||
                this.audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE;
        if (state != PREVIOUS_CALL_STATE && !silentMode) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    killMediaPlayer();
                    resetAudioStreams();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    killMediaPlayer();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    // save values so I can restore them on STATE_IDLE
                    alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                    ringVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                    PhoneGroup matchedGroup = null;
                    Boolean customRingtoneSet = false;

                    // if incoming call is from a contact already, no need to check sqlite tables
                    Uri contactRingtoneUri = Uri.withAppendedPath(
                            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                            Uri.encode(incomingNumber));

                    if (contactRingtoneUri != null) {
                        String[] projection = new String[] {ContactsContract.PhoneLookup.CUSTOM_RINGTONE};
                        Cursor customRingCursor = this.context.getContentResolver().query(contactRingtoneUri, projection, null, null, null);
                        if (customRingCursor != null && customRingCursor.getCount() > 0) {
                            while (customRingCursor.moveToNext()) {
                                String contactCustomRingtone = customRingCursor.getString(customRingCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.CUSTOM_RINGTONE));
                                if (contactCustomRingtone != null) {
                                    customRingtoneSet = true;
                                    break;
                                }
                            }
                        }
                    }

                    // if this # does not already have a custom ringtone, check phone matches
                    if (!customRingtoneSet) {
                        WhoIsItMatcher.init(context);
                        matchedGroup = WhoIsItMatcher.findMatchingGroup(incomingNumber);
                    }

                    if (matchedGroup != null) {
                        try {
                            audioStreamsModified = true;

                            // if ringtone set
                            mediaPlayer = new MediaPlayer();
                            // mute current ringtone if any
                            audioManager.setStreamMute(AudioManager.STREAM_RING, true);
                            mediaPlayer.setDataSource(this.context, Uri.parse(matchedGroup.getRingtone()));
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
        }
        PREVIOUS_CALL_STATE = state;
    }

    private void killMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void resetAudioStreams() {
        // audioStreamsModified is only set to true if a match was found
        if (audioStreamsModified) {
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
        }
        audioStreamsModified = false;
    }
}
