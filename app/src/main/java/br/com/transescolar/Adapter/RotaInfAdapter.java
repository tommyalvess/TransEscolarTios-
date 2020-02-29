package br.com.transescolar.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.gauravbhola.ripplepulsebackground.RipplePulseLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.transescolar.model.Kids;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.transescolar.API.URL.URL_DELETA_KID_ROTA;
import static br.com.transescolar.API.URL.URL_DESEMBARQUE;
import static br.com.transescolar.API.URL.URL_EMBARQUE;
import static br.com.transescolar.Adapter.RotaInfAdapter.MyViewHolder.swipeLayout;

public class RotaInfAdapter extends RecyclerSwipeAdapter<RotaInfAdapter.MyViewHolder> {

    private List<Kids> contacts;
    private Context context;
    RequestOptions options;
    SessionManager sessionManager;
    String getId;
    String getIdKids;
    Kids kids;
    Handler handler;

    RipplePulseLayout mRipplePulseLayout;

    public RotaInfAdapter(List<Kids> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
        options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.kids);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_inf_rota_layout, parent, false);
        sessionManager = new SessionManager(context);

        HashMap<String, String> rotas = sessionManager.getIdRoto();
        getId = rotas.get(sessionManager.IDROTA);

        mRipplePulseLayout = view.findViewById(R.id.layout_ripplepulse);

        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        kids = contacts.get(position);
        holder.txtNome.setText(contacts.get(position).getNome());
        holder.txtStatus.setText("STATUS: " + contacts.get(position).getStatus());
        holder.txtEmbarque.setText("Embarque: " + contacts.get(position).getEmbarque() + " Desembarque: " + contacts.get(position).getDesembarque());

        Glide.with(context).load(contacts.get(position).getImg()).apply(options).into(holder.imgPerfilT);

        String status = holder.txtStatus.getText().toString().trim();
        if (status.equals("STATUS: Desembarcou")){
            holder.imgPerfilT.setBackgroundResource(R.drawable.circle_desembarcou);
        }else if (status.equals("STATUS: Embarcou")){
            holder.imgPerfilT.setBackgroundResource(R.drawable.circle_embarcou);
        }else if (status.equals("STATUS: Faltou")){
            holder.imgPerfilT.setBackgroundResource(R.drawable.circle_faltou);
            mRipplePulseLayout.stopRippleAnimation();
        }else {
            holder.imgPerfilT.setBackgroundResource(R.drawable.circle);
            mRipplePulseLayout.stopRippleAnimation();
        }

        //Embarque
        String horarioE = contacts.get(position).getEmbarque();
        String novoHE = horarioE.replace(":","");
        long novoHE2 = Integer.parseInt(novoHE);

        //Desembarque
        String horarioD = contacts.get(position).getDesembarque();
        String novoHD = horarioD.replace(":","");
        long novoHD2 = Integer.parseInt(novoHD);

        //Horario do celular
        Calendar cal = Calendar.getInstance();
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String formattedDate=dateFormat.format(date);
        String subHorario = formattedDate.replace(":", "");
        long novoH3 = Integer.parseInt(subHorario);

        if(novoHE2 == novoH3 && !status.equals("STATUS: Embarcou")){
            mRipplePulseLayout.startRippleAnimation();
        }

        if(novoHD2 == novoH3 && !status.equals("STATUS: Embarcou")){
            mRipplePulseLayout.startRippleAnimation();
        }


        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //from the left
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.swipeLayout.findViewById(R.id.bottom_wrapper1));

        //from the right
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wraper));

        holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });

        holder.Embarco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdKids = String.valueOf(contacts.get(position).getIdKids());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EMBARQUE,
                        new com.android.volley.Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    //boolean success = jsonObject.getBoolean("success");
                                    String success = jsonObject.getString("success");
                                    //JSONArray success = jsonObject.getJSONArray("success");

                                    if (success.equals("OK")){
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("data_changed1"));

                                    }else {
//                                        Toast toast= Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG);
//                                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
//                                        toast.show();
                                        final Snackbar snackbar = showSnackbar(swipeLayout, Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        View view = snackbar.getView();
                                        TextView tv = (TextView) view.findViewById(R.id.textSnack);
                                        tv.setText(jsonObject.getString("message"));
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
                        params.put("idKids", getIdKids);
                        params.put("embarque", "Embarcou");
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
            }
        });

        holder.Desembarco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getIdKids = String.valueOf(contacts.get(position).getIdKids());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DESEMBARQUE,
                        new com.android.volley.Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    //boolean success = jsonObject.getBoolean("success");
                                    String success = jsonObject.getString("success");
                                    //JSONArray success = jsonObject.getJSONArray("success");

                                    if (success.equals("OK")){
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("data_changed1"));
                                    }else {
//                                        Toast toast= Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG);
//                                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
//                                        toast.show();
                                        final Snackbar snackbar = showSnackbar(swipeLayout, Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        View view = snackbar.getView();
                                        TextView tv = (TextView) view.findViewById(R.id.textSnack);
                                        tv.setText(jsonObject.getString("message"));
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
                        params.put("idKids", getIdKids);
                        params.put("desembarque", "Desembarcou");
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
            }
        });

        holder.Faltou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdKids = String.valueOf(contacts.get(position).getIdKids());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DESEMBARQUE,
                        new com.android.volley.Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    //boolean success = jsonObject.getBoolean("success");
                                    String success = jsonObject.getString("success");
                                    //JSONArray success = jsonObject.getJSONArray("success");

                                    if (success.equals("OK")){
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("data_changed1"));
                                    }else {
//                                        Toast toast= Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG);
//                                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
//                                        toast.show();

                                        final Snackbar snackbar = showSnackbar(swipeLayout, Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        View view = snackbar.getView();
                                        TextView tv = (TextView) view.findViewById(R.id.textSnack);
                                        tv.setText(jsonObject.getString("message"));
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
                        params.put("idKids", getIdKids);
                        params.put("desembarque", "Faltou");
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
            }
        });

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
                        getIdKids = String.valueOf(contacts.get(position).getIdKids());
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETA_KID_ROTA,
                                new com.android.volley.Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            //boolean success = jsonObject.getBoolean("success");
                                            String success = jsonObject.getString("success");
                                            //JSONArray success = jsonObject.getJSONArray("success");

                                            if (success.equals("OK")){
                                                //LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("data_changed1"));
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
                                            dialog.dismiss();
                                            e1.printStackTrace();
                                            Log.e("JSON", "Error parsing JSON", e1);
                                            Log.e("Chamada", response);
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
                                params.put("idKids", getIdKids);
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

        static SwipeLayout swipeLayout;
        private final Context context;
        TextView txtNome, txtStatus, txtEmbarque;
        CircleImageView imgPerfilT;
        public TextView Delete, Desembarco, Embarco, Faltou;
        TextView text;

        public MyViewHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();
            swipeLayout = itemView.findViewById(R.id.swipe);
            Delete = itemView.findViewById(R.id.Delete);
            Desembarco = itemView.findViewById(R.id.Desembarco);
            Embarco = itemView.findViewById(R.id.txtEmbarco);
            text = itemView.findViewById(R.id.text);
            Faltou = itemView.findViewById(R.id.Faltou);

            txtNome = itemView.findViewById(R.id.txtNome);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtEmbarque = itemView.findViewById(R.id.txtEmbarque);
            imgPerfilT = itemView.findViewById(R.id.imgPerfilT);


        }
    }
}
