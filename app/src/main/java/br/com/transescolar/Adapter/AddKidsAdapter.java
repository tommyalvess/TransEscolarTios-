package br.com.transescolar.Adapter;

import android.content.Context;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.swipe.SwipeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.transescolar.controler.RotaControler;
import br.com.transescolar.model.Kids;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import br.com.transescolar.model.Rota;
import br.com.transescolar.model.Tios;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.transescolar.API.URL.URL_ADD_KIDS_ROTA;
import static br.com.transescolar.Adapter.AddKidsAdapter.MyViewHolder.swipeLayout;

public class AddKidsAdapter extends RecyclerSwipeAdapter<AddKidsAdapter.MyViewHolder> {

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private final Context context;
        TextView txtHora, txtNomeRota, txtDias, Delete, ADD;
        static SwipeLayout swipeLayout;
        CircleImageView imgPass;

        public MyViewHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();
            txtNomeRota = itemView.findViewById(R.id.txtNome);
            txtDias = itemView.findViewById(R.id.txtEmbarque);
            txtHora = itemView.findViewById(R.id.txtStatus);
            ADD = itemView.findViewById(R.id.ADD);
            imgPass = itemView.findViewById(R.id.imgPerfilT);
            swipeLayout = itemView.findViewById(R.id.swipe);

        }
    }

    private List<Kids> contacts;
    private Context context;
    int getId;
    int getIdKids;
    RequestOptions options;
    SessionManager sessionManager;
    RotaControler rotaControler;
    Rota objRota;
    Kids objKids;


    public AddKidsAdapter(List<Kids> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
        options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.kids)
                .error(R.drawable.kids);
    }

    @Override
    public AddKidsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.linha_add_kids, parent, false);
        sessionManager = new SessionManager(context);

        rotaControler = new RotaControler();
        HashMap<String, String> rotas = sessionManager.getIdRoto();
        getId = Integer.parseInt(rotas.get(sessionManager.IDROTA));

        return new AddKidsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AddKidsAdapter.MyViewHolder holder, final int position) {
        objKids = this.contacts.get(position);
        getIdKids = contacts.get(position).getIdKids();
        holder.txtNomeRota.setText(contacts.get(position).getNome());
        holder.txtDias.setText(contacts.get(position).getNm_escola());
        holder.txtHora.setText(contacts.get(position).getPeriodo());
        Glide.with(context).load(contacts.get(position).getImg()).apply(options).into(holder.imgPass);

        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //from the left
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper1));

        //from the right
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wraper));

        swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        holder.ADD.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {

                   objKids = new Kids();
                   objRota = new Rota();

                   objRota.setId(getId);
                   objKids.setIdKids(getIdKids);

                   rotaControler.salvarKidsnaRota(objRota, objKids, context);
               }
           }
        );

        mItemManger.bindView(holder.itemView, position);

    }

    private void popularObj() {

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

}
