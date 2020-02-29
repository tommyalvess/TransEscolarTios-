package br.com.transescolar.controler;

import android.content.Context;
import android.os.Handler;

import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.transescolar.model.Tios;

import static br.com.transescolar.API.URL.URL_COUNTTIOSKIDS;
import static br.com.transescolar.API.URL.URL_COUNTTIOSPAIS;
import static br.com.transescolar.API.URL.URL_UPLOAD;
import static br.com.transescolar.Activies.PerfilActivity.txtCountKids;
import static br.com.transescolar.Activies.PerfilActivity.txtCountPais;

public class PerfilControler {

    private Tios tios;

    public PerfilControler() {
        this.tios = new Tios();
    }

    //Contar pais
    public void countPais(Context context){
        //TODO: Pegando countPais do BD

        tios = new Tios();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_COUNTTIOSPAIS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Mensagem GetUserDetail", response.toString());
                        try {
                            JSONObject json = new JSONObject(response);
                            String success = json.getString("error");
                            JSONArray nameArray = json.names();
                            JSONArray valArray = json.toJSONArray( nameArray );
                            if (success.equals("OK")){
                                for (int i = 0; i < valArray.length(); i++) {
                                    JSONObject object = valArray.getJSONObject(i);
                                    String nome = object.getString("nome").trim();

                                    txtCountPais.setText(nome);
                                }
                            }
                        }catch ( JSONException e ) {
                            Log.e("JSON", "Error parsing JSON", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", "Error", error);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("idTios", tios.getId());
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    //Contar Kids
    public void countKids(Context context){
        //TODO: Pegando countKids do BD

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_COUNTTIOSKIDS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Mensagem GetUserDetail", response.toString());
                        try {
                            JSONObject json = new JSONObject(response);
                            String success = json.getString("error");
                            JSONArray nameArray = json.names();
                            JSONArray valArray = json.toJSONArray( nameArray );
                            if (success.equals("OK")){
                                for (int i = 0; i < valArray.length(); i++) {
                                    JSONObject object = valArray.getJSONObject(i);
                                    String nome = object.getString("nome").trim();

                                    txtCountKids.setText(nome);
                                }
                            }
                        }catch ( JSONException e ) {
                            Log.e("JSON", "Error parsing JSON", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", "Error", error);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("idTios", tios.getId());
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    //Dialog de conexão
    public void dialogP(boolean value, final Context context){
        //TODO: dialogo para conexão
        if(value){
            //snackbar.dismiss();
            //perfilControler.getUserDetail(context);
            //perfilControler.countPais(context);
            // countKids(context);

            Handler handler = new Handler();
            Runnable delayrunnable = new Runnable() {
                @Override
                public void run() {
                    //snackbar.dismiss();
                }
            };
            handler.postDelayed(delayrunnable, 300);
        }else {
            //getUserDetailSessão(context);
            //txtCountPais.setText("0");
            //txtCountKids.setText("0");
            //snackbar = showSnackbar(relativeLayoutPer, Snackbar.LENGTH_INDEFINITE, context);
            //snackbar.show();
            //View view = snackbar.getView();
            //TextView tv = (TextView) view.findViewById(R.id.textSnack);
            //tv.setText("Sem conexão a internet!");
        }
    }


    //Campo Editar Foto
    public void UploadPicture(final String id, final String cpf, final String photo, Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Mensagem Upload", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("OK")){
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Erro", "Upload", e);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idTios", id);
                params.put("cpf", cpf);
                params.put("img", photo);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


}
