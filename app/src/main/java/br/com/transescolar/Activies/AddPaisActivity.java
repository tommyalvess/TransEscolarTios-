package br.com.transescolar.Activies;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.transescolar.API.URL.URL_REGIST;
import static br.com.transescolar.API.URL.URL_REGIST_PAIS;


public class AddPaisActivity extends AppCompatActivity {

    EditText editNome, editCpf, editTell, editEmail;

    static SessionManager sessionManager;

    String getId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pais);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Add Pais");     //Titulo para ser exibido na sua Action Bar em frente à seta

        editNome = findViewById(R.id.editNomeT);
        editCpf =  findViewById(R.id.editCpfT);
        editTell =  findViewById(R.id.editTellT);
        editEmail = findViewById(R.id.editEmailT);

        editNome.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        findViewById(R.id.btnSaveCadastro).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUser();
            }
        });

    }

    private void registerUser() {

        final String nome = editNome.getText().toString().trim();
        final String cpf = editCpf.getText().toString().trim();
        final String tell = editTell.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();
        final String senha = "123456789";

        if (nome.isEmpty()){
            editNome.setError("Insira o seu nome");
            editNome.requestFocus();
            return;
        }
        if (cpf.isEmpty()){
            editCpf.setError("Insira o seu CPF");
            editCpf.requestFocus();
            return;
        }

        if (tell.isEmpty()){
            editTell.setError("Insira o telefone");
            editTell.requestFocus();
            return;
        }
        if (email.isEmpty()){
            editEmail.setError("Insira um email!");
            editEmail.requestFocus();
            return;
        }
        if (validateEmail(email) != true){
            editEmail.setError("Email invalido!");
            editEmail.requestFocus();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST_PAIS,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //boolean success = jsonObject.getBoolean("success");
                            String success = jsonObject.getString("success");
                            //JSONArray success = jsonObject.getJSONArray("success");

                            if (success.equals("1")){
                                Toast.makeText(AddPaisActivity.this, "Resgistrado com Sucesso!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddPaisActivity.this, PaisActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(AddPaisActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddPaisActivity.this, "Erro ao resgistrar o usuario!", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nm_pai", nome);
                params.put("email", email);
                params.put("cpf", cpf);
                params.put("tell", tell);
                params.put("idTios", getId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private static boolean validateEmail(String email) {
        Pattern patternEmail = Patterns.EMAIL_ADDRESS;
        if (TextUtils.isEmpty(email))
            return false;

        Matcher matcher = patternEmail.matcher(email);
        return matcher.matches();
    }



}
