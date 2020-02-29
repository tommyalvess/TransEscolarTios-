package br.com.transescolar.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import br.com.transescolar.Activies.AddKidsActivity;
import br.com.transescolar.model.Pais;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.transescolar.API.URL.URL_DELETA_PAI;
import static br.com.transescolar.Adapter.PaiAdapter.MyViewHolder.swipeLayout;


public class PaiAdapter extends RecyclerSwipeAdapter<PaiAdapter.MyViewHolder> {

    private final Context context;
    private final List<Pais> nData;
    RequestOptions options;

    final String[] items = {"Desculpe, não iremos hoje!", "Estamos chegando!", "O carro quebrou!"};

    SessionManager sessionManager;
    String getNome, getId;

    public PaiAdapter(List<Pais> filhos, Context context) {
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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.linha_pais_list,parent,false);
        sessionManager = new SessionManager(context);

        HashMap<String, String> user = sessionManager.getUserDetail();
        getNome = user.get(sessionManager.NAME);
        getId = user.get(sessionManager.ID);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Pais pais = this.nData.get(position);
        final String idPais = String.valueOf(pais.getIdPais());
        holder.nome.setText(nData.get(position).getNome());
        holder.tell.setText(nData.get(position).getTell());
        holder.email.setText(nData.get(position).getEmail());
        Glide.with(context).load(nData.get(position).getImg()).apply(options).into(holder.img);

        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //from the right
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wraper));

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
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETA_PAI,
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
                                                nData.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, nData.size());
                                                mItemManger.closeAllItems();
                                                dialog.dismiss();
                                            }else {

                                                Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

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
                                params.put("idTios", getId);
                                params.put("idPais", idPais);
                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        requestQueue.add(stringRequest);
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

        holder.AddKids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(PaiAdapter.this.context, AddKidsActivity.class);
                it.putExtra("paisId", pais);
                PaiAdapter.this.context.startActivity(it);
            }
        });

        mItemManger.bindView(holder.itemView, position);

        swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Notificar o Pai!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        if (item == 0){
                            Toast.makeText(context, R.string.mensagem_enviada, Toast.LENGTH_SHORT).show();


                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                                    if (SDK_INT > 8) {
                                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                                .permitAll().build();
                                        StrictMode.setThreadPolicy(policy);
                                        String send_email = nData.get(position).getCpf();

                                        try {
                                            String jsonResponse;

                                            URL url = new URL("https://onesignal.com/api/v1/notifications");
                                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                            con.setUseCaches(false);
                                            con.setDoOutput(true);
                                            con.setDoInput(true);

                                            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                                            con.setRequestProperty("Authorization", "Basic MzUzNzMxMjMtOTIxNy00ZTBlLTg2YjktMDRlOTg4YmEwNzFh");
                                            con.setRequestMethod("POST");

                                            String strJsonBody = "{"
                                                    + "\"app_id\": \"ef54b0b1-d6b0-46e0-ad4f-4e15d9f7dfe6\","

                                                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                                    + "\"data\": {\"foo\": \"bar\"},"
                                                    + "\"contents\": {\"en\": \" "+ getNome +":"+"Desculpe, não iremos hoje"+"\"}"
                                                    + "}";


                                            System.out.println("strJsonBody:\n" + strJsonBody);

                                            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                                            con.setFixedLengthStreamingMode(sendBytes.length);

                                            OutputStream outputStream = con.getOutputStream();
                                            outputStream.write(sendBytes);

                                            int httpResponse = con.getResponseCode();
                                            System.out.println("httpResponse: " + httpResponse);

                                            if (httpResponse >= HttpURLConnection.HTTP_OK
                                                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                                                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                                                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                                scanner.close();
                                            } else {
                                                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                                                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                                scanner.close();
                                            }
                                            System.out.println("jsonResponse:\n" + jsonResponse);

                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }else if (item == 1){

                            Toast.makeText(context, R.string.mensagem_enviada, Toast.LENGTH_SHORT).show();

                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                                    if (SDK_INT > 8) {
                                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                                .permitAll().build();
                                        StrictMode.setThreadPolicy(policy);
                                        String send_email = nData.get(position).getCpf();

                                        try {
                                            String jsonResponse;

                                            URL url = new URL("https://onesignal.com/api/v1/notifications");
                                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                            con.setUseCaches(false);
                                            con.setDoOutput(true);
                                            con.setDoInput(true);

                                            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                                            con.setRequestProperty("Authorization", "Basic MzUzNzMxMjMtOTIxNy00ZTBlLTg2YjktMDRlOTg4YmEwNzFh");
                                            con.setRequestMethod("POST");

                                            String strJsonBody = "{"
                                                    + "\"app_id\": \"ef54b0b1-d6b0-46e0-ad4f-4e15d9f7dfe6\","

                                                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                                    + "\"data\": {\"foo\": \"bar\"},"
                                                    + "\"contents\": {\"en\": \" "+ getNome +":"+"Estamos chegando!"+"\"}"
                                                    + "}";


                                            System.out.println("strJsonBody:\n" + strJsonBody);

                                            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                                            con.setFixedLengthStreamingMode(sendBytes.length);

                                            OutputStream outputStream = con.getOutputStream();
                                            outputStream.write(sendBytes);

                                            int httpResponse = con.getResponseCode();
                                            System.out.println("httpResponse: " + httpResponse);

                                            if (httpResponse >= HttpURLConnection.HTTP_OK
                                                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                                                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                                                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                                scanner.close();
                                            } else {
                                                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                                                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                                scanner.close();
                                            }
                                            System.out.println("jsonResponse:\n" + jsonResponse);

                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }else if (item == 2){
                            Toast.makeText(context, R.string.mensagem_enviada, Toast.LENGTH_SHORT).show();

                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                                    if (SDK_INT > 8) {
                                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                                .permitAll().build();
                                        StrictMode.setThreadPolicy(policy);
                                        String send_email = nData.get(position).getCpf();

                                        try {
                                            String jsonResponse;

                                            URL url = new URL("https://onesignal.com/api/v1/notifications");
                                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                            con.setUseCaches(false);
                                            con.setDoOutput(true);
                                            con.setDoInput(true);

                                            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                                            con.setRequestProperty("Authorization", "Basic MzUzNzMxMjMtOTIxNy00ZTBlLTg2YjktMDRlOTg4YmEwNzFh");
                                            con.setRequestMethod("POST");

                                            String strJsonBody = "{"
                                                    + "\"app_id\": \"ef54b0b1-d6b0-46e0-ad4f-4e15d9f7dfe6\","

                                                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                                    + "\"data\": {\"foo\": \"bar\"},"
                                                    + "\"contents\": {\"en\": \" "+ getNome +":"+"O carro quebrou!"+"\"}"
                                                    + "}";


                                            System.out.println("strJsonBody:\n" + strJsonBody);

                                            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                                            con.setFixedLengthStreamingMode(sendBytes.length);

                                            OutputStream outputStream = con.getOutputStream();
                                            outputStream.write(sendBytes);

                                            int httpResponse = con.getResponseCode();
                                            System.out.println("httpResponse: " + httpResponse);

                                            if (httpResponse >= HttpURLConnection.HTTP_OK
                                                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                                                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                                                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                                scanner.close();
                                            } else {
                                                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                                                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                                scanner.close();
                                            }
                                            System.out.println("jsonResponse:\n" + jsonResponse);

                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return nData.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome, tell, email, Delete, AddKids;
        CircleImageView img;
        static SwipeLayout swipeLayout;

        //private final Context context;

        public MyViewHolder(View itemView){
            super(itemView);

            //context = itemView.getContext();

            nome = itemView.findViewById(R.id.txtNomeP);
            tell = itemView.findViewById(R.id.txtTellP);
            email = itemView.findViewById(R.id.txtEmailP);
            img = itemView.findViewById(R.id.imgPerfilK);
            swipeLayout = itemView.findViewById(R.id.swipe);
            Delete = itemView.findViewById(R.id.Delete);
            AddKids = itemView.findViewById(R.id.AddKids);
        }

    }
}
