package br.com.transescolar.Activies;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;

import static br.com.transescolar.API.URL.URL_EDIT_SENHA;

public class ResetSenhaActivity extends AppCompatActivity {

    EditText editSenha, editSenhaConfirme;
    Button btnSalvar;

    String getId;
    String getCpf;

    SessionManager sessionManager;
    ConstraintLayout constraintLayoutRest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_senha);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sessionManager = new SessionManager(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Alterar Senha");

        btnSalvar =  findViewById(R.id.btnSalvar);
        editSenha = findViewById(R.id.editText3);
        editSenhaConfirme = findViewById(R.id.editText4);
        constraintLayoutRest = findViewById(R.id.constraintLayoutRest);

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);
        getCpf = user.get(sessionManager.CPF);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String senhaNovo = editSenha.getText().toString().trim();
                String senhaConfirme = editSenhaConfirme.getText().toString().trim();

                if (senhaNovo.equals(senhaConfirme)){
                    if (senhaNovo.length() > 6) {

                        final String senha = editSenha.getText().toString().trim();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT_SENHA,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //progess.setVisibility(View.GONE);
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            //boolean success = jsonObject.getBoolean("success");
                                            String success = jsonObject.getString("success");

                                            if (success.equals("OK")) {
                                                editSenha.setText("");
                                                editSenhaConfirme.setText("");

                                                Intent intent = new Intent(ResetSenhaActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                            } else {
                                                final Snackbar snackbar = showSnackbar(constraintLayoutRest, Snackbar.LENGTH_LONG);
                                                snackbar.show();
                                                View view = snackbar.getView();
                                                TextView tv = (TextView) view.findViewById(R.id.textSnack);
                                                tv.setText(jsonObject.getString("message"));
                                            }

                                        } catch (JSONException e1) {
                                            Log.e("JSON", "Error parsing JSON", e1);
                                        }
                                        Log.e("Chamada", "response: " + response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("JSON", "Error parsing JSON", error);
                                    }
                                }) {

                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("idPais", getId);
                                params.put("senha", senha);
                                params.put("cpf", getCpf);
                                return params;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(ResetSenhaActivity.this);
                        requestQueue.add(stringRequest);

                    }else {
                        final Snackbar snackbar = showSnackbar(constraintLayoutRest, Snackbar.LENGTH_LONG);
                        snackbar.show();
                        View view = snackbar.getView();
                        TextView tv = (TextView) view.findViewById(R.id.textSnack);
                        tv.setText("Senha deve ter pelo menos 6 caracteres!");
                    }
                }else {
                    final Snackbar snackbar = showSnackbar(constraintLayoutRest, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    View view = snackbar.getView();
                    TextView tv = (TextView) view.findViewById(R.id.textSnack);
                    tv.setText("As senhas não são iguais!");
                }
            }
        });
    }//oncreate

    private Snackbar showSnackbar(ConstraintLayout coordinatorLayout, int duration) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "", duration);
        // 15 is margin from all the sides for snackbar
        int marginFromSides = 15;

        float height = 100;

        //inflate view
        LayoutInflater inflater = (LayoutInflater)ResetSenhaActivity.this.getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View snackView = inflater.inflate(R.layout.snackbar_layout, null);

        // White background
        snackbar.getView().setBackgroundResource(R.color.white);
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
}
