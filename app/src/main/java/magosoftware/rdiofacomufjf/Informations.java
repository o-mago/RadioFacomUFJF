package magosoftware.rdiofacomufjf;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Alexandre on 27/08/2017.
 */

public class Informations extends Fragment implements View.OnClickListener {


    ImageButton wapp;
    Button numero;
    ImageButton site;
    Button site_texto;
    ImageButton sound;
    Button sound_texto;
    ImageButton pet;
    String number = "+553291144488";
    Intent sendWhatsApp;
    Intent site_intent;
    Intent sound_intent;
    Intent pet_intent;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.info_layout, container, false);

        wapp = view.findViewById(R.id.whatsapp_icon);
        numero = view.findViewById(R.id.numero_telefone);
        site = view.findViewById(R.id.site);
        site_texto = view.findViewById(R.id.site_texto);
        sound = view.findViewById(R.id.sound);
        sound_texto = view.findViewById(R.id.sound_text);
        pet = view.findViewById(R.id.pet);
        wapp.setOnClickListener(this);
        numero.setOnClickListener(this);
        site.setOnClickListener(this);
        site_texto.setOnClickListener(this);
        sound.setOnClickListener(this);
        sound_texto.setOnClickListener(this);
        pet.setOnClickListener(this);

        return view;

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.whatsapp_icon:
                Log.d("TESTE","FOI");
                sendWhatsApp = new Intent(Intent.ACTION_SENDTO);
                sendWhatsApp.setData(Uri.parse("smsto:" + number));
                sendWhatsApp.setPackage("com.whatsapp");
                startActivity(sendWhatsApp);
                break;
            case R.id.numero_telefone:
                Log.d("TESTE","FOI");
                sendWhatsApp = new Intent(Intent.ACTION_SENDTO);
                sendWhatsApp.setData(Uri.parse("smsto:" + number));
                sendWhatsApp.setPackage("com.whatsapp");
                startActivity(sendWhatsApp);
                break;
            case R.id.site:
                site_intent = new Intent();
                site_intent.setAction(Intent.ACTION_VIEW);
                site_intent.addCategory(Intent.CATEGORY_BROWSABLE);
                site_intent.setData(Uri.parse("http://www.ufjf.br/radio/"));
                startActivity(site_intent);
                break;
            case R.id.site_texto:
                site_intent = new Intent();
                site_intent.setAction(Intent.ACTION_VIEW);
                site_intent.addCategory(Intent.CATEGORY_BROWSABLE);
                site_intent.setData(Uri.parse("http://www.ufjf.br/radio/"));
                startActivity(site_intent);
                break;
            case R.id.sound:
                sound_intent = new Intent();
                sound_intent.setAction(Intent.ACTION_VIEW);
                sound_intent.addCategory(Intent.CATEGORY_BROWSABLE);
                sound_intent.setData(Uri.parse("https://www.soundcloud.com/radiofacom/"));
                startActivity(sound_intent);
                break;
            case R.id.sound_text:
                sound_intent = new Intent();
                sound_intent.setAction(Intent.ACTION_VIEW);
                sound_intent.addCategory(Intent.CATEGORY_BROWSABLE);
                sound_intent.setData(Uri.parse("https://www.soundcloud.com/radiofacom/"));
                startActivity(sound_intent);
                break;
            case R.id.pet:
                pet_intent = new Intent();
                pet_intent.setAction(Intent.ACTION_VIEW);
                pet_intent.addCategory(Intent.CATEGORY_BROWSABLE);
                pet_intent.setData(Uri.parse("http://www.peteletrica.xyz"));
                startActivity(pet_intent);
                break;
        }
        return;
    }
}