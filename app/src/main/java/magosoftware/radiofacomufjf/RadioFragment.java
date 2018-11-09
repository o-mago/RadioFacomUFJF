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
import android.media.Rating;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaButtonReceiver;
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

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.DefaultPlaybackController;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_FAST_FORWARD;
import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_NEXT;
import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PAUSE;
import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PLAY;
import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PREVIOUS;
import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_REWIND;
import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_STOP;

//import wseemann.media.FFmpegMediaPlayer;

/**
 * Created by Alexandre on 06/09/2017.
 */

public class RadioFragment extends Fragment {

    //    String mms_url = "mms://radio.correios.com.br/radio";
//    String mms_url = "rtsp://radio.correios.com.br/radio";
    String mms_url = "http://bbcwssc.ic.llnwd.net/stream/bbcwssc_mp1_ws-einws";

    public static final String CHANNEL_1_ID = "channel1";

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState==null) {
            view = inflater.inflate(R.layout.radio_fragment, container, false);

            progressBar = view.findViewById(R.id.progressBar);
            playButton = view.findViewById(R.id.playButton);
            facebookButton = view.findViewById(R.id.facebook);
            reload = view.findViewById(R.id.reload);
            progressBar.setVisibility(View.INVISIBLE);

            sharedPreferences = getActivity().getSharedPreferences("main", 0);
            editor = sharedPreferences.edit();

//        mp = (FFmpegMediaPlayerSerial) getArguments().getSerializable("mediaPlayer");
            setupMediaPlayer();
            createNotificationChannels();

            mediaSessionBegin();

            tocando = sharedPreferences.getBoolean("tocando", false);
            carregando = sharedPreferences.getBoolean("carregando", false);

            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_PAUSE);
            filter.addAction(ACTION_PLAY);
            getActivity().registerReceiver(receiver, filter);

            view.findViewById(R.id.scroll).setSelected(true);

//        initMediaPlayer();

//        mp.setOnPreparedListener(new FFmpegMediaPlayer.OnPreparedListener() {
//
//            @Override
//            public void onPrepared(FFmpegMediaPlayer mp) {
////                if(!reloadButton) {
//                mp.start();
////                }
//                playButton.bringToFront();
//                progressBar.setVisibility(View.INVISIBLE);
////                reloadButton = false;
//                carregando = false;
//                tocando = true;
//                editor.putBoolean("tocando", true);
//                editor.putBoolean("carregando", false);
//                editor.apply();
//            }
//        });
//        mp.setOnErrorListener(new FFmpegMediaPlayer.OnErrorListener() {
//
//            @Override
//            public boolean onError(FFmpegMediaPlayer mp, int what, int extra) {
//                progressBar.setVisibility(View.INVISIBLE);
//                playButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
//                playButton.bringToFront();
////                primeiro = true;
//                mp.reset();
//                return false;
//            }
//        });

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
                    player.stop();
                    carregando = false;
                    buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
