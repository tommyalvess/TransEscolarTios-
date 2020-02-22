package br.com.transescolar.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.swipe.SwipeLayout;

import java.util.List;

import br.com.transescolar.Activies.InfKidsActivity;
import br.com.transescolar.model.Kids;
import br.com.transescolar.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class KidsAdpter extends RecyclerView.Adapter<KidsAdpter.MyViewHolder> {

    RequestOptions options;
    private final Context context;
    private final List<Kids> nData;

    public KidsAdpter(List<Kids> filhos, Context context) {
        this.context = context;
        this.nData = filhos;
        options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.kids)
                .error(R.drawable.kids);
    }


    @NonNull
    @Override
    public KidsAdpter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.linha_passageiro_list,parent,false);

        return new KidsAdpter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final KidsAdpter.MyViewHolder holder, final int position) {
        final Kids kids = this.nData.get(position);
        holder.nome.setText(nData.get(position).getNome());
        holder.escola.setText(nData.get(position).getNm_escola());
        holder.periodo.setText(nData.get(position).getPeriodo());
        Glide.with(context).load(nData.get(position).getImg()).apply(options).into(holder.img);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(KidsAdpter.this.context, InfKidsActivity.class);
                it.putExtra("crianca", kids);
                KidsAdpter.this.context.startActivity(it);
            }
        });

    }


    @Override
    public int getItemCount() {
        return nData.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome, escola, periodo, Delete;
        CircleImageView img;
        public SwipeLayout swipeLayout;
        CardView cardView;

        //private final Context context;

        public MyViewHolder(View itemView){
            super(itemView);

            //context = itemView.getContext();
            cardView = (CardView) itemView;
            nome = itemView.findViewById(R.id.txtNomeK);
            escola = itemView.findViewById(R.id.txtEscola);
            periodo = itemView.findViewById(R.id.txtPeriodo);
            img = itemView.findViewById(R.id.imgPerfilK);

        }

    }

}
