package magosoftware.radiofacomufjf;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PAUSE;
import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PLAY;

/**
 * Created by Alexandre on 06/09/2017.
 */

public class RadioFragment extends Fragment {

    //    String mms_url = "mms://radio.correios.com.br/radio";
//    String mms_url = "rtsp://radio.correios.com.br/radio";
//    String mms_url = "http://bbcwssc.ic.llnwd.net/stream/bbcwssc_mp1_ws-einws";
    String mms_url = "http://200.17.70.162:8080/facom";

    public static final String CHANNEL_1_ID = "channel1";

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    //    String mms_url = "mms://200.17.70.162:8080/Rádio Universitária";
    boolean botaoPlay = false;
    //    boolean primeiro = true;
    ProgressBar progressBar;
    ImageView playButton;
    ImageButton facebookButton;
    ImageView reload;
    //    boolean reloadButton = false;
    boolean carregando = false;
    boolean tocando = false;
    View view;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    //    FFmpegMediaPlayerSerial mp;
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    public SimpleExoPlayer player;
    MediaSessionCompat mMediaSession;
    PlaybackStateCompat.Builder mStateBuilder;
    MediaSessionConnector mediaSessionConnector;
    private MediaControllerCompat mController;
    private boolean isActivityActive = true;
    NotificationManager notificationManager;
    TextView radioStatus;
    TextView espectadores;
    int quantidade;
    int previousPlaybackState = 2;
    int status = 0;
    boolean hasInternet;
    boolean hasInternetBefore = false;
    boolean radioOnline;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState==null) {
            view = inflater.inflate(R.layout.radio_fragment, container, false);

            getActivity().startService(new Intent(getActivity(), ClosingService.class));

            progressBar = view.findViewById(R.id.progressBar);
            playButton = view.findViewById(R.id.playButton);
            facebookButton = view.findViewById(R.id.facebook);
            reload = view.findViewById(R.id.reload);
            radioStatus = view.findViewById(R.id.radio_status);
            espectadores = view.findViewById(R.id.espectadores);
            progressBar.setVisibility(View.INVISIBLE);

            sharedPreferences = getActivity().getSharedPreferences("main", 0);
            editor = sharedPreferences.edit();

            mDatabase.child("radio").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        mms_url = dataSnapshot.getValue(String.class);
                        checkInternet();
                    } catch (NullPointerException e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            setupMediaPlayer();

            checkInternet();

            createNotificationChannels();

            mediaSessionBegin();

//            tocando = sharedPreferences.getBoolean("tocando", false);
//            carregando = sharedPreferences.getBoolean("carregando", false);

            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_PAUSE);
            filter.addAction(ACTION_PLAY);
            getActivity().registerReceiver(receiver, filter);

            view.findViewById(R.id.scroll).setSelected(true);

            facebookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("https://www.facebook.com/RadioFacomOficial/"));
                    startActivity(intent);
                }
            });

            reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopPlayer();
                }
            });

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar();
                    initMediaPlayer();
                }
            });
        }

        return view;
    }

    private void snackbar() {
        if(!radioOnline) {
            Snackbar mySnackbar = Snackbar.make(view.findViewById(R.id.coordinator),
                    "Rádio Offline", Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }
        if(!hasInternet) {
            Snackbar mySnackbar = Snackbar.make(view.findViewById(R.id.coordinator),
                    "Por favor, verifique sua conexão", Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }
    }

    private void initMediaPlayer() {
        checkInternet();
        Log.d("DEV/State", ""+player.getPlaybackState());
        if((status == 3 || status == 0) && hasInternet && radioOnline) {
            status = 1;
            progressBar.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(mms_url);
            MediaSource mediaSource = buildMediaSource(uri);
            player.prepare(mediaSource, true, false);
            player.setPlayWhenReady(true);
            buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
        }
    }

    private void stopPlayer() {
        status = 3;
        player.stop();
        buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
    }

    @Override
    public void onResume() {
        super.onResume();
        isActivityActive = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isActivityActive = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        stopPlayer();
        if(status == 2) {
            status = 3;
            player.stop();
        }
//        buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
        player.release();
        notificationManager.cancel(1);
        getActivity().unregisterReceiver(receiver);
    }

//    public static void clearTask() {
//        mDatabase.child("espec").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                0int quantidade = dataSnapshot.getValue(Integer.class);
//                mDatabase.setValue(quantidade-1);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void setupMediaPlayer() {
        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        player = ExoPlayerFactory.newSimpleInstance(getActivity());
        mDatabase.child("espec").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quantidade = dataSnapshot.getValue(Integer.class);
                espectadores.setText("" + quantidade);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                checkInternet();
                if(hasInternet && radioOnline) {
                    Log.d("DEV/PlaybackState", "STATE: " + player.getPlaybackState());
                    if (playbackState == Player.STATE_READY) {
                        radioStatus.setText("ON");
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    if (status == 1 &&
                            (previousPlaybackState == PlaybackState.STATE_STOPPED ||
                                    previousPlaybackState == PlaybackState.STATE_PAUSED)) {
                        status = 2;
                        Log.d("DEV/PlaybackState", playbackState + "");
                        previousPlaybackState = playbackState;
                        mDatabase.child("espec").setValue(quantidade + 1);
                    }
                    if ((playbackState == PlaybackState.STATE_STOPPED ||
                            playbackState == PlaybackState.STATE_PAUSED) &&
                            status == 3) {
                        status = 0;
                        Log.d("DEV/PlaybackState", playbackState + "");
                        previousPlaybackState = playbackState;
                        mDatabase.child("espec").setValue(quantidade - 1);
                    }
                }
                else {

                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.d("DEV/Error", "Código do erro:"+error.type);
//                Log.d("DEV/PlaybackState", "STATE1: "+player.getPlaybackState());
//                if(error.type==0) {
//                    radioStatus.setText("OFF");
//                    player.retry();
//                    progressBar.setVisibility(View.INVISIBLE);
//                    status = 0;
//                    Snackbar mySnackbar = Snackbar.make(view.findViewById(R.id.coordinator),
//                            "Rádio Offline", Snackbar.LENGTH_SHORT);
//                    mySnackbar.show();
//                }
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.d("DEV/LOADING", "Loading: "+isLoading);
                Log.d("DEV/PlaybackState", "STATE2: "+player.getPlaybackState());
                if(!isLoading) {

                }
            }
        });
        Uri uri = Uri.parse(mms_url);
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);
    }



