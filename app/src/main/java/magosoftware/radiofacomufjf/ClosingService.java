package magosoftware.radiofacomufjf;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClosingService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        // Handle application closing
//        fireClosingNotification();
        closing();

        // Destroy the service
        stopSelf();
    }

    private void closing() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("espec").addValueEventListener(new ValueEventListenerSend(mDatabase) {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int quantidade = dataSnapshot.getValue(Integer.class);
                DatabaseReference reference = (DatabaseReference) variavel;
                reference.setValue(quantidade-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
