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
import android.view.MenuItem;
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

import br.com.transescolar.controler.RotaControler;
import br.com.transescolar.model.Rota;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import ca.antonious.materialdaypicker.MaterialDayPicker;

import static br.com.transescolar.API.URL.URL_EDIT_ROTA;

public class EditRotaActivity extends AppCompatActivity {

    TextInputEditText nomeI;
    MaskEditText horarioI;
    MaterialDayPicker diasI;
    public static Button btnSalvarI;
    public static ProgressBar progressBar;

    Rota objRota;
    String getId;

    ConstraintLayout constraintLayoutEditR;

    //Day buttons
    ToggleButton tDon, tSeg, tTer, tQua, tQui, tSex, tSab;
    String markedButtons= "";

    RotaControler rotaControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rota);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Editar Rota");     //Titulo para ser exibido na sua Action Bar em frente à seta

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Rota rota = (Rota) getIntent().getExtras().get("rota");

        objRota = new Rota();
        rotaControler = new RotaControler();

        constraintLayoutEditR = findViewById(R.id.constraintLayoutEditR);
        progressBar = findViewById(R.id.progess);
        nomeI = findViewById(R.id.nomeI);
        horarioI = findViewById(R.id.horarioI);
        btnSalvarI = findViewById(R.id.btnSalvarI);
        progressBar.setVisibility(View.GONE);

        nomeI.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        tDon = findViewById(R.id.tDom);
        tSeg = findViewById(R.id.tSeg);
        tTer = findViewById(R.id.tTer);
        tQua = findViewById(R.id.tQua);
        tQui = findViewById(R.id.tQui);
        tSex = findViewById(R.id.tSex);
        tSab = findViewById(R.id.tSab);

        progressBar.setVisibility(View.GONE);

        getId = String.valueOf(rota.getId());
        nomeI.setText(rota.getNm_rota());
        horarioI.setText(rota.getHora());

        btnSalvarI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    popularDados();
            }
        });
    }

    private void popularDados() {

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
        objRota.setId(getId);

        if (objRota.getHora().isEmpty()){
            nomeI.setError("Campo não pode ficar vazio");
            nomeI.requestFocus();
            return;
        }else if (objRota.getHora().isEmpty()){
            horarioI.setError("Campo não pode ficar vazio");
            horarioI.requestFocus();
            return;
        }else if(!tDon.isChecked() && !tSeg.isChecked() && !tTer.isChecked() && !tQua.isChecked() && !tQui.isChecked() && !tSex.isChecked() && !tSab.isChecked()){
            Toast.makeText(EditRotaActivity.this, "Selecione pelo menos um dia da semana!", Toast.LENGTH_SHORT).show();
        }else {
            progressBar.setVisibility(View.VISIBLE);
            btnSalvarI.setVisibility(View.GONE);
            rotaControler.updateRota(objRota, EditRotaActivity.this);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