//    private void handleIntent( Intent intent ) {
//        if( intent == null || intent.getAction() == null )
//            return;
//
//        String action = intent.getAction();
//
//        if( action.equalsIgnoreCase( ACTION_PLAY ) ) {
//            mController.getTransportControls().play();
//        } else if( action.equalsIgnoreCase( ACTION_PAUSE ) ) {
//            mController.getTransportControls().pause();
//        } else if( action.equalsIgnoreCase( ACTION_FAST_FORWARD ) ) {
//            mController.getTransportControls().fastForward();
//        } else if( action.equalsIgnoreCase( ACTION_REWIND ) ) {
//            mController.getTransportControls().rewind();
//        } else if( action.equalsIgnoreCase( ACTION_PREVIOUS ) ) {
//            mController.getTransportControls().skipToPrevious();
//        } else if( action.equalsIgnoreCase( ACTION_NEXT ) ) {
//            mController.getTransportControls().skipToNext();
//        } else if( action.equalsIgnoreCase( ACTION_STOP ) ) {
//            mController.getTransportControls().stop();
//        }
//    }

    private NotificationCompat.Action generateAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(intentAction);
//        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action.Builder(icon, title, pendingIntent).build();
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
    }

    private void buildNotification(NotificationCompat.Action action) {

        String title = "Rádio Facom";
        String message = "103,9 FM";

        Bitmap artwork = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);

        Intent intent = new Intent(getActivity(), RadioMain.class);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_1_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(artwork)
