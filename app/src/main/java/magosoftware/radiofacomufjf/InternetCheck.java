package magosoftware.radiofacomufjf;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

class InternetCheck extends AsyncTask<Void,Void,Boolean[]> {

    private Consumer mConsumer;
    public  interface Consumer { void accept(Boolean[] internet); }
    private String url;

    public InternetCheck(String url, Consumer consumer) { this.url = url; mConsumer = consumer; execute(); }

    @Override protected Boolean[] doInBackground(Void... voids) {
        Boolean[] array = {false, false};
        try {
            Log.d("DEV/URL", url);
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
            sock.close();
            array[0] = true;
//            Socket sock2 = new Socket();
//            HttpGet requestForTest = new HttpGet(url);
            URL url_teste = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url_teste.openConnection();
            int code = connection.getResponseCode();
            if(code == 200) {
                // reachable
                array[1] = true;
            } else {
                array[1] = false;
            }
//            new DefaultHttpClient().execute(requestForTest);
//            sock2.connect(new InetSocketAddress(url, 53), 1500);
//            sock2.close();
            return array;
        } catch (IOException e) { return array; } }

    @Override protected void onPostExecute(Boolean[] internet) { mConsumer.accept(internet); }
}
