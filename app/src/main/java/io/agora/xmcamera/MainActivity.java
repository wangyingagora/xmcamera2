package io.agora.xmcamera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.agora.rtc.AgoraRtcEngine;
import io.agora.rtc.Constants;
import io.agora.rtc.IEventHandler;
import io.agora.rtc.IRtcEngine;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.RtcStats;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private AgoraRtcEngine mRtcEngine;
    private IEventHandler mEventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mEventHandler = new IEventHandler() {
                @Override
                public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                    Log.e(TAG, "onJoinChannelSuccess");
                }

                @Override
                public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {

                }

                @Override
                public void onWarning(int warn) {

                }

                @Override
                public void onError(int err) {
                    Log.e(TAG, "code: " + err);
                }

                @Override
                public void onUserJoined(int uid, int elapsed) {

                }

                @Override
                public void onUserOffline(int uid, int reason) {

                }

                @Override
                public void onRtcStats(RtcStats stats) {

                }

                @Override
                public void onConnectionLost() {

                }

                @Override
                public void onUserMuteVideo(int uid, boolean muted) {

                }
            };

            mRtcEngine = AgoraRtcEngine.create(this, getString(R.string.private_broadcasting_app_id), mEventHandler);
            ((AgoraRtcEngine)mRtcEngine).setParameters("{\"rtc.log_filter\": 65535}");
            //mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            //mRtcEngine.enableVideo();
            //mRtcEngine.enableDualStreamMode(true);

            //mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_480P, false);
            // mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);

            mRtcEngine.enableVideo();
            mRtcEngine.joinChannel(null, "test", 0);
            //mRtcEngine.joinChannel(null, "arcore", "ARCore with RtcEngine", 0);
        } catch (Exception ex) {
            Log.e(TAG, "Create engine error: " + ex.toString());
        }

    }
}
