package magosoftware.radiofacomufjf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.exoplayer2.SimpleExoPlayer;

abstract class BroadCastReceiverParam extends BroadcastReceiver {

    SimpleExoPlayer player;

    public BroadCastReceiverParam(SimpleExoPlayer player) {
        this.player = player;
    }

}
