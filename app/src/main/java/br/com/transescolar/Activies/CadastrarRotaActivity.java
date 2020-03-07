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
import android.widget.Toast;
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
import br.com.transescolar.model.Tios;
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
    Tios objTios;
    RotaControler rotaControler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_rota);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadastrar Rota");     //Titulo para ser exibido na sua Action Bar em frente à seta

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        objRota = new Rota();
        objTios = new Tios();
        rotaControler = new RotaControler();

        //TODO: Pegando os dados do usuario armazenado.
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        progressBar = findViewById(R.id.progess);
        nomeI = findViewById(R.id.nomeI);
        horarioI = findViewById(R.id.horarioI);
        btnSalvarI = findViewById(R.id.btnSalvarI);
        constraintLayoutCadR = findViewById(R.id.constraintLayoutCadR);
        nomeI.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        progressBar.setVisibility(View.GONE);

        tDon = findViewById(R.id.tDom);
        tSeg = findViewById(R.id.tSeg);
        tTer = findViewById(R.id.tTer);
        tQua = findViewById(R.id.tQua);
        tQui = findViewById(R.id.tQui);
        tSex = findViewById(R.id.tSex);
        tSab = findViewById(R.id.tSab);

        btnSalvarI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //TODO: conexão com Controler
                popularDadosRota();
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
        objTios.setId(getId);

        if (objRota.getNm_rota().isEmpty()){
            nomeI.setError("Campo não pode ficar vazio");
            nomeI.requestFocus();
            return;
        } else if (objRota.getHora().isEmpty()){
            horarioI.setError("Campo não pode ficar vazio");
            horarioI.requestFocus();
            return;
        } else {
            rotaControler.salvarRota(objRota, objTios, CadastrarRotaActivity.this);
        }



    }

}
