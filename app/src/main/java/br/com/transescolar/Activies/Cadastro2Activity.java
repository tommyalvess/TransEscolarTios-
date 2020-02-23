package br.com.transescolar.Activies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import com.google.android.material.snackbar.Snackbar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.R;
import br.com.transescolar.controler.CadastroControler;
import br.com.transescolar.model.Tios;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class  Cadastro2Activity extends AppCompatActivity {

    EditText editNome, editCpf, editApelido, editPlaca, editTell, editSenha, editEmail;
    ProgressBar progressBar;
    String tio;
    RadioGroup radiogTios;
    RadioButton bTios, bTias;
    ConstraintLayout constraintLayoutCadas;

    Tios objTios;
    CadastroControler cadastroControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadastro");     //Titulo para ser exibido na sua Action Bar em frente à seta


        objTios = new Tios();
        cadastroControler = new CadastroControler();

        editNome = findViewById(R.id.editNomeT);
        editCpf =  findViewById(R.id.editCpfT);
        editApelido =  findViewById(R.id.editApelido2);
        editPlaca = findViewById(R.id.editPlaca);
        editTell =  findViewById(R.id.editTellT);
        editSenha =  findViewById(R.id.editSenhaT);
        editEmail = findViewById(R.id.editEmailT);
        radiogTios = (RadioGroup) findViewById(R.id.rgTios);
        bTios = findViewById(R.id.rbTio);
        bTias = findViewById(R.id.rbTia);
        constraintLayoutCadas = findViewById(R.id.constraintLayoutCadas);
        editNome.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editApelido.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);


        findViewById(R.id.btnSaveCadastro).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: conexão
                popularDadosTios();
                cadastroControler.salvarTios(objTios);
            }
        });
    }

    private void popularDadosTios(){

        //TODO: inserido atributos no model Tios

        objTios.setNome(editNome.getText().toString().trim());
        objTios.setCpf(editCpf.getText().toString().trim());
        objTios.setApelido(tio + editApelido.getText().toString().trim());
        objTios.setPlaca(editPlaca.getText().toString().trim());
        objTios.setTell(editTell.getText().toString().trim());
        objTios.setSenha(editSenha.getText().toString().trim());
        objTios.setEmail(editEmail.getText().toString().trim());

        int selectedId = radiogTios.getCheckedRadioButtonId();
        // find which radioButton is checked by id
        if(selectedId == bTios.getId()) {
            tio = "Tio ";
        } else if(selectedId == bTias.getId()) {
            tio = "Tia ";
        }

        if (objTios.getNome().isEmpty()){
            editNome.setError("Insira o seu nome");
            editNome.requestFocus();
            return;
        }
        if (objTios.getCpf().isEmpty()){
            editCpf.setError("Insira o seu CPF");
            editCpf.requestFocus();
            return;
        }

        if (objTios.getApelido().isEmpty()){
            editApelido.setError("Insira o seu Apelido");
            editApelido.requestFocus();
            return;
        }

        if (objTios.getPlaca().isEmpty()){
            editPlaca.setError("Insira a placa");
            editPlaca.requestFocus();
            return;
        }

        if (objTios.getTell().isEmpty()){
            editTell.setError("Insira o telefone");
            editTell.requestFocus();
            return;
        }
        if (objTios.getEmail().isEmpty()){
            editEmail.setError("Insira um email!");
            editEmail.requestFocus();
            return;
        }


    }

    public static boolean isValidPassword(String s) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "[a-zA-Z0-9\\!\\@\\#\\$]{8,24}"
        );
        return !TextUtils.isEmpty(s) && PASSWORD_PATTERN.matcher(s).matches();
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
