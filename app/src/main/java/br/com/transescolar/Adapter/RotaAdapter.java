package br.com.transescolar.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.transescolar.Activies.EditRotaActivity;
import br.com.transescolar.Activies.InfRotaActivity;
import br.com.transescolar.model.Rota;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;

import static br.com.transescolar.API.URL.URL_DELETA_ROTA;
import static br.com.transescolar.Adapter.RotaInfAdapter.MyViewHolder.swipeLayout;

public class RotaAdapter extends RecyclerSwipeAdapter<RotaAdapter.MyViewHolder> {

    private List<Rota> contacts;
    private Context context;
    String getId;
    SessionManager sessionManager;

    public RotaAdapter(List<Rota> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @Override
    public RotaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.linha_rotas_list, parent, false);
        sessionManager = new SessionManager(context);
        return new RotaAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RotaAdapter.MyViewHolder holder, final int position) {
        final Rota rota = this.contacts.get(position);
        getId = String.valueOf(rota.getId());
        holder.txtNomeRota.setText(contacts.get(position).getNm_rota());
        holder.txtDias.setText(contacts.get(position).getDias());
        holder.txtHora.setText(contacts.get(position).getHora());

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //from the right
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wraper));

        holder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(RotaAdapter.this.context, InfRotaActivity.class);
                it.putExtra("rota", rota);
                RotaAdapter.this.context.startActivity(it);
            }
        });

        holder.Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(RotaAdapter.this.context, EditRotaActivity.class);
                it.putExtra("rota", rota);
                RotaAdapter.this.context.startActivity(it);
                }
            }
        );

        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mView = inflater.inflate(R.layout.dialog_text, null);
                final TextView nomeE = mView.findViewById(R.id.nomeD);
                nomeE.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                Button mSim = mView.findViewById(R.id.btnSim);
                Button mNao = mView.findViewById(R.id.btnNao);

                alertDialog.setView(mView);
                final AlertDialog dialog = alertDialog.create();

                nomeE.setText("Você deseja realmente deletar?");
                mSim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETA_ROTA,
                                new com.android.volley.Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            //boolean success = jsonObject.getBoolean("success");
                                            String success = jsonObject.getString("success");
                                            //JSONArray success = jsonObject.getJSONArray("success");


                                            if (success.equals("OK")){
                                                mItemManger.removeShownLayouts(holder.swipeLayout);
                                                contacts.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, contacts.size());
                                                mItemManger.closeAllItems();
                                                dialog.dismiss();

                                            }else {
//                                                Toast toast= Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG);
//                                                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
//                                                toast.show();

                                                final Snackbar snackbar = showSnackbar(swipeLayout, Snackbar.LENGTH_LONG);
                                                snackbar.show();
                                                View view = snackbar.getView();
                                                TextView tv = (TextView) view.findViewById(R.id.textSnack);
                                                tv.setText(jsonObject.getString("message"));
                                                dialog.dismiss();

                                            }

                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                            Log.e("JSON", "Error parsing JSON", e1);
                                            Log.e("Chamada", response);
                                            dialog.dismiss();

                                        }
                                    }
                                },
                                new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        dialog.dismiss();

                                    }
                                }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("idRota", getId);
                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        requestQueue.add(stringRequest);
                        dialog.dismiss();
                    }
                });
                mNao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        mItemManger.bindView(holder.itemView, position);

    }

    private Snackbar showSnackbar(SwipeLayout coordinatorLayout, int duration) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "", duration);
        // 15 is margin from all the sides for snackbar
        int marginFromSides = 15;

        float height = 100;

        //inflate view
        LayoutInflater inflater = (LayoutInflater)context.getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View snackView = inflater.inflate(R.layout.snackbar_layout, null);

        // White background
        snackbar.getView().setBackgroundResource(R.color.ColorBGThema);
        snackbar.setActionTextColor(Color.BLACK);
        // for rounded edges
//        snackbar.getView().setBackground(getResources().getDrawable(R.drawable.shape_oval));

        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        FrameLayout.LayoutParams parentParams = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
        parentParams.setMargins(marginFromSides, 0, marginFromSides, marginFromSides);
        parentParams.height = (int) height;
        parentParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
        snackBarView.setLayoutParams(parentParams);

        snackBarView.addView(snackView, 0);
        return snackbar;
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private final Context context;
        TextView txtHora, txtNomeRota, txtDias, Delete, Edit;
        static SwipeLayout swipeLayout;


        public MyViewHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();
            txtNomeRota = itemView.findViewById(R.id.txtNomeRota);
            txtDias = itemView.findViewById(R.id.txtDias);
            txtHora = itemView.findViewById(R.id.txtHora);
            Delete = itemView.findViewById(R.id.Delete);
            Edit = itemView.findViewById(R.id.Edit);
            swipeLayout = itemView.findViewById(R.id.swipe);

        }
    }

}
