package magosoftware.radiofacomufjf;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapterProgramacao extends PagerAdapter {

    private Context mContext;
    public List<ViewGroup> listaViews;

    public PagerAdapterProgramacao(Context context) {
        mContext = context;
        listaViews = new ArrayList<>();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout;
        layout = (ViewGroup) inflater.inflate(R.layout.dia_programacao, collection, false);
//        if(position == 0) {
//
//        }
//        else if(position == 1){
//            layout = (ViewGroup) inflater.inflate(R.layout.tarefas_page, collection, false);
//        }
//        else {
//            layout = (ViewGroup) inflater.inflate(R.layout.tarefas_feitas, collection, false);
//        }
        collection.addView(layout);
        listaViews.add(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
        listaViews.remove(position);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "SEGUNDA";
            case 1:
                return "TERÇA";
            case 2:
                return "QUARTA";
            case 3:
                return "QUINTA";
            case 4:
                return "SEXTA";
            default:
                return null;
        }
    }
}