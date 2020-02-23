package br.com.transescolar.Activies;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.santalu.widget.MaskEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import br.com.transescolar.controler.RotaControler;
import ca.antonious.materialdaypicker.MaterialDayPicker;
import br.com.transescolar.model.Rota;


import static br.com.transescolar.API.URL.URL_REGIST_ROTA;

public class CadastrarRotaActivity extends AppCompatActivity {

    TextInputEditText nomeI;
    MaskEditText horarioI;
    MaterialDayPicker diasI;
    Button btnSalvarI;
    public static ProgressBar progressBar;

    SessionManager sessionManager;
    String getId;

    ConstraintLayout constraintLayoutCadR;

    //Day buttons
    ToggleButton tDon, tSeg, tTer, tQua, tQui, tSex, tSab;
    String markedButtons= "";

    Rota objRota;
    RotaControler rotaControler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_rota);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadastrar Rota");     //Titulo para ser exibido na sua Action Bar em frente à seta

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        objRota = new Rota();
        rotaControler = new RotaControler();

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        progressBar = findViewById(R.id.progess);
        nomeI = findViewById(R.id.nomeI);
        horarioI = findViewById(R.id.horarioI);
        btnSalvarI = findViewById(R.id.btnSalvarI);
        progressBar.setVisibility(View.GONE);
        constraintLayoutCadR = findViewById(R.id.constraintLayoutCadR);
        nomeI.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);


        tDon = findViewById(R.id.tDom);
        tSeg = findViewById(R.id.tSeg);
        tTer = findViewById(R.id.tTer);
        tQua = findViewById(R.id.tQua);
        tQui = findViewById(R.id.tQui);
        tSex = findViewById(R.id.tSex);
        tSab = findViewById(R.id.tSab);

        progressBar.setVisibility(View.GONE);

        btnSalvarI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //TODO: conexão com Controler
                popularDadosRota();
                rotaControler.salvarRota(objRota);

            }
        });

    }//Final do onCreat

    private void popularDadosRota(){

        if(tDon.isChecked()){
            markedButtons +="Dom,";
        }
        if(tSeg.isChecked()){
            markedButtons +="Seg,";
        }
        if(tTer.isChecked()){
            markedButtons +="Ter,";
        }
        if(tQua.isChecked()){
            markedButtons +="Qua,";
        }
        if(tQui.isChecked()){
            markedButtons +="Qui,";
        }
        if(tSex.isChecked()){
            markedButtons +="Sex,";
        }
        if(tSab.isChecked()){
            markedButtons +="Sab";
        }

        objRota.setNm_rota(nomeI.getText().toString().trim());
        objRota.setHora(horarioI.getText().toString().trim());
        objRota.setDias(markedButtons);

        if (objRota.getNm_rota().isEmpty()){
            nomeI.setError("Campo não pode ficar vazio");
            nomeI.requestFocus();
            return;
        }

        if (objRota.getHora().isEmpty()){
            horarioI.setError("Campo não pode ficar vazio");
            horarioI.requestFocus();
            return;
        }



    }

    public void Regist() {
        final String nome = this.nomeI.getText().toString().trim();
        final String horario = this.horarioI.getText().toString().trim();

        //Check individual items.
        if(tDon.isChecked()){
            markedButtons +="Dom,";
        }
        if(tSeg.isChecked()){
            markedButtons +="Seg,";
        }
        if(tTer.isChecked()){
            markedButtons +="Ter,";
        }
        if(tQua.isChecked()){
            markedButtons +="Qua,";
        }
        if(tQui.isChecked()){
            markedButtons +="Qui,";
        }
        if(tSex.isChecked()){
            markedButtons +="Sex,";
        }
        if(tSab.isChecked()){
            markedButtons +="Sab";
        }

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
                                Intent intent = new Intent(CadastrarRotaActivity.this, RotaActivity.class);
                                startActivity(intent);
                                progressBar.setVisibility(View.GONE);
                                finish();
                            }else {
                                final Snackbar snackbar = showSnackbar(constraintLayoutCadR, Snackbar.LENGTH_LONG);
                                snackbar.show();
                                View view = snackbar.getView();
                                TextView tv = (TextView) view.findViewById(R.id.textSnack);
                                tv.setText(jsonObject.getString("message"));

                                progressBar.setVisibility(View.GONE);
                                btnSalvarI.setVisibility(View.VISIBLE);

                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            Log.e("JSON", "Error parsing JSON", e1);
                            Log.e("Chamada", response);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nm_rota", nome);
                params.put("hora", horario);
                params.put("dias", markedButtons);
                params.put("idTios", getId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private Snackbar showSnackbar(ConstraintLayout coordinatorLayout, int duration) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "", duration);
        // 15 is margin from all the sides for snackbar
        int marginFromSides = 15;

        float height = 100;

        //inflate view
        LayoutInflater inflater = (LayoutInflater)CadastrarRotaActivity.this.getApplicationContext().getSystemService
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
}
