package br.com.transescolar.Activies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;

import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.com.transescolar.Conexao.NetworkChangeReceiver2;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import br.com.transescolar.controler.TioControler;
import br.com.transescolar.model.Tios;
import br.com.transescolar.controler.PerfilControler;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.transescolar.API.URL.URL_COUNTTIOSKIDS;
import static br.com.transescolar.API.URL.URL_COUNTTIOSPAIS;
import static br.com.transescolar.API.URL.URL_EDIT;
import static br.com.transescolar.API.URL.URL_READ;
import static br.com.transescolar.API.URL.URL_UPLOAD;
import static br.com.transescolar.controler.HomeControler.showSnackbar;

public class PerfilActivity extends AppCompatActivity {

    private static final String TAG = PerfilActivity.class.getSimpleName();
    public static TextView textNomeU, textEmailU, textCpfU, texPlacaU, textTellU, tv_name, txtCountPais, txtCountKids;
    public static CircleImageView imgPerfilT;
    static String getId, getCpf, nId, nNome,nEmail,nCpf,nApelido,nPlaca,nTell,nIMG;
    private Bitmap bitmap;

    public static SessionManager sessionManager;

    public static RequestOptions cropOptions;

    private final Handler handler = new Handler();

    private NetworkChangeReceiver2 mNetworkReceiver;

    static Snackbar snackbar;
    static RelativeLayout relativeLayoutPer;
    DocumentReference db;

    PerfilControler perfilControler;
    TioControler tioControler;
    Tios tios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        perfilControler = new PerfilControler();
        tioControler =new TioControler();
        tios = new Tios();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("");

        IntentFilter inF1 = new IntentFilter("data_changed");
        LocalBroadcastManager.getInstance(PerfilActivity.this).registerReceiver(dataChangeReceiver1,inF1);

        mNetworkReceiver = new NetworkChangeReceiver2();
        registerNetworkBroadcastForNougat();

        textNomeU =  findViewById(R.id.txtNomeT);
        tv_name = findViewById(R.id.tv_name);
        textEmailU =  findViewById(R.id.txtEmailT);
        textCpfU =  findViewById(R.id.txtCPFT);
        texPlacaU =  findViewById(R.id.txtPlacaT);
        textTellU =  findViewById(R.id.txtTellT);
        imgPerfilT = findViewById(R.id.imgPerfilT);
        txtCountPais = findViewById(R.id.txtCountPais);
        txtCountKids = findViewById(R.id.txtCountKids);
        relativeLayoutPer = findViewById(R.id.relativeLayoutPer);

        //TODO: PEgando dados da Sessão criada
        HashMap<String, String> user = sessionManager.getUserDetail();
        nId = user.get(sessionManager.ID);
        nNome = user.get(sessionManager.NAME);
        nEmail = user.get(sessionManager.EMAIL);
        nCpf = user.get(sessionManager.CPF);
        nApelido = user.get(sessionManager.APELIDO);
        nPlaca = user.get(sessionManager.PLACA);
        nTell = user.get(sessionManager.TELL);
        nIMG = user.get(sessionManager.IMG);

        tios.setId(nId);

