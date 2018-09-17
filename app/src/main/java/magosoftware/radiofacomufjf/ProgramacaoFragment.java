package magosoftware.radiofacomufjf;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexandre on 20/10/2017.
 */

public class ProgramacaoFragment extends Fragment implements AsyncResponseString, View.OnClickListener {

    final String url = "http://www.ufjf.br/radio/programacao/";
    String html = "";
    String informacao = "";

    List<String> programacao;
    HashMap<String, List<String>> programacaoPorDia;
    String[] diasSemana = {"Segunda","Terça","Quarta","Quinta","Sexta"};

    ViewPager viewPager;
    PagerAdapterProgramacao pagerAdapter;
    boolean inicio = true;
    private ExpandableListView expandableListView;
    int diaSelecionado;
    ExpandableListAdapter adapter;
    LinkedHashMap<String, String> lstItensGrupo;
    List<String> lstGrupo;
    String[] horarios = {"8:00","9:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00",
            "17:00","18:00","19:00","20:00","21:00"};
    TextView diaSemana;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.programacao_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);
        Calendar c = Calendar.getInstance();
        diaSelecionado = c.get(Calendar.DAY_OF_WEEK)-2;
        if(diaSelecionado<0) {
            diaSelecionado = 0;
        }
        if(diaSelecionado>4) {
            diaSelecionado = 4;
        }
        diaSemana = getView().findViewById(R.id.dia_semana);
        diaSemana.setText(getDiaSemana(diaSelecionado));

        getView().findViewById(R.id.seta_esquerda).setOnClickListener(this);
        getView().findViewById(R.id.seta_direita).setOnClickListener(this);
        progressBar = getView().findViewById(R.id.progressBar);

        lstGrupo = new ArrayList<>();

        viewPager = getView().findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(5);
        pagerAdapter = new PagerAdapterProgramacao(getActivity());

        for(int i = 0; i<horarios.length; i++) {
            lstGrupo.add(horarios[i]);
        }

        RequestProgramacao requestProgramacao = new RequestProgramacao();
        requestProgramacao.delegate = this;
        try {
            requestProgramacao.execute(url);
        } catch (Exception e) {

        }
        programacaoPorDia = new HashMap<>();
