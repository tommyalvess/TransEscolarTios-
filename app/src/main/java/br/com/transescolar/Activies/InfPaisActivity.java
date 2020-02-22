package br.com.transescolar.Activies;

import android.content.pm.ActivityInfo;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import br.com.transescolar.model.Pais;
import br.com.transescolar.R;

public class InfPaisActivity extends AppCompatActivity {

    ImageView imgP;
    TextView nomeK,emailP,cpfP,tellP;
    Pais pais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inf_pais);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Pais kids = (Pais) getIntent().getExtras().get("pais");
        getSupportActionBar().setTitle(kids.getNome());
        nomeK = findViewById(R.id.nomeK);
        emailP = findViewById(R.id.emailP);
        cpfP = findViewById(R.id.cpfP);
        imgP = findViewById(R.id.imgP);
        tellP = findViewById(R.id.tellP);
        //nmpais = findViewById(R.id.paisK);

        nomeK.setText(kids.getNome());
        emailP.setText(kids.getEmail().toUpperCase());
        cpfP.setText(kids.getCpf());
        tellP.setText(kids.getTell());
        //nmpais.setText(pais.getNome());

        Glide.with(this).load(kids.getImg()).into(imgP);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