        cropOptions = new RequestOptions().centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.kids)
                .error(R.drawable.kids);

        imgPerfilT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosseFile();
            }
        });

        perfilControler.readPerfil(tios, PerfilActivity.this);
        perfilControler.countPais(this);
        perfilControler.countKids(this);

    }// onCreate



    //Pegar inf da sessão
    public static void getUserDetailSessão(Context context){
        textNomeU.setText(nNome);
        textEmailU.setText(nEmail);
        textCpfU.setText(nCpf);
        tv_name.setText(nApelido);
        texPlacaU.setText(nPlaca);
        textTellU.setText(nTell);

        Glide.with(context).load(nIMG).apply(cropOptions).into(imgPerfilT);
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

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);
        return true;
    };


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //Fazer upload da foto
    private void chosseFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione a imagem"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgPerfilT.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Erro", "Upload", e);
            }

            perfilControler.UploadPicture(getId, getCpf, getStringImage(bitmap), PerfilActivity.this);

        }
    }

    public String getStringImage(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        return encodedImage;
    }

    public void EditarNome(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PerfilActivity.this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String nNome = user.get(sessionManager.NAME);
        String nId = user.get(sessionManager.ID);
        int id = Integer.parseInt(nId);

        View mView = getLayoutInflater().inflate(R.layout.dialog_nome, null);
        final EditText nomeE = (EditText) mView.findViewById(R.id.nomeD);
        nomeE.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        Button mSim = (Button) mView.findViewById(R.id.btnSim);
        Button mNao = (Button) mView.findViewById(R.id.btnNao);

        alertDialog.setTitle("Editar Nome");

        alertDialog.setView(mView);
        final AlertDialog dialog = alertDialog.create();

        nomeE.setText(nNome);

        mNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> user = sessionManager.getUserDetail();

                final String id = user.get(sessionManager.ID);
                final String nome = nomeE.getText().toString().trim();
                final String email = user.get(sessionManager.EMAIL);
                final String cpf = user.get(sessionManager.CPF);
                final String tell = user.get(sessionManager.TELL);
                final String placa = user.get(sessionManager.PLACA);
                final String apelido = user.get(sessionManager.APELIDO);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //progess.setVisibility(View.GONE);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    //boolean success = jsonObject.getBoolean("success");
                                    String success = jsonObject.getString("success");

                                    if (success.equals("OK")){
                                        sessionManager.createSession(id, nome, email, cpf, apelido, placa, tell);
                                        LocalBroadcastManager.getInstance(PerfilActivity.this).sendBroadcast(new Intent("data_changed"));
                                        dialog.dismiss();
                                    }else {
                                        dialog.dismiss();
                                    }

                                } catch (JSONException e1) {
                                    Log.e("JSON", "Error parsing JSON", e1);
                                    dialog.dismiss();
                                }
                                Log.e(TAG, "response: " + response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("JSON", "Error parsing JSON", error);
                                dialog.dismiss();
                            }
                        }){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("idTios", id);
                        params.put("nome", nome);
                        params.put("email", email);
                        params.put("cpf", getCpf);
                        params.put("placa", placa);
                        params.put("tell", tell);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(PerfilActivity.this);
                requestQueue.add(stringRequest);
            }
        });
        dialog.show();
    }

    public void EditarCPF(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PerfilActivity.this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String nCPF = user.get(sessionManager.CPF);
        String nId = user.get(sessionManager.ID);
        int id = Integer.parseInt(nId);

        View mView = getLayoutInflater().inflate(R.layout.dialog_cpf, null);
        final EditText cpfE =  mView.findViewById(R.id.cpfD);
        Button mSim = mView.findViewById(R.id.btnSim);
        Button mNao = mView.findViewById(R.id.btnNao);

        alertDialog.setTitle("Editar CPF");

        alertDialog.setView(mView);
        final AlertDialog dialog = alertDialog.create();

        cpfE.setText(nCPF);

        mNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> user = sessionManager.getUserDetail();

                final String id = getId;
                final String nome = user.get(sessionManager.NAME);
                final String email = user.get(sessionManager.EMAIL);
                final String cpf = cpfE.getText().toString().trim();
                final String tell = user.get(sessionManager.TELL);
                final String placa = user.get(sessionManager.PLACA);
                final String apelido = user.get(sessionManager.APELIDO);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //progess.setVisibility(View.GONE);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    //boolean success = jsonObject.getBoolean("success");
                                    String success = jsonObject.getString("success");

                                    if (success.equals("OK")){
                                        sessionManager.createSession(id, nome, email, cpf, apelido, placa, tell);
                                        LocalBroadcastManager.getInstance(PerfilActivity.this).sendBroadcast(new Intent("data_changed"));
                                        dialog.dismiss();
                                    }else {
                                        dialog.dismiss();
                                    }

                                } catch (JSONException e1) {
                                    Log.e("JSON", "Error parsing JSON", e1);
                                    dialog.dismiss();
                                }
                                Log.e(TAG, "response: " + response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("JSON", "Error parsing JSON", error);
                                dialog.dismiss();
                            }
                        }){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("idTios", getId);
                        params.put("nome", nome);
                        params.put("email", email);
                        params.put("cpf", cpf);
                        params.put("placa", placa);
                        params.put("tell", tell);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(PerfilActivity.this);
                requestQueue.add(stringRequest);
            }
        });
        dialog.show();
    }

    public void EditarEmail(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PerfilActivity.this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String nEmail = user.get(sessionManager.EMAIL);
        String nId = user.get(sessionManager.ID);

        View mView = getLayoutInflater().inflate(R.layout.dialog_email, null);
        final EditText emailE = (EditText) mView.findViewById(R.id.emailD);
        Button mSim = (Button) mView.findViewById(R.id.btnSim);
        Button mNao = (Button) mView.findViewById(R.id.btnNao);

        alertDialog.setTitle("Editar CPF");

        alertDialog.setView(mView);
        final AlertDialog dialog = alertDialog.create();

        emailE.setText(nEmail);

        mNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> user = sessionManager.getUserDetail();

                final String id = getId;
                final String nome = user.get(sessionManager.NAME);
                final String email = emailE.getText().toString().trim();
                final String cpf = user.get(sessionManager.CPF);
                final String tell = user.get(sessionManager.TELL);
                final String placa = user.get(sessionManager.PLACA);
                final String apelido = user.get(sessionManager.APELIDO);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //progess.setVisibility(View.GONE);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    //boolean success = jsonObject.getBoolean("success");
                                    String success = jsonObject.getString("success");

                                    if (success.equals("OK")){
                                        sessionManager.createSession(id, nome, email, cpf, apelido, placa, tell);
                                        LocalBroadcastManager.getInstance(PerfilActivity.this).sendBroadcast(new Intent("data_changed"));
                                        dialog.dismiss();
                                    }else {
                                        dialog.dismiss();
                                    }

                                } catch (JSONException e1) {
                                    Log.e("JSON", "Error parsing JSON", e1);
                                    dialog.dismiss();
                                }
                                Log.e(TAG, "response: " + response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("JSON", "Error parsing JSON", error);
                                dialog.dismiss();
                            }
                        }){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("idTios", getId);
                        params.put("nome", nome);
                        params.put("email", email);
                        params.put("cpf", getCpf);
                        params.put("placa", placa);
                        params.put("tell", tell);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(PerfilActivity.this);
                requestQueue.add(stringRequest);
            }
        });
        dialog.show();
    }

    public void EditarPlaca(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PerfilActivity.this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String nPlaca = user.get(sessionManager.PLACA);

        View mView = getLayoutInflater().inflate(R.layout.dialog_placa, null);
        final EditText placaE = (EditText) mView.findViewById(R.id.nomeD);
        Button mSim = (Button) mView.findViewById(R.id.btnSim);
        Button mNao = (Button) mView.findViewById(R.id.btnNao);

        alertDialog.setTitle("Editar Placa");

        alertDialog.setView(mView);
        final AlertDialog dialog = alertDialog.create();

        placaE.setText(nPlaca);

        mNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> user = sessionManager.getUserDetail();

                final String id = getId;
                final String nome = user.get(sessionManager.NAME);
                final String email = user.get(sessionManager.EMAIL);
                final String cpf = user.get(sessionManager.CPF);
                final String tell = user.get(sessionManager.TELL);
                final String placa = placaE.getText().toString().trim();
                final String apelido = user.get(sessionManager.APELIDO);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //progess.setVisibility(View.GONE);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    //boolean success = jsonObject.getBoolean("success");
                                    String success = jsonObject.getString("success");

                                    if (success.equals("OK")){
                                        sessionManager.createSession(id, nome, email, cpf, apelido, placa, tell);
                                        LocalBroadcastManager.getInstance(PerfilActivity.this).sendBroadcast(new Intent("data_changed"));
                                        dialog.dismiss();
                                    }else {
                                        dialog.dismiss();
                                    }

                                } catch (JSONException e1) {
                                    Log.e("JSON", "Error parsing JSON", e1);
                                    dialog.dismiss();
                                }
                                Log.e(TAG, "response: " + response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("JSON", "Error parsing JSON", error);
                                dialog.dismiss();
                            }
                        }){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("idTios", getId);
                        params.put("nome", nome);
                        params.put("email", email);
                        params.put("cpf", getCpf);
                        params.put("placa", placa);
                        params.put("tell", tell);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(PerfilActivity.this);
                requestQueue.add(stringRequest);
            }
        });
        dialog.show();
    }

    public void EditarTell(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PerfilActivity.this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String nTell = user.get(sessionManager.TELL);
        String nId = user.get(sessionManager.ID);

        View mView = getLayoutInflater().inflate(R.layout.dialog_edit_tell, null);
        final EditText tellE = (EditText) mView.findViewById(R.id.periodoD);
        Button mSim = (Button) mView.findViewById(R.id.btnSim);
        Button mNao = (Button) mView.findViewById(R.id.btnNao);

        alertDialog.setTitle("Editar Placa");

        alertDialog.setView(mView);
        final AlertDialog dialog = alertDialog.create();

        tellE.setText(nTell);

        mNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> user = sessionManager.getUserDetail();

                final String id = user.get(sessionManager.ID);
                final String nome = user.get(sessionManager.NAME);
                final String email = user.get(sessionManager.EMAIL);
                final String cpf = user.get(sessionManager.CPF);
                final String tell = tellE.getText().toString().trim();
                final String placa = user.get(sessionManager.PLACA);
                final String apelido = user.get(sessionManager.APELIDO);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //progess.setVisibility(View.GONE);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    //boolean success = jsonObject.getBoolean("success");
                                    String success = jsonObject.getString("success");

                                    if (success.equals("OK")){
                                        sessionManager.createSession(id, nome, email, cpf, apelido, placa, tell);
                                        LocalBroadcastManager.getInstance(PerfilActivity.this).sendBroadcast(new Intent("data_changed"));
                                        dialog.dismiss();
                                    }else {
                                        dialog.dismiss();
                                    }

                                } catch (JSONException e1) {
                                    Log.e("JSON", "Error parsing JSON", e1);
                                    dialog.dismiss();
                                }
                                Log.e(TAG, "response: " + response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("JSON", "Error parsing JSON", error);
                                dialog.dismiss();
                            }
                        }){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("idTios", id);
                        params.put("nome", nome);
                        params.put("email", email);
                        params.put("cpf", getCpf);
                        params.put("placa", placa);
                        params.put("tell", tell);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(PerfilActivity.this);
                requestQueue.add(stringRequest);
            }
        });
        dialog.show();
    }


    public void alterarSenha(MenuItem item) {
        Intent intent = new Intent(PerfilActivity.this, AlterarSenhaActivity.class);
        startActivity(intent);
    }


    private BroadcastReceiver dataChangeReceiver1= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            perfilControler.readPerfil(tios, PerfilActivity.this);
        }
    };


    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }


}
