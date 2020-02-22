package br.com.transescolar.Activies;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import br.com.transescolar.model.Kids;
import br.com.transescolar.model.Pais;
import br.com.transescolar.R;


public class InfKidsActivity extends AppCompatActivity {

    ImageView imgKid;
    TextView nomeK, periodoK, escolaK, endK, dt_nasc, nmpais, txtStatus, txtEmbarque, txtDesembaque;
    int yearNow;
    String yearK, yearKFinal, bdayFinal;
    Kids kids;
    Pais pais;
    int bday;

    RequestOptions cropOptions;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inf_kids);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Kids kids = (Kids) getIntent().getExtras().get("crianca");
        getSupportActionBar().setTitle("");
        nomeK = findViewById(R.id.txtApelidoK);
        periodoK = findViewById(R.id.txtPeriodo);
        escolaK = findViewById(R.id.txtEscolaK);
        imgKid = findViewById(R.id.imgPerfilK);
        endK = findViewById(R.id.txtEnderecoK);
        dt_nasc = findViewById(R.id.txtData);
        nmpais = findViewById(R.id.txtPai);
        txtStatus = findViewById(R.id.txtStatus);
        txtEmbarque = findViewById(R.id.txtEmbarque);
        txtDesembaque = findViewById(R.id.txtDesembaque);

        nomeK.setText(kids.getNome());
        periodoK.setText(kids.getPeriodo());
        escolaK.setText(kids.getNm_escola());
        endK.setText(kids.getEnd_principal());
        dt_nasc.setText(kids.getDt_nas());
        nmpais.setText("");
        txtStatus.setText(kids.getStatus());
        txtEmbarque.setText(kids.getEmbarque());
        txtDesembaque.setText(kids.getDesembarque());

        String status =  txtStatus.getText().toString();

        if (status.equals("Faltou")){
            txtStatus.setTextColor(Color.RED);
        }

        //idade
        yearNow = Calendar.getInstance().get(Calendar.YEAR);
        yearK = kids.getDt_nas();
        yearKFinal = yearK.substring(6);
        bday = yearNow - Integer.parseInt(yearKFinal);
        bdayFinal = String.valueOf(bday);
        nmpais.setText(bdayFinal);
        //fim idade

        cropOptions = new RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).error(R.drawable.kids);
        Glide.with(this).load(kids.getImg()).apply(cropOptions).into(imgKid);


    }

}
