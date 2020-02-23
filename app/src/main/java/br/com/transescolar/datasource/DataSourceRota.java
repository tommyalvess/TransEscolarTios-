package br.com.transescolar.datasource;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.transescolar.Activies.CadastrarRotaActivity;
import br.com.transescolar.Activies.RotaActivity;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import br.com.transescolar.controler.TioControler;
import br.com.transescolar.model.Rota;

import static br.com.transescolar.API.URL.URL_REGIST_ROTA;

public class DataSourceRota {

    private Context context;
    private TioControler tioControler;

    public boolean inserirDadosRota(Rota objRota){
        //TODO: Adicionar Tios

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST_ROTA,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //boolean success = jsonObject.getBoolean("success");
                            String success = jsonObject.getString("success");
                            //JSONArray success = jsonObject.getJSONArray("success");


                            if (success.equals("OK")){
                                Intent intent = new Intent(context, RotaActivity.class);
                                DataSourceRota.this.context.startActivity(intent);
                                //progressBar.setVisibility(View.GONE);
                            }else {
                                Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                               // progressBar.setVisibility(View.GONE);
                                //btnSalvarI.setVisibility(View.VISIBLE);

                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            Log.e("JSON", "Error parsing JSON", e1);
                            Log.e("Chamada", response);
                            //progressBar.setVisibility(View.GONE);
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressBar.setVisibility(View.GONE);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nm_rota", objRota.getNm_rota());
                params.put("hora", objRota.getHora());
                params.put("dias", objRota.getDias());
                params.put("idTios", tioControler.pegarIdTio());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

        return true;
    }
}
