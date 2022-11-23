package hcmute.edu.vn.zalo_05;

import static hcmute.edu.vn.zalo_05.GenerateToken.GenerateAccessToken.genAccessToken;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.call.StringeeCall2;
import com.stringee.common.StringeeAudioManager;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StatusListener;
import com.stringee.listener.StringeeConnectionListener;
import com.stringee.listener.StringeeRoomListener;
import com.stringee.video.RemoteParticipant;
import com.stringee.video.StringeeRoom;
import com.stringee.video.StringeeVideo;
import com.stringee.video.StringeeVideoTrack;

import org.json.JSONObject;
import org.webrtc.RendererCommon;

import hcmute.edu.vn.zalo_05.GenerateToken.GenerateAccessToken;
import hcmute.edu.vn.zalo_05.Models.VideoTrack;
import hcmute.edu.vn.zalo_05.Services.UserService;

public class StreamActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String ACTION_HOST_STREAM = "action_host_stream";
    public static final String ACTION_GUEST_STREAM = "action_guest_stream";

    private Button btnBack;
    private Button btnCountViewer;
    private Button emptyCommentView;
    private Button btnCommentAction;
    private RecyclerView rvComment;
    private final String token = genAccessToken("SK.0.69KDX8O3hMva2UpK9CVN2sWlhTAtbn6D","bk1qRzBvSFZtRWtSaHpvanY1d2d5OXB5VUVCQXM2MGo=",1728000, UserService.getInstance(this).getCurrentUser().getNumberPhone());

    public static StringeeClient client;
    public static FrameLayout mainView;
    private StringeeRoom stringeeRoom;
    private StringeeVideoTrack stringeeVideoTrack;
    public static VideoTrack mainTrack;
    private String roomToken;

    private StringeeAudioManager audioManager;

    private void initView() {
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnCountViewer = findViewById(R.id.btn_count_viewer);
        emptyCommentView = findViewById(R.id.view_empty_comment);
        btnCommentAction = findViewById(R.id.btn_comment);
        btnCommentAction.setOnClickListener(this);
        mainView = findViewById(R.id.fl_main_track_view);
        mainTrack = null;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void join() {
        stringeeRoom = StringeeVideo.connect(client, roomToken, new StringeeRoomListener() {
            @Override
            public void onConnected(StringeeRoom stringeeRoom) {
                Log.d("StringeeVideo", "=========Room connected=========");
                for(RemoteParticipant remoteParticipant: stringeeRoom.getRemoteParticipants()) {
                    for(StringeeVideoTrack videoTrack: remoteParticipant.getVideoTracks()) {
                        videoTrack.setListener(videoTrackListener(videoTrack));
                        StringeeVideoTrack.Options options = new StringeeVideoTrack.Options();
                        options.audio(true);
                        options.video(true);
                        stringeeRoom.subscribe(videoTrack, options, new StatusListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("StringeeVideo", "=========Subcrise success=========");
                            }

                            @Override
                            public void onError(StringeeError stringeeError) {
                                super.onError(stringeeError);
                                Log.d("StringeeVideo", "=========Subcrise error=========");
                                Log.d("StringeeVideo", "error: " + stringeeError.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onDisconnected(StringeeRoom stringeeRoom) {
                Log.d("StringeeVideo", "=========onDisconnected=========");
            }

            @Override
            public void onError(StringeeRoom stringeeRoom, StringeeError stringeeError) {
                Log.d("StringeeVideo", "=========onError=========");
                Log.d("StringeeVideo", "error: " + stringeeError.getMessage());
            }

            @Override
            public void onParticipantConnected(StringeeRoom stringeeRoom, RemoteParticipant remoteParticipant) {
                Log.d("StringeeVideo", "=========onParticipantConnected=========");
                runOnUiThread(() -> {
                    int length = stringeeRoom.getRemoteParticipants().size();
                    btnCountViewer.setText(String.valueOf(length));
                });
            }

            @Override
            public void onParticipantDisconnected(StringeeRoom stringeeRoom, RemoteParticipant remoteParticipant) {
                Log.d("StringeeVideo", "=========onParticipantDisconnected=========");
                runOnUiThread(() -> {
                    int length = stringeeRoom.getRemoteParticipants().size();
                    btnCountViewer.setText(String.valueOf(length));
                });
            }

            @Override
            public void onVideoTrackAdded(StringeeRoom stringeeRoom, StringeeVideoTrack stringeeVideoTrack) {

            }

            @Override
            public void onVideoTrackRemoved(StringeeRoom stringeeRoom, StringeeVideoTrack stringeeVideoTrack) {

            }

            @Override
            public void onMessage(StringeeRoom stringeeRoom, JSONObject jsonObject, RemoteParticipant remoteParticipant) {

            }

            @Override
            public void onVideoTrackNotification(RemoteParticipant remoteParticipant, StringeeVideoTrack stringeeVideoTrack, StringeeVideoTrack.MediaType mediaType) {

            }
        });
    }

    public void guest() {
        client = new StringeeClient(this);
        client.setConnectionListener(new StringeeConnectionListener() {
            @Override
            public void onConnectionConnected(StringeeClient stringeeClient, boolean b) {
                join();
            }

            @Override
            public void onConnectionDisconnected(StringeeClient stringeeClient, boolean b) {

            }

            @Override
            public void onIncomingCall(StringeeCall stringeeCall) {

            }

            @Override
            public void onIncomingCall2(StringeeCall2 stringeeCall2) {

            }

            @Override
            public void onConnectionError(StringeeClient stringeeClient, StringeeError stringeeError) {

            }

            @Override
            public void onRequestNewToken(StringeeClient stringeeClient) {

            }

            @Override
            public void onCustomMessage(String s, JSONObject jsonObject) {

            }

            @Override
            public void onTopicMessage(String s, JSONObject jsonObject) {

            }
        });
    }

    public void hostStream() {
        client = new StringeeClient(this);
        client.setConnectionListener(new StringeeConnectionListener() {
            @Override
            public void onConnectionConnected(StringeeClient stringeeClient, boolean b) {
                runOnUiThread(() -> {
                    Log.d(Common.TAG, "onConnectionConnected");
                    createRoom();

                });
            }

            @Override
            public void onConnectionDisconnected(StringeeClient stringeeClient, boolean b) {
                runOnUiThread(() -> {
                    Log.d(Common.TAG, "onConnectionDisconnected");

                });
            }

            @Override
            public void onIncomingCall(StringeeCall stringeeCall) {

            }

            @Override
            public void onIncomingCall2(StringeeCall2 stringeeCall2) {

            }

            @Override
            public void onConnectionError(StringeeClient stringeeClient, StringeeError stringeeError) {

            }

            @Override
            public void onRequestNewToken(StringeeClient stringeeClient) {
                runOnUiThread(() -> {
                    Log.d(Common.TAG, "onRequestNewToken");
                    client.connect(token);
                });
            }

            @Override
            public void onCustomMessage(String s, JSONObject jsonObject) {

            }

            @Override
            public void onTopicMessage(String s, JSONObject jsonObject) {

            }
        });

        client.connect(token);
    }

    public void createRoom() {
        StringeeVideoTrack.Options options = new StringeeVideoTrack.Options();
        options.audio(true);
        options.video(true);
        options.screen(false);

        stringeeVideoTrack = StringeeVideo.createLocalVideoTrack(this, options);
        stringeeVideoTrack.getView(this).setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT, RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        mainView.removeAllViews();
        mainView.addView(stringeeVideoTrack.getView(this));
        stringeeVideoTrack.renderView(false);
        stringeeVideoTrack.getView(this).setMirror(true);

        mainTrack = new VideoTrack(stringeeVideoTrack, mainView);


        stringeeRoom = new StringeeRoom(client);
        String roomToken = GenerateAccessToken.genRoomToken("SK.0.69KDX8O3hMva2UpK9CVN2sWlhTAtbn6D","bk1qRzBvSFZtRWtSaHpvanY1d2d5OXB5VUVCQXM2MGo=",1728000, stringeeRoom.getId());
        stringeeRoom = StringeeVideo.connect(client, roomToken, new StringeeRoomListener() {

            @Override
            public void onConnected(StringeeRoom stringeeRoom) {
                Log.d("StringeeVideo", "=========Room connected=========");
                stringeeRoom.publish(stringeeVideoTrack, new StatusListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("StringeeVideo", "=========Publish local success=========");
                        ((TextView)findViewById(R.id.tv_room_token)).setText(roomToken);
                    }

                    @Override
                    public void onError(StringeeError stringeeError) {
                        super.onError(stringeeError);
                        Log.d("StringeeVideo", "=========Publish local error=========");
                        Log.d("StringeeVideo", "error: " + stringeeError.getMessage());
                    }
                });
            }

            @Override
            public void onDisconnected(StringeeRoom stringeeRoom) {
                Log.d("StringeeVideo", "=========onDisconnected=========");

            }

            @Override
            public void onError(StringeeRoom stringeeRoom, StringeeError stringeeError) {
                Log.d("StringeeVideo", "=========onError=========");
                Log.d("StringeeVideo", "error: " + stringeeError.getMessage());
            }

            @Override
            public void onParticipantConnected(StringeeRoom stringeeRoom, RemoteParticipant remoteParticipant) {
                Log.d("StringeeVideo", "=========onParticipantConnected=========");
                runOnUiThread(() -> {
                    int length = stringeeRoom.getRemoteParticipants().size();
                    btnCountViewer.setText(String.valueOf(length));
                });
            }

            @Override
            public void onParticipantDisconnected(StringeeRoom stringeeRoom, RemoteParticipant remoteParticipant) {
                Log.d("StringeeVideo", "=========onParticipantDisconnected=========");
                runOnUiThread(() -> {
                    int length = stringeeRoom.getRemoteParticipants().size();
                    btnCountViewer.setText(String.valueOf(length));
                });
            }

            @Override
            public void onVideoTrackAdded(StringeeRoom stringeeRoom, StringeeVideoTrack stringeeVideoTrack) {

            }

            @Override
            public void onVideoTrackRemoved(StringeeRoom stringeeRoom, StringeeVideoTrack stringeeVideoTrack) {

            }

            @Override
            public void onMessage(StringeeRoom stringeeRoom, JSONObject jsonObject, RemoteParticipant remoteParticipant) {

            }

            @Override
            public void onVideoTrackNotification(RemoteParticipant remoteParticipant, StringeeVideoTrack stringeeVideoTrack, StringeeVideoTrack.MediaType mediaType) {

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stream);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



//        if(getIntent() != null) {
//            roomToken = getIntent().getStringExtra("room_token");
//        }
        initView();
        if(getIntent() != null) {
            switch (getIntent().getAction()) {
                case ACTION_HOST_STREAM:
                    audioManager = StringeeAudioManager.create(this);
                    audioManager.start((selectedAudioDevice, availableAudioDevices) -> {
                    });
                    audioManager.setSpeakerphoneOn(true);
                    hostStream();
                    break;

                case ACTION_GUEST_STREAM:
                    roomToken = getIntent().getStringExtra("room_token");
                    guest();
                    break;
            }
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }



    private StringeeVideoTrack.Listener videoTrackListener(StringeeVideoTrack stringeeVideoTrack) {
        return new StringeeVideoTrack.Listener() {
            @Override
            public void onMediaAvailable() {
                runOnUiThread(() -> {
                    Log.d("StringeeVideo", "stringeeVideoTrack: " + stringeeVideoTrack.getId());
                    mainView.removeAllViews();
                    stringeeVideoTrack.getView(StreamActivity.this).setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT, RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                    mainView.addView(stringeeVideoTrack.getView(StreamActivity.this));
                    stringeeVideoTrack.renderView(true);
                    stringeeVideoTrack.getView(StreamActivity.this).setMirror(false);
                });
            }

            @Override
            public void onMediaStateChange(StringeeVideoTrack.MediaState mediaState) {

            }
        };
    }

    private void leaveRoom() {
        if (stringeeRoom != null) {
            //release localVideoTrack
            stringeeRoom.unpublish(stringeeVideoTrack, new StatusListener() {
                @Override
                public void onSuccess() {
                    stringeeVideoTrack.release();
                }
            });

            //leaveRoom
            stringeeRoom.leave(true, new StatusListener() {
                @Override
                public void onSuccess() {
                    StringeeVideo.release(stringeeRoom);
                }
            });
        }
        if (audioManager != null) {
            audioManager.stop();
            audioManager = null;
        }
        finish();
    }
}