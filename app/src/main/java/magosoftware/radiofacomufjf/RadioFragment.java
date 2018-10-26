package magosoftware.radiofacomufjf;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
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

//import wseemann.media.FFmpegMediaPlayer;

/**
 * Created by Alexandre on 06/09/2017.
 */

public class RadioFragment extends Fragment {

//    String mms_url = "mms://radio.correios.com.br/radio";
//    String mms_url = "rtsp://radio.correios.com.br/radio";
    String mms_url = "http://bbcwssc.ic.llnwd.net/stream/bbcwssc_mp1_ws-einws";
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
    private SimpleExoPlayer player;
    private BandwidthMeter bandwidthMeter;
    private ExtractorsFactory extractorsFactory;
    private TrackSelection.Factory trackSelectionFactory;
    private TrackSelector trackSelector;
    private DefaultBandwidthMeter defaultBandwidthMeter;
    private DataSource.Factory dataSourceFactory;
    private MediaSource mediaSource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.radio_fragment, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        playButton = view.findViewById(R.id.playButton);
        facebookButton = view.findViewById(R.id.facebook);
        reload = view.findViewById(R.id.reload);
        progressBar.setVisibility(View.INVISIBLE);

        sharedPreferences = getActivity().getSharedPreferences("main", 0);
        editor = sharedPreferences.edit();

//        mp = (FFmpegMediaPlayerSerial) getArguments().getSerializable("mediaPlayer");

        tocando = sharedPreferences.getBoolean("tocando", false);
        carregando = sharedPreferences.getBoolean("carregando", false);

        view.findViewById(R.id.scroll).setSelected(true);

        initMediaPlayer();

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
                player.setPlayWhenReady(false);
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
                player.setPlayWhenReady(true);
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

        return view;
    }

    private void initMediaPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getActivity()),
                new DefaultTrackSelector(), new DefaultLoadControl());
        Uri uri = Uri.parse(mms_url);
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
    }


}
