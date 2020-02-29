package br.com.transescolar.Activies;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.transescolar.API.URL;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;

import static br.com.transescolar.API.URL.URL_EDIT;
import static br.com.transescolar.API.URL.URL_READ;

public class EditarUsuarioActivity extends AppCompatActivity {

    private static final String TAG = EditarUsuarioActivity.class.getSimpleName();
    EditText editNomeU, editEmailU, editCpfU, editApelidoU, editPlacaU, editTellU;
    SessionManager sessionManager;
    String getId;
    ProgressBar progess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Editar Cadastro");

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        editNomeU =  findViewById(R.id.editarNome);
        editEmailU =  findViewById(R.id.editarEmailT);
        editCpfU =  findViewById(R.id.editarCpfT);
        editApelidoU =  findViewById(R.id.editarApelido2);
        editPlacaU =  findViewById(R.id.editarPlaca);
        editTellU =  findViewById(R.id.editarTellT);
        editTellU.addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s)
            {
                String text = editTellU.getText().toString();
                int  textLength = editTellU.getText().length();
                if (text.endsWith("-") || text.endsWith(" ") || text.endsWith(" "))
                    return;
                if (textLength == 1) {
                    if (!text.contains("("))
                    {
                        editTellU.setText(new StringBuilder(text).insert(text.length() - 1, "(").toString());
                        editTellU.setSelection(editTellU.getText().length());
                    }
                }
                else if (textLength == 4)
                {
                    if (!text.contains(")"))
                    {
                        editTellU.setText(new StringBuilder(text).insert(text.length() - 1, ")").toString());
                        editTellU.setSelection(editTellU.getText().length());
                    }
                }
                else if (textLength == 5)
                {
                    editTellU.setText(new StringBuilder(text).insert(text.length() - 1, " ").toString());
                    editTellU.setSelection(editTellU.getText().length());
                }
                else if (textLength == 11)
                {
                    if (!text.contains("-"))
                    {
                        editTellU.setText(new StringBuilder(text).insert(text.length() - 1, "-").toString());
                        editTellU.setSelection(editTellU.getText().length());
                    }
                }


            }
        });
        progess = findViewById(R.id.progess);

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveEditDetail();
            }
        });

    }

    //Salvar as infs atualizada
    private void SaveEditDetail() {

        final String id = getId;
        final String nome = this.editNomeU.getText().toString().trim();
        final String email = this.editEmailU.getText().toString().trim();
        final String cpf = this.editCpfU.getText().toString().trim();
        final String apelido = this.editApelidoU.getText().toString().trim();
        final String placa = this.editPlacaU.getText().toString().trim();
        final String tell = this.editTellU.getText().toString().trim();

        progess.setVisibility(View.VISIBLE);
        final FloatingActionButton fab = findViewById(R.id.fab);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progess.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //boolean success = jsonObject.getBoolean("success");
                            String success = jsonObject.getString("success");

                            if (success.equals("OK")){
                                sessionManager.createSession(id, nome, email, cpf, apelido, placa, tell);
                                progess.setVisibility(View.GONE);
                                fab.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Toast.makeText(EditarUsuarioActivity.this, "Alterado com Sucesso!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Toast.makeText(EditarUsuarioActivity.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();

                            }else {
                                Toast.makeText(EditarUsuarioActivity.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                                fab.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Toast.makeText(EditarUsuarioActivity.this, "Opss! Algo deu errado!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                progess.setVisibility(View.GONE);
                            }

                        } catch (JSONException e1) {
                            progess.setVisibility(View.GONE);
                            Log.e("JSON", "Error parsing JSON", e1);

                        }
                        Log.e(TAG, "response: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progess.setVisibility(View.GONE);
                        Log.e("JSON", "Error parsing JSON", error);

                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nome", nome);
                params.put("email", email);
                params.put("cpf", cpf);
                params.put("apelido", apelido);
                params.put("placa", placa);
                params.put("tell", tell);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //Pegar as infs do user
    private void getUserDetail(){
        progess.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progess.setVisibility(View.GONE);
                        Log.i(TAG, response.toString());

                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray nameArray = json.names();
                            JSONArray valArray = json.toJSONArray( nameArray );

                            if (!json.optBoolean("falhou")){
                                for (int i = 0; i < valArray.length(); i++) {
                                    JSONObject object = valArray.getJSONObject(i);
                                    String id = object.getString("idTios").trim();
                                    String nome = object.getString("nome").trim();
                                    String email = object.getString("email").trim();
                                    String cpf = object.getString("cpf").trim();
                                    String apelido = object.getString("apelido").trim();
                                    String placa = object.getString("placa").trim();
                                    String tell = object.getString("tell").trim();

                                    editNomeU.setText(nome);
                                    editEmailU.setText(email);
                                    editCpfU.setText(cpf);
                                    editApelidoU.setText(apelido);
                                    editPlacaU.setText(placa);
                                    editTellU.setText(tell);

                                    progess.setVisibility(View.GONE);
                                    Log.e(TAG, "response: " + response);
                                }
                            }else {
                                Toast.makeText(EditarUsuarioActivity.this,json.getString("message"),Toast.LENGTH_LONG).show();
                                progess.setVisibility(View.GONE);

                            }
                        }catch ( JSONException e ) {
                            progess.setVisibility(View.GONE);
                            Log.e("JSON", "Error parsing JSON", e);
                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditarUsuarioActivity.this, "Opss!! Algo deu errado VolleyError", Toast.LENGTH_SHORT).show();
                        Log.e("VolleyError", "Error", error);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("idTios", getId);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserDetail();
    }


}