//                .addAction(R.drawable.ic_stop_notification, "stop", pendingIntent)
                .addAction(action)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0)
                        .setMediaSession(mMediaSession.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .build();

        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
//        this.notificationManager = new PlayerNotificationManager(
//                getActivity(),
//                CHANNEL_1_ID,
//                1,
//                new DescriptionAdapter(getActivity()));
//        this.notificationManager.setPlayer(player);
        notificationManager.notify(1, notification);
//        notificationManager.notify( 1, notification);
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel1.setDescription("This is Channel 1");

            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ACTION_PAUSE.equals(action)) {
                Log.d("DEV/RadioFragment", "STOP");
                carregando = false;
                stopPlayer();
            }
            else if(ACTION_PLAY.equals(action)) {
                Log.d("DEV/RadioFragment", "PLAY");
                snackbar();
                if(player.getPlaybackState() != PlaybackState.STATE_PLAYING && hasInternet && radioOnline) {
                    progressBar.setVisibility(View.VISIBLE);
                    status = 1;
                    player.retry();
                    buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
                }
            }
        }
    };

    private void checkInternet() {
        Log.d("DEV/Radio", mms_url);
        new InternetCheck(mms_url, internet -> {
            Log.d("DEV/Internet", internet[0]+", "+internet[1]);
            if(!internet[0]) {
                hasInternet = false;
                status = 1;
//                Toast.makeText(getActivity(), "Por favor, verifique sua conexão",
//                        Toast.LENGTH_LONG).show();
                radioStatus.setText("OFF");
                espectadores.setText("0");
                progressBar.setVisibility(View.INVISIBLE);
                stopPlayer();
            } else {
                hasInternet = true;
                if(!hasInternetBefore) {
                    Uri uri = Uri.parse(mms_url);
                    MediaSource mediaSource = buildMediaSource(uri);
                    player.prepare(mediaSource, true, false);
                }
            }
            hasInternetBefore = hasInternet;
            if(!internet[1]) {
                radioOnline = false;
                status = 1;
                radioStatus.setText("OFF");
//                player.retry();
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                radioOnline = true;
                status = 0;
                radioStatus.setText("ON");
//                player.retry();
//                if(!hasInternetBefore) {
//                    Uri uri = Uri.parse(mms_url);
//                    MediaSource mediaSource = buildMediaSource(uri);
//                    player.prepare(mediaSource, true, false);
//                }
            }
        });
    }

    private void mediaSessionBegin() {
        mMediaSession = new MediaSessionCompat(getActivity(), "this");
        try {
            mController = new MediaControllerCompat(getActivity(), mMediaSession.getSessionToken());
        } catch (RemoteException e) {

        }
//        if(Build.VERSION.SDK_INT >= 21) {
//            mMediaSession.setCallback(new MediaSessionCompat.Callback() {
//                @Override
//                public void onPlay() {
//                    super.onPlay();
//                    Log.e("MediaPlayerService", "onPlay");
//                    buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
//                }
//
//                @Override
//                public void onPause() {
//                    super.onPause();
//                    Log.e("MediaPlayerService", "onPause");
//                    buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
//                }
//
//                @Override
//                public void onSkipToNext() {
//                    super.onSkipToNext();
//                    Log.e("MediaPlayerService", "onSkipToNext");
//                    //Change media here
//                    buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
//                }
//
//                @Override
//                public void onSkipToPrevious() {
//                    super.onSkipToPrevious();
//                    Log.e("MediaPlayerService", "onSkipToPrevious");
//                    //Change media here
//                    buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
//                }
//
//                @Override
//                public void onFastForward() {
//                    super.onFastForward();
//                    Log.e("MediaPlayerService", "onFastForward");
//                    //Manipulate current media here
//                }
//
//                @Override
//                public void onRewind() {
//                    super.onRewind();
//                    Log.e("MediaPlayerService", "onRewind");
//                    //Manipulate current media here
//                }
//
//                @Override
//                public void onStop() {
//                    super.onStop();
//                    Log.e("MediaPlayerService", "onStop");
//                    //Stop media player here
//                    NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//                    notificationManager.cancel(1);
//                    Intent intent = new Intent(getActivity(), RadioFragment.class);
//                    getActivity().stopService(intent);
//                }
//
//                @Override
//                public void onSeekTo(long pos) {
//                    super.onSeekTo(pos);
//                }
//            });
//
//            mMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
//                    MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
//            mediaSessionConnector = new MediaSessionConnector(mMediaSession);
//            mediaSessionConnector.setPlayer(player, null);
//        }
        mMediaSession.setActive(true);
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//
//    }
}
