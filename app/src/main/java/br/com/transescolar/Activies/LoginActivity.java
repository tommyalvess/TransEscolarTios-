package br.com.transescolar.Activies;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import br.com.transescolar.controler.TioControler;
import br.com.transescolar.model.Tios;

import static br.com.transescolar.API.URL.URL_LOGIN;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    public static Button btnLogin;
    EditText editCpf, editSenha;
    TextView textForgot;
    public static ProgressBar loginProgress;

    static String LoggedIn_User_Email;

    public static SessionManager sessionManager;

    TioControler tioControler;
    Tios objTio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tioControler = new TioControler();
        objTio = new Tios();

        //OneSignal
        OneSignal.startInit(this).init();

        sessionManager = new SessionManager(this);

        editCpf = findViewById(R.id.login_main);
        editSenha = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btn_login);
        loginProgress = findViewById(R.id.login_progress);
        textForgot = findViewById(R.id.textForgot);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: conexão com controler
                final String cpf = editCpf.getText().toString().trim();
                final String senha  = editSenha.getText().toString().trim();

                objTio.setCpf(editCpf.getText().toString().trim());
                objTio.setSenha(editSenha.getText().toString().trim());

                if (TextUtils.isEmpty(cpf) ) {
                    editCpf.setError("por favor inserir o CPF");
                    editCpf.requestFocus();
                    return;
                }else if (TextUtils.isEmpty(senha)) {
                    editSenha.setError("por favor inserir a senha");
                    editSenha.requestFocus();
                    return;
                }else {
                    LoggedIn_User_Email = cpf.trim();
                    OneSignal.sendTag("User_ID", LoggedIn_User_Email);

                    tioControler.logarTio(objTio, LoginActivity.this);
                }

            }
        });

        textForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RecuperarSenhaActivity.class);
                startActivity(intent);
            }
        });

        checkLocationPermission();
    }




    public void Cadastro(View view) {
        Intent intent = new Intent(LoginActivity.this, CadastroTioActivity.class);
        startActivity(intent);
    }

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
}
