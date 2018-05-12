package io.agora.xmcamera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

import io.agora.rtc.Constants;
import io.agora.rtc.IEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.RtcStats;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    public static final int BASE_VALUE_PERMISSION = 0X0001;
    public static final int PERMISSION_REQ_ID_RECORD_AUDIO = BASE_VALUE_PERMISSION + 1;
    public static final int PERMISSION_REQ_ID_CAMERA = BASE_VALUE_PERMISSION + 2;
    public static final int PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = BASE_VALUE_PERMISSION + 3;

    private RtcEngine mRtcEngine;
    private IEventHandler mEventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkSelfPermissions();

        try {
            mEventHandler = new IEventHandler() {
                @Override
                public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                    Log.e(TAG, "IEventHandler onJoinChannelSuccess");
                }

                @Override
                public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
                    Log.e(TAG, "IEventHandler onRejoinChannelSuccess");
                }

                @Override
                public void onWarning(int warn) {
                    Log.e(TAG, "IEventHandler onWarning: " + warn);
                }

                @Override
                public void onError(int err) {
                    Log.e(TAG, "IEventHandler onError: " + err);
                }

                @Override
                public void onUserJoined(int uid, int elapsed) {
                    Log.e(TAG, "IEventHandler onUserJoined: " + uid);
                }

                @Override
                public void onUserOffline(int uid, int reason) {
                    Log.e(TAG, "IEventHandler onUserOffline: " + uid + ", reason: " + reason);
                }

                @Override
                public void onRtcStats(RtcStats stats) {
                    // Log.e(TAG, "IEventHandler onRtcStats: " + stats.toString());
                }

                @Override
                public void onConnectionLost() {
                    Log.e(TAG, "IEventHandler onConnectionLost");
                }

                @Override
                public void onUserMuteVideo(int uid, boolean muted) {
                    Log.e(TAG, "IEventHandler onUserMuteVideo: " + uid + ", muted: " + muted);
                }

                @Override
                public void onReceivedVideoData(int uid, byte[] data, int isKeyFrame) {
                    if (data == null) {
                        // Log.e(TAG, "IEventHandler onReceivedVideoData error");
                        return;
                    }
                    Log.e(TAG, "IEventHandler onReceivedVideoData: " + uid + ", size: " + data.length);
                }

                @Override
                public void onReceivedAudioData(int uid, byte[] data) {
                    if (data == null) return;
                    Log.e(TAG, "IEventHandler onReceivedAudioData: " + uid + "size: " + data.length);
                }
            };

            mRtcEngine = RtcEngine.create(this, getString(R.string.private_broadcasting_app_id), mEventHandler);
            mRtcEngine.setParameters_c("{\"rtc.log_filter\": 65535}");
            int r = mRtcEngine.enableVideo_c();
            r = mRtcEngine.joinChannel_c(null, "wangyy", 0);
            mThread.start();
        } catch (Exception ex) {
            Log.e(TAG, "Create engine error: " + ex.toString());
        }
    }

    private boolean checkSelfPermissions() {
        return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE) &&
                checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) &&
                (checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA));
    }

    protected final boolean checkPermissions(int cRole) {
        if (cRole == Constants.CLIENT_ROLE_AUDIENCE) {
            return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
        }

        return checkSelfPermissions();
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.d(TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult " + requestCode + " " + Arrays.toString(permissions) + " " + Arrays.toString(grantResults));
        switch (requestCode) {
            case PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    finish();
                }
                break;
            }
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA);
                } else {
                    // showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                }
                break;
            }
            case PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // showLongToast("No permission for " + Manifest.permission.CAMERA);
                }
                break;
            }
        }
    }

    private Thread mThread = new Thread() {
        @Override
        public void run() {
            byte[] videoData = new byte[1024];
            for (int i = 0; i < videoData.length; ++i) {
                videoData[i] = 1;
            }
            byte[] audioData = new byte[128];
            for (int i = 0; i < audioData.length; ++i) {
                audioData[i] = 1;
            }
            /*
            for (int i = 0; i < 8; i++) {
                mRtcEngine.sendVideoData_c(videoData, 0);
                mRtcEngine.sendAudioData_c(audioData);
                Log.e (TAG, "sdk Audio Bitrate: " + mRtcEngine.getAudioRecommendedBitrate_c() + ", Video Bitrate: " + mRtcEngine.getVideoRecommendedBitrate_c());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Log.e(TAG, ex.toString());
                }
            }

            //	agora_rtc_disable_video ();
            Log.e (TAG, "sdk Start sending non-Key video frame...");

            for (int i = 0; i < 8; i++) {
                mRtcEngine.sendVideoData_c(videoData, 0);
                mRtcEngine.sendAudioData_c(audioData);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Log.e(TAG, ex.toString());
                }
            }

           // agora_rtc_disable_audio ();
           // agora_rtc_enable_video ();

            Log.e (TAG, "sdk Start sending non-Key video frame...");

            for (int i = 0; i < 8; i++) {
                mRtcEngine.sendVideoData_c(videoData, 0);
                mRtcEngine.sendAudioData_c(audioData);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Log.e(TAG, ex.toString());
                }
            }
            */

            for (;;) {
                mRtcEngine.sendVideoData_c(videoData, 1);
                mRtcEngine.sendAudioData_c(audioData);
                // Log.e (TAG, "agora sdk Audio Bitrate: " + mRtcEngine.getAudioRecommendedBitrate_c() + ", Video Bitrate: " + mRtcEngine.getVideoRecommendedBitrate_c());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Log.e(TAG, ex.toString());
                }
            }
        }
    };
}