//                if (!carregando && tocando) {
//                    try {
//                        if (mp.isPlaying()) {
////                            mp.pause();
////                            mp.stop();
//                            mp.reset();
//                            tocando = false;
//                            editor.putBoolean("tocando", false);
//                            editor.apply();
////                            playButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
//                        }
////                        if (!primeiro) {
////                            mp.reset();
////                        }
////                        mp.setDataSource(mms_url);
////                        progressBar.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
////                        progressBar.setVisibility(View.VISIBLE);
////                        progressBar.bringToFront();
////                        mp.prepareAsync();
////                        reloadButton = true;
////                        botaoPlay = false;
////                        if (primeiro) {
////                            primeiro = !primeiro;
////                        }
//                        //mp.pause();
//                    } catch (IllegalArgumentException e) {
//                        e.printStackTrace();
//                    } catch (SecurityException e) {
//                        e.printStackTrace();
//                    } catch (IllegalStateException e) {
//                        e.printStackTrace();
//                    }
//                }
                }
            });

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initMediaPlayer();
//                PlaybackStateCompat state = new PlaybackStateCompat.Builder()
//                        .setActions(PlaybackState.ACTION_PLAY)
//                        .setState(PlaybackState.STATE_STOPPED, PlaybackState.PLAYBACK_POSITION_UNKNOWN, SystemClock.elapsedRealtime())
//                        .build();
//                mMediaSession.setPlaybackState(state);
//                if (!carregando && !tocando) {
////                    if (primeiro) {
////                        primeiro = !primeiro;
////                        playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
//                        try {
//                            mp.setDataSource(mms_url);
//                            progressBar.setVisibility(View.VISIBLE);
//                            progressBar.bringToFront();
////                            botaoPlay = !botaoPlay;
//                            mp.prepareAsync();
//                            carregando = true;
//                            editor.putBoolean("carregando", true);
//                            editor.apply();
//                        } catch (IllegalArgumentException e) {
//                            e.printStackTrace();
//                        } catch (SecurityException e) {
//                            e.printStackTrace();
//                        } catch (IllegalStateException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
////                    }
////                    else {
////                        mp.start();
////                        if (!botaoPlay || reloadButton) {
////                            playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
////                            reloadButton = false;
////                            mp.start();
////                        } else {
//////                            playButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
////                            //mp.stop();
////                            //mp.reset();
////                            mp.pause();
////                        }
////                        botaoPlay = !botaoPlay;
////                    }
//                }
                }
            });
        }

        return view;
    }

    private void initMediaPlayer() {
//        Uri uri = Uri.parse(mms_url);
//        MediaSource mediaSource = buildMediaSource(uri);
//        player.prepare(mediaSource, true, false);
        Log.d("DEV/State", ""+player.getPlaybackState());
//        player.setPlayWhenReady(true);
        if(!carregando) {
            carregando = true;
            progressBar.setVisibility(View.VISIBLE);
//            if (!player.getPlayWhenReady()) {
            player.setPlayWhenReady(true);
//            } else {
            player.retry();
            buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
        }
//        }
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
        player.stop();
        player.release();
        getActivity().unregisterReceiver(receiver);
    }

    private void setupMediaPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(getActivity());
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == PlaybackState.STATE_PLAYING) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
//                if(playbackState == PlaybackState.STATE_STOPPED && !carregando) {
//                    player.release();
//                }
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
//        Notification.MediaStyle style = new Notification.MediaStyle();
//
//        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
//        intent.setAction(ACTION_STOP);
//        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
//        Notification.Builder builder = new Notification.Builder(this)
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setContentTitle("Media Title")
//                .setContentText("Media Artist")
//                .setDeleteIntent(pendingIntent)
//                .setStyle(style);

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

        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//        this.notificationManager = new PlayerNotificationManager(
//                getActivity(),
//                CHANNEL_1_ID,
//                1,
//                new DescriptionAdapter(getActivity()));
//        this.notificationManager.setPlayer(player);
        notificationManager.notify(1, notification);
//        notificationManager.notify( 1, notification);
    }

//    public void sendOnChannel2() {
//        String title = "Radio Facom";
//        String message = "103,9 FM";
//
//        Bitmap artwork = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
//
//        Intent intent = new Intent(getActivity(), RadioFragment.class);
//        intent.setAction(ACTION_STOP);
//        PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 1, intent, 0);
//
//        Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_2_ID)
//                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setLargeIcon(artwork)
//                .addAction(R.drawable.ic_stop_notification, "stop", pendingIntent)
//                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
//                        .setShowActionsInCompactView(0)
//                        .setMediaSession(mMediaSession.getSessionToken()))
//                .setPriority(NotificationCompat.PRIORITY_LOW)
//                .build();
//
//        notificationManager.notify(2, notification);
//    }

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
                player.stop();
                buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
            }
            else if(ACTION_PLAY.equals(action)) {
                Log.d("DEV/RadioFragment", "PLAY");
                if(player.getPlaybackState() != PlaybackState.STATE_PLAYING) {
                    progressBar.setVisibility(View.VISIBLE);
                    player.retry();
                    buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
                }
            }
        }
    };

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
