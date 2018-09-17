package magosoftware.radiofacomufjf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class VideoFullscreen extends AppCompatActivity implements YouTubePlayer.OnFullscreenListener {

    private VideoFragment videoFragment;
    private String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_fulscreen);
        Intent intent = getIntent();
        videoId = intent.getStringExtra("video");
        videoFragment =
                (VideoFragment) getSupportFragmentManager().findFragmentById(R.id.video_fragment_container);
        videoFragment.retornaString(videoId);
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
    }

    public static final class VideoFragment extends YouTubePlayerSupportFragment
            implements YouTubePlayer.OnInitializedListener, RespostaClass {

        private YouTubePlayer player;
        private String videoId;

        public static VideoList.VideoFragment newInstance() {
            return new VideoList.VideoFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            initialize(Config.YOUTUBE_API_KEY, this);
        }

        @Override
        public void retornaString(String output) {
            videoId = output;
            setVideoId(videoId);
        }

        @Override
        public void onDestroy() {
            if (player != null) {
                player.release();
            }
            super.onDestroy();
        }

        public void setVideoId(String videoId) {
            if (videoId != null && !videoId.equals(this.videoId)) {
                this.videoId = videoId;
                if (player != null) {
                    player.cueVideo(videoId);
                }
            }
        }

        public void pause() {
            if (player != null) {
                player.pause();
            }
        }

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean restored) {
            this.player = player;
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
            player.setFullscreen(true);
            player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {

                @Override
                public void onFullscreen(boolean fullscreen) {
                    Log.d("FullScreen", "Entrou");
                    if(!fullscreen) {
                        getActivity().finish();
                    }
                }
            });
            if (!restored && videoId != null) {
                player.cueVideo(videoId);
            }
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
            this.player = null;
        }

    }
}
