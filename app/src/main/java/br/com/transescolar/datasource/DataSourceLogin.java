package br.com.transescolar.datasource;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

import br.com.transescolar.Activies.HomeActivity;
import br.com.transescolar.Activies.LoginActivity;
import br.com.transescolar.controler.TioControler;
import br.com.transescolar.model.Tios;

import static br.com.transescolar.API.URL.URL_LOGIN;
import static br.com.transescolar.Activies.LoginActivity.btnLogin;
import static br.com.transescolar.Activies.LoginActivity.loginProgress;
import static br.com.transescolar.Activies.LoginActivity.sessionManager;
import static com.android.volley.VolleyLog.TAG;

public class DataSourceLogin {

    public boolean sessionTio(Tios objTio, Context context){
        //TODO: logando tio
        
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray nameArray = json.names();
                            JSONArray valArray = json.toJSONArray( nameArray );
                            String success = json.getString("error");


                            if (success.equals("OK")){

                                for ( int i = 0; i < valArray.length(); i++) {
                                    JSONObject object = valArray.getJSONObject(i);
                                    objTio.setId(object.getString("idTios").trim());
                                    objTio.setNome(object.getString("nome").trim());
                                    objTio.setEmail(object.getString("email").trim());
                                    objTio.setCpf(object.getString("cpf").trim());
                                    objTio.setApelido(object.getString("apelido").trim());
                                    objTio.setPlaca(object.getString("placa").trim());
                                    objTio.setTell(object.getString("tell").trim());
                                    objTio.setImg(object.getString("img").trim());

                                    sessionManager.createSession(objTio.getId(), objTio.getNome(), objTio.getEmail(),
                                            objTio.getCpf(), objTio.getApelido(), objTio.getPlaca(), objTio.getTell(),
                                            objTio.getImg());
                                    sessionManager.createSessionStatus("notChecked");


                                    Intent intent = new Intent(context, HomeActivity.class);
                                    intent.putExtra("idTios", objTio.getId());
                                    intent.putExtra("nome", objTio.getNome());
                                    intent.putExtra("email", objTio.getEmail());
                                    intent.putExtra("cpf", objTio.getCpf());
                                    intent.putExtra("apelido", objTio.getApelido());
                                    intent.putExtra("placa", objTio.getPlaca());
                                    intent.putExtra("tell", objTio.getTell());
                                    intent.putExtra("img", objTio.getImg());
                                    context.startActivity(intent);

                                    loginProgress.setVisibility(View.GONE);
                                    btnLogin.setVisibility(View.VISIBLE);

                                }
                            }else if(success.equals("falhou")) {
                                Toast.makeText(context,json.getString("message"),Toast.LENGTH_LONG).show();
                                loginProgress.setVisibility(View.GONE);
                                btnLogin.setVisibility(View.VISIBLE);

                            }
                        }catch ( JSONException e ) {
                            loginProgress.setVisibility(View.GONE);
                            btnLogin.setVisibility(View.VISIBLE);
                            Log.e("JSON", "Error parsing JSON", e);
                        }

                        Log.e(TAG, "response: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loginProgress.setVisibility(View.GONE);
                        btnLogin.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "Opss!! Sem Conexão a internet", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "response: " + error);
                    }
                }){
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> param = new HashMap<>();
                param.put("cpf", objTio.getCpf());
                param.put("senha", objTio.getSenha());
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

        return true;
    }
}
