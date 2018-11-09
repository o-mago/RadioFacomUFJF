package magosoftware.radiofacomufjf;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

public class DescriptionAdapter implements
        PlayerNotificationManager.MediaDescriptionAdapter {

    private Context context;

    public DescriptionAdapter(Context context) {
        this.context = context;
    }

    @Override
    public String getCurrentContentTitle(Player player) {
//        int window = player.getCurrentWindowIndex();
//        return getTitle(window);
        return "TesteContentTitle";
    }

    @Nullable
    @Override
    public String getCurrentContentText(Player player) {
        int window = player.getCurrentWindowIndex();
//        return getDescription(window);
        return "TesteContentText";
    }

    @Nullable
    @Override
    public Bitmap getCurrentLargeIcon(Player player,
                                      PlayerNotificationManager.BitmapCallback callback) {
//        int window = player.getCurrentWindowIndex();
//        Bitmap largeIcon = getLargeIcon(window);
//        if (largeIcon == null && getLargeIconUri(window) != null) {
//            // load bitmap async
//            loadBitmap(getLargeIconUri(window), callback);
//            return getPlaceholderBitmap();
//        }
//        return largeIcon;
        return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round);
    }

    @Nullable
    @Override
    public PendingIntent createCurrentContentIntent(Player player) {
//        int window = player.getCurrentWindowIndex();
//        return createPendingIntent(window);
        return PendingIntent.getActivity(context,0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
