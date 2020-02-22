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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadastro");     //Titulo para ser exibido na sua Action Bar em frente à seta


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
                //if user pressed on button register
                //here we will register the user to server
                registerUser();
            }
        });
    }

    private void registerUser() {

        int selectedId = radiogTios.getCheckedRadioButtonId();
        // find which radioButton is checked by id
        if(selectedId == bTios.getId()) {
            tio = "Tio ";
        } else if(selectedId == bTias.getId()) {
            tio = "Tia ";
        }

        final String nome = editNome.getText().toString().trim();
        final String cpf = editCpf.getText().toString().trim();
        final String apelido = tio + editApelido.getText().toString().trim();
        final String placa =  editPlaca.getText().toString().trim();
        final String tell = editTell.getText().toString().trim();
        final String senha = editSenha.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();


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

        if (apelido.isEmpty()){
            editApelido.setError("Insira o seu Apelido");
            editApelido.requestFocus();
            return;
        }

        if (placa.isEmpty()){
            editPlaca.setError("Insira a placa");
            editPlaca.requestFocus();
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
//        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
//            editEmail.setError("Email invalido!");
//            editEmail.requestFocus();
//            return;
//        }

        if (senha.isEmpty()){
            editSenha.setError("Insira sua senha!");
            editSenha.requestFocus();
            return;
        }

        if (senha.length() < 6){
            editSenha.setError("Senha deve ter 6 caracteres!");
            editSenha.requestFocus();
            return;
        }

        Call<ResponseBody> call = ApiClient
                .getInstance()
                .getApi()
                .createuser(nome, email, cpf, apelido, placa, tell, senha);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String s = null;

                try {
                    if (response.code() == 201) {

                        s = response.body().string();
                        Intent intent = new Intent(Cadastro2Activity.this, LoginActivity.class);
                        startActivity(intent);

                    }else {
                        s = response.errorBody().string();
                        Log.e("Chamada", s);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (s != null){
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        final Snackbar snackbar = showSnackbar(constraintLayoutCadas, Snackbar.LENGTH_LONG);
                        snackbar.show();
                        View view = snackbar.getView();
                        TextView tv = (TextView) view.findViewById(R.id.textSnack);
                        tv.setText(jsonObject.getString("message"));


                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Call", "Error", t);
            }
        });

    }// final registerUser()

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

    private Snackbar showSnackbar(ConstraintLayout coordinatorLayout, int duration) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "", duration);
        // 15 is margin from all the sides for snackbar
        int marginFromSides = 15;

        float height = 100;

        //inflate view
        LayoutInflater inflater = (LayoutInflater)Cadastro2Activity.this.getApplicationContext().getSystemService
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
