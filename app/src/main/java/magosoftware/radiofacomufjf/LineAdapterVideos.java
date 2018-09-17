//package magosoftware.radiofacomufjf;
//
//import android.graphics.Color;
//import android.graphics.drawable.Drawable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class LineAdapterVideos extends RecyclerView.Adapter<LineHolderVideos>{
//
//    private LineAdapterVideos.OnItemClicked onClick;
//
//    List<Video> mVideo;
//
//    private Drawable backgroundEntrar;
//    private Drawable backgroundAguardando;
//    private Drawable backgroundMembro;
//
//    public LineAdapterVideos(Drawable backgroundEntrar, Drawable backgroundAguardando, Drawable backgroundMembro) {
//        mVideo = new ArrayList<>();
//        this.backgroundEntrar = backgroundEntrar;
//        this.backgroundAguardando = backgroundAguardando;
//        this.backgroundMembro = backgroundMembro;
//    }
//
//    //make interface like this
//    public interface OnItemClicked {
//        void onItemClick(int position, String nome, String situacao, String node);
//    }
//
//    @Override
//    public LineHolderVideos onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new LineHolderVideos(LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.Video_card, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(LineHolderVideos holder, final int position) {
//        holder.nomeVideo.setText(mVideo.get(position).getNome());
//        holder.imagemVideo.setImageDrawable(mVideo.get(position).getImagemVideo());
//        if(mVideo.get(position).getSituacao().equals("fora")) {
//            holder.opcao.setText("ENTRAR");
//            holder.opcao.setTextColor(Color.parseColor("#03A9F4"));
//            holder.opcao.setBackgroundDrawable(backgroundEntrar);
//        }
//        else if(mVideo.get(position).getSituacao().equals("aguardando")) {
//            holder.opcao.setText("AGUARDANDO");
//            holder.opcao.setTextColor(Color.parseColor("#FFD600"));
//            holder.opcao.setBackgroundDrawable(backgroundAguardando);
//        }
//        else if(mVideo.get(position).getSituacao().equals("membro")) {
//            holder.opcao.setText("MEMBRO");
//            holder.opcao.setTextColor(Color.parseColor("#00E676"));
//            holder.opcao.setBackgroundDrawable(backgroundMembro);
//        }
//        holder.cardVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClick.onItemClick(position, mVideo.get(position).getNome(), mVideo.get(position).getSituacao(), mVideo.get(position).getNode());
//            }
//        });
//    }
//
//    public void add(Video model) {
//        mVideo.add(model);
//    }
//
//    public void remove(Video model) {
//        mVideo.remove(model);
//    }
//
//    public void add(List<Video> models) {
//        mVideo.addAll(models);
//    }
//
//    public void removeAll() {
//        for (int i = mVideo.size() - 1; i >= 0; i--) {
//            final Video model = mVideo.get(i);
//            mVideo.remove(model);
//        }
//    }
//
//    public void replaceAll(List<Video> models) {
//        for (int i = mVideo.size() - 1; i >= 0; i--) {
//            final Video model = mVideo.get(i);
//            if (!models.contains(model)) {
//                mVideo.remove(model);
//            }
//        }
//        mVideo.addAll(models);
//    }
//
//    @Override
//    public int getItemCount() {
//        return mVideo.size();
//    }
//
//    public void setOnClick(LineAdapterVideos.OnItemClicked onClick)
//    {
//        this.onClick=onClick;
//    }
//}
