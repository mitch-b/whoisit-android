package com.mitchbarry.android.whoisit.core;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Mitchell on 12/9/13.
 */
public class SmsReceiver extends WakefulBroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();
    private static final String PDUs = "pdus";

    private static AudioManager audioManager;
    private static MediaPlayer mediaPlayer;
    private static int ringVolume = -100;
    private static int alarmVolume = -100;
    private static boolean audioStreamsModified = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        if (extras != null) {
            if (audioManager == null)
                audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
            ringVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);

            String incomingNumber = "";
            PhoneGroup matchedGroup = null;

            Object[] smsExtras = (Object[]) extras.get(PDUs);
            for (int i = 0; i < smsExtras.length; i++) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtras[i]);
                incomingNumber = sms.getOriginatingAddress();
            }
            if (incomingNumber != null) {
                WhoIsItMatcher.init(context);
                matchedGroup = WhoIsItMatcher.findMatchingGroup(incomingNumber);
                if (matchedGroup != null && matchedGroup.getRingSms() == true) {
                    if (ringVolume > 0) {
                        playTone(matchedGroup, context);
                    } else {
                        Log.d(TAG, "Ringer volume not audible");
                    }
                }
            }
        }
    }

    private void playTone(PhoneGroup group, Context context) {
        try {
            audioStreamsModified = true;
            // if ringtone set
            mediaPlayer = new MediaPlayer();
            // mute current ringtone if any
            audioManager.setStreamMute(AudioManager.STREAM_RING, true);
            mediaPlayer.setDataSource(context, Uri.parse(group.getRingtone()));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            audioManager.setStreamVolume(
                    AudioManager.STREAM_ALARM,
                    ringVolume,
                    AudioManager.FLAG_ALLOW_RINGER_MODES);
            mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);

            /*
                If we ever want to loop until notification is picked up, this
                solution will not work.
             */
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                public void onSeekComplete(MediaPlayer mp) {
                    killMediaPlayer();
                    resetAudioStreams();
                }
            });
            mediaPlayer.setLooping(false);

            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error while starting MediaPlayer", e);
            audioManager.setStreamMute(AudioManager.STREAM_RING, false);
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

    private void killMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
