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
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.gauravbhola.ripplepulsebackground.RipplePulseLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.Activies.RotaActivity;
import br.com.transescolar.controler.RotaControler;
import br.com.transescolar.model.Kids;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import br.com.transescolar.model.Rota;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    Rota rota;
    Handler handler;
    RotaControler rotaControler;

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

        mRipplePulseLayout = view.findViewById(R.id.layout_ripplepulse);

        rotaControler = new RotaControler();
        kids = new Kids();
        rota = new Rota();

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
        String horarioE = contacts.get(position).getStatus();
        String novoHE = horarioE.replace(":","");
        String novoHE2 = novoHE;

        //Desembarque
        String horarioD = contacts.get(position).getDesembarque();
        String novoHD = horarioD.replace(":","");
        String novoHD2 = novoHD;

        //Horario do celular
        Calendar cal = Calendar.getInstance();
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String formattedDate=dateFormat.format(date);
        String subHorario = formattedDate.replace(":", "");
        String novoH3 = subHorario;

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

                kids.setIdKids(Integer.parseInt(getIdKids));
                kids.setStatus("Embarco");

                rotaControler.upDateEmbarque(kids, context);
            }
        });

        holder.Desembarco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getIdKids = String.valueOf(contacts.get(position).getIdKids());

                kids.setIdKids(Integer.parseInt(getIdKids));
                kids.setStatus("Desembarco");

                rotaControler.upDateDesembarque(kids, context);
            }
        });

        holder.Faltou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdKids = String.valueOf(contacts.get(position).getIdKids());
                kids.setIdKids(Integer.parseInt(getIdKids));
                kids.setStatus("Faltou");
                rotaControler.upDateFaltou(kids, context);
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
                        kids.setIdKids(Integer.parseInt(getIdKids));
                        rota.setId(getId);

                        Call<ResponseBody> call = ApiClient
                                .getInstance()
                                .getApiR()
                                .deletarKidsRotas(rota.getId(), String.valueOf(kids.getIdKids()));

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                String s = null;

                                try {
                                    if (response.code() == 201) {
                                        mItemManger.removeShownLayouts(holder.swipeLayout);
                                        contacts.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, contacts.size());
                                        mItemManger.closeAllItems();
                                        dialog.dismiss();

                                    }else {
                                        s = response.errorBody().string();
                                        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (s != null){
                                    try {
                                        JSONObject jsonObject = new JSONObject(s);
                                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.e("Call", "Error", t);
                            }
                        });

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
