package br.com.transescolar.Adapter;

import android.content.Context;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import br.com.transescolar.model.Kids;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
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
    String getId, getIdKids;
    RequestOptions options;
    SessionManager sessionManager;

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

        HashMap<String, String> rotas = sessionManager.getIdRoto();
        getId = rotas.get(sessionManager.IDROTA);

        return new AddKidsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AddKidsAdapter.MyViewHolder holder, final int position) {
        final Kids kids = this.contacts.get(position);
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
                   getIdKids = String.valueOf(contacts.get(position).getIdKids());
                   StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD_KIDS_ROTA,
                           new com.android.volley.Response.Listener<String>() {
                               @Override
                               public void onResponse(String response) {
                                   try {
                                       JSONObject jsonObject = new JSONObject(response);
                                       //boolean success = jsonObject.getBoolean("success");
                                       String success = jsonObject.getString("success");
                                       //JSONArray success = jsonObject.getJSONArray("success");
                                       if (success.equals("OK")){
                                           mItemManger.removeShownLayouts(swipeLayout);
                                           contacts.remove(position);
                                           notifyItemRemoved(position);
                                           notifyItemRangeChanged(position, contacts.size());
                                           mItemManger.closeAllItems();

                                           Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                       }else {

                                           Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                                       }

                                   } catch (JSONException e1) {
                                       e1.printStackTrace();
                                       Log.e("JSON", "Error parsing JSON", e1);
                                       Log.e("Chamada", response);
                                   }
                               }
                           },
                           new com.android.volley.Response.ErrorListener() {
                               @Override
                               public void onErrorResponse(VolleyError error) {
                               }
                           }){
                       @Override
                       protected Map<String, String> getParams() throws AuthFailureError {
                           Map<String, String> params = new HashMap<>();
                           params.put("idRota", getId);
                           params.put("idKids", getIdKids);
                           return params;
                       }
                   };

                   RequestQueue requestQueue = Volley.newRequestQueue(context);
                   requestQueue.add(stringRequest);
               }
           }
        );

        mItemManger.bindView(holder.itemView, position);

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
