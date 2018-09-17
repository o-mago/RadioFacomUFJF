package magosoftware.radiofacomufjf;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Alexandre on 24/08/2017.
 */

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splash_screen);
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                vaiParaMain();
            }
        }, 1000);
    }

    private void vaiParaMain() {
        Intent intent = new Intent(SplashScreenActivity.this,
                RadioMain.class);
        startActivity(intent);
        finish();
    }

}