//        filtraInformacao();
        FiltraInformacao filtraInformacao = new FiltraInformacao();
        filtraInformacao.execute();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.seta_esquerda) {
            diaSelecionado = diaSelecionado-1;
            viewPager.setCurrentItem(diaSelecionado);
        }
        if(id == R.id.seta_direita) {
            diaSelecionado = diaSelecionado+1;
            viewPager.setCurrentItem(diaSelecionado);
        }
    }

    private void filtraInformacao() {
//        programacao = new ArrayList<>();
////        String[] linhasTabela = html.split("<tr valign=\"TOP\">");
//        html = html.replace("<p>&nbsp;</p>", "");
//        for(int i = 8; i<22; i++) {
//            html = html.replace(i+":00", "");
//        }
//        String[] linhasTabela = html.split("</td>");
//        for(int i = 7; i<linhasTabela.length-1; i++) {
////            linhasTabela[i].substring(linhasTabela[i].indexOf(">") + 1, linhasTabela[i].indexOf("<"));
//            String pattern1 = ">";
//            String pattern2 = "<";
//
//            Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
//            Matcher m = p.matcher(linhasTabela[i]);
//            int j = 0;
//            String informacao = "";
//            while (m.find()) {
//                j++;
////                Log.d("Informacion","linha "+j+": "+m.group(1));
//                informacao = informacao + m.group(1);
//
//            }
//            if(!informacao.isEmpty() && !informacao.equals(" ") && !informacao.equals("") && informacao.length()>3) {
//                Log.d("Informacion","Informação: "+informacao);
//                if(informacao.startsWith(" ")) {
//                    informacao = informacao.substring(1);
//                }
//                programacao.add(informacao);
//            }
//        }
//        programacaoPorDia = new HashMap<>();
//        for(int i = 0; i<diasSemana.length;i++) {
//            List<String> programacaoPorLinha = new ArrayList<>();
//            programacaoPorDia.put(diasSemana[i], programacaoPorLinha);
//        }
//        int j = 0;
//        for(String item : programacao) {
//            programacaoPorDia.get(diasSemana[j]).add(item);
//            j++;
//            if(j>4) {
//                j=0;
//            }
//        }
//        Log.d("Informacion","Final: "+programacaoPorDia);
        setupViewPager();
    }

    private class FiltraInformacao extends AsyncTask<String, Void, HashMap<String, List<String>>>{
        @Override
        protected void onPreExecute(){
        }

        @Override
        protected HashMap<String, List<String>> doInBackground(String... params) {
                List<String> programacao = new ArrayList<>();
//        String[] linhasTabela = html.split("<tr valign=\"TOP\">");

                html = html.replace("<p>&nbsp;</p>", "");
                for(int i = 8; i<22; i++) {
                    html = html.replace(i+":00", "");
                }
                String[] linhasTabela = html.split("</td>");
                for(int i = 7; i<linhasTabela.length-1; i++) {
//            linhasTabela[i].substring(linhasTabela[i].indexOf(">") + 1, linhasTabela[i].indexOf("<"));
                    String pattern1 = ">";
                    String pattern2 = "<";

                    Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
                    Matcher m = p.matcher(linhasTabela[i]);
                    int j = 0;
                    String informacao = "";
                    while (m.find()) {
                        j++;
//                Log.d("Informacion","linha "+j+": "+m.group(1));
                        informacao = informacao + m.group(1);

                    }
                    if(!informacao.isEmpty() && !informacao.equals(" ") && !informacao.equals("") && informacao.length()>3) {
                        Log.d("Informacion","Informação: "+informacao);
                        if(informacao.startsWith(" ")) {
                            informacao = informacao.substring(1);
                        }
                        programacao.add(informacao);
                    }
                }
                programacaoPorDia = new HashMap<>();
                for(int i = 0; i<diasSemana.length;i++) {
                    List<String> programacaoPorLinha = new ArrayList<>();
                    programacaoPorDia.put(diasSemana[i], programacaoPorLinha);
                }
                int j = 0;
                for(String item : programacao) {
                    programacaoPorDia.get(diasSemana[j]).add(item);
                    j++;
                    if(j>4) {
                        j=0;
                    }
                }
                Log.d("Informacion","Final: "+programacaoPorDia);
                return programacaoPorDia;
            }

        @Override
        protected void onPostExecute(HashMap<String, List<String>> resultado){
            setupViewPager();
        }
    }

    public void setupViewPager() {
        Log.d("DEV/CALENDARIOFRAG", "setupViewPager");
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(diaSelecionado);
        viewPager.addOnPageChangeListener(new OnPageChangeListenerSend(this) {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(inicio) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("DEV/TAREFASCONCENT", "INICIOU");
                    inicio = false;
                    setupExpandableView(R.id.horarios);
                }
            }

            @Override
            public void onPageSelected(int position) {
                diaSelecionado = position;
                diaSemana.setText(getDiaSemana(diaSelecionado));
                setupExpandableView(R.id.horarios);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    private void setupExpandableView(int expandableLayout) {
        if(diaSelecionado == 0) {
            getView().findViewById(R.id.seta_esquerda).setVisibility(View.GONE);
        } else if (diaSelecionado == 4) {
            getView().findViewById(R.id.seta_direita).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.seta_esquerda).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.seta_direita).setVisibility(View.VISIBLE);
        }
        lstItensGrupo = new LinkedHashMap<>();
        Log.d("Informacion","Tamanho listaView: "+pagerAdapter.listaViews.size());
        Log.d("Informacion","Final "+diasSemana[diaSelecionado]+": "+programacaoPorDia.get(diasSemana[diaSelecionado]));
        for(int i = 0; i< horarios.length; i++) {
            lstItensGrupo.put(horarios[i], programacaoPorDia.get(diasSemana[diaSelecionado]).get(i));
        }
        Log.d("Informacion", "lstGrupo: "+lstGrupo.size());
        Log.d("Informacion", "lstItens: "+lstItensGrupo.size());
        expandableListView = pagerAdapter.listaViews.get(diaSelecionado).findViewById(expandableLayout);
        adapter = new ExpandableListAdapter(getActivity(), lstGrupo, lstItensGrupo);
        // define o apadtador do ExpandableListView
        expandableListView.setAdapter(adapter);
        for(int i = 0; i<horarios.length; i++) {
            expandableListView.expandGroup(i);
        }
//        adapter.notifyDataSetChanged();
        Log.d("DEV/TAREFASCONC", "Passou do trem");
    }

    public String getDiaSemana(int diaNumero) {
        String diaSemana = "";
        if (diaNumero == 0) {
            diaSemana = "SEGUNDA";
        } else if (diaNumero == 1) {
            diaSemana = "TERÇA";
        } else if (diaNumero == 2) {
            diaSemana = "QUARTA";
        } else if (diaNumero == 3) {
            diaSemana = "QUINTA";
        } else if (diaNumero == 4) {
            diaSemana = "SEXTA";
        }
        return diaSemana;
    }

    @Override
    public void processFinish(String html) {
        this.html = html;
    }

    static class RequestProgramacao extends AsyncTask {

        String server_response;
        List<String> titulos;
        List<String> id;
        String html = "";
        public AsyncResponseString delegate = null;

        @Override
        protected Object doInBackground(Object... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet((String) params[0]);

            try {
                HttpResponse response = httpclient.execute(httpget);
                if (response.getStatusLine().getStatusCode() == 200) {
                    InputStream in = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder str = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        str.append(line);
                    }
                    in.close();
                    html = str.toString();
                    Log.i("Server response", html);
                    delegate.processFinish(html);
                }
                else {
                    Log.i("Server response", "Failed to get server response");
                }
//                if (response.getStatusLine().getStatusCode() == 200) {
//                    server_response = EntityUtils.toString(response.getEntity());
//                    Log.i("Server response", server_response);
//                } else {
//                    Log.i("Server response", "Failed to get server response");
//                }
            } catch (IOException e) {

            }
            return null;
        }
    }
}
