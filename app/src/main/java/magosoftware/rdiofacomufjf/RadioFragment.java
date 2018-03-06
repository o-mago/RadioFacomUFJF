package magosoftware.rdiofacomufjf;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.io.IOException;

import wseemann.media.FFmpegMediaPlayer;

/**
 * Created by Alexandre on 06/09/2017.
 */

public class RadioFragment extends Fragment {

    String mms_url = "mms://radio.correios.com.br/radio";
    //String mms_url = "mms://200.17.70.162:8080/Rádio Universitária";
    boolean botaoPlay = false;
    boolean primeiro = true;
    ProgressBar progressBar;
    ImageButton playButton;
    ImageButton facebookButton;
    ImageButton reload;
    boolean reloadButton = false;
    boolean carregando = false;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.radio_fragment, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        playButton = view.findViewById(R.id.playButton);
        facebookButton = view.findViewById(R.id.facebook);
        reload = view.findViewById(R.id.reload);
        progressBar.setVisibility(View.INVISIBLE);

        view.findViewById(R.id.scroll).setSelected(true);

        final FFmpegMediaPlayer mp = new FFmpegMediaPlayer();
        mp.setOnPreparedListener(new FFmpegMediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(FFmpegMediaPlayer mp) {
                if(!reloadButton) {
                    mp.start();
                }
                playButton.bringToFront();
                progressBar.setVisibility(View.INVISIBLE);
                reloadButton = false;
                carregando = false;
            }
        });
        mp.setOnErrorListener(new FFmpegMediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(FFmpegMediaPlayer mp, int what, int extra) {
                progressBar.setVisibility(View.INVISIBLE);
                playButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
                playButton.bringToFront();
                primeiro = true;
                mp.reset();
                return false;
            }
        });

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
                if (!carregando) {
                    try {
                        if (mp.isPlaying()) {
                            mp.pause();
                            playButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
                        }
                        if (!primeiro) {
                            mp.reset();
                        }
                        mp.setDataSource(mms_url);
                        progressBar.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.bringToFront();
                        mp.prepareAsync();
                        carregando = true;
                        reloadButton = true;
                        botaoPlay = false;
                        if (primeiro) {
                            primeiro = !primeiro;
                        }
                        //mp.pause();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!carregando) {
                    if (primeiro) {
                        primeiro = !primeiro;
                        playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
                        try {
                            mp.setDataSource(mms_url);
                            progressBar.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.bringToFront();
                            botaoPlay = !botaoPlay;
                            carregando = true;
                            mp.prepareAsync();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (!botaoPlay || reloadButton) {
                            playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
                            reloadButton = false;
                            mp.start();
                        } else {
                            playButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
                            //mp.stop();
                            //mp.reset();
                            mp.pause();
                        }
                        botaoPlay = !botaoPlay;
                    }
                }
            }
        });

        return view;
    }


}
