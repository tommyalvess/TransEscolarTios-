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
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.transescolar.API.URL.URL_COUNTTIOSKIDS;
import static br.com.transescolar.API.URL.URL_COUNTTIOSPAIS;
import static br.com.transescolar.API.URL.URL_EDIT;
import static br.com.transescolar.API.URL.URL_READ;
import static br.com.transescolar.API.URL.URL_UPLOAD;

public class PerfilActivity extends AppCompatActivity {

    private static final String TAG = PerfilActivity.class.getSimpleName();
    static TextView textNomeU;
    static TextView textEmailU;
    static TextView textCpfU;
    static TextView texPlacaU;
    static TextView textTellU;
    static TextView tv_name;
    static TextView txtCountPais;
    static TextView txtCountKids;
    static CircleImageView imgPerfilT;
    static String getId;
    static String getCpf, img, getApelido, getNome;
    private Bitmap bitmap;

    static SessionManager sessionManager;

    static RequestOptions cropOptions;

    boolean conectado;

    private final Handler handler = new Handler();

    private NetworkChangeReceiver2 mNetworkReceiver;

    static Snackbar snackbar;
    static RelativeLayout relativeLayoutPer;
    DocumentReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);
        getCpf = user.get(sessionManager.CPF);
        img = user.get(sessionManager.IMG);
        getNome = user.get(sessionManager.NAME);
        getApelido = user.get(sessionManager.APELIDO);

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

        getUserDetail(this);
        countPais(this);
        countKids(this);

    }// onCreate

    //Contar pais
    private static void countPais(Context context){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_COUNTTIOSPAIS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Mensagem GetUserDetail", response.toString());
                        try {
                            JSONObject json = new JSONObject(response);
                            String success = json.getString("error");
                            JSONArray nameArray = json.names();
                            JSONArray valArray = json.toJSONArray( nameArray );
                            if (success.equals("OK")){
                                for (int i = 0; i < valArray.length(); i++) {
                                    JSONObject object = valArray.getJSONObject(i);
                                    String nome = object.getString("nome").trim();

                                    txtCountPais.setText(nome);
                                }
                            }
                        }catch ( JSONException e ) {
                            Log.e("JSON", "Error parsing JSON", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", "Error", error);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("idTios", getId);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private static void countKids(Context context){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_COUNTTIOSKIDS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Mensagem GetUserDetail", response.toString());
                        try {
                            JSONObject json = new JSONObject(response);
                            String success = json.getString("error");
                            JSONArray nameArray = json.names();
                            JSONArray valArray = json.toJSONArray( nameArray );
                            if (success.equals("OK")){
                                for (int i = 0; i < valArray.length(); i++) {
                                    JSONObject object = valArray.getJSONObject(i);
                                    String nome = object.getString("nome").trim();

                                    txtCountKids.setText(nome);
                                }
                            }
                        }catch ( JSONException e ) {
                            Log.e("JSON", "Error parsing JSON", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", "Error", error);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("idTios", getId);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    //Dialo para sair da tele
    private void dialogExit(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_text, null);
        final TextView nomeE = mView.findViewById(R.id.nomeD);
        nomeE.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        Button mSim = mView.findViewById(R.id.btnSim);
        Button mNao = mView.findViewById(R.id.btnNao);

        alertDialog.setView(mView);
        final AlertDialog dialog = alertDialog.create();

        nomeE.setText("Você deseja realmente sair?");
        mSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        mNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //Pegar inf da sessão
    private static void getUserDetailSessão(Context context){
        HashMap<String, String> user = sessionManager.getUserDetail();
        String nNome = user.get(sessionManager.NAME);
        String nEmail = user.get(sessionManager.EMAIL);
        String nCpf = user.get(sessionManager.CPF);
        String nApelido = user.get(sessionManager.APELIDO);
        String nPlaca = user.get(sessionManager.PLACA);
        String nTell = user.get(sessionManager.TELL);
        String nIMG = user.get(sessionManager.IMG);

        textNomeU.setText(nNome);
        textEmailU.setText(nEmail);
        textCpfU.setText(nCpf);
        tv_name.setText(nApelido);
        texPlacaU.setText(nPlaca);
        textTellU.setText(nTell);

        Glide.with(context).load(nIMG).apply(cropOptions).into(imgPerfilT);


    }

    //Pegar as infs do BD
    private static void getUserDetail(final Context context){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Mensagem GetUserDetail", response.toString());
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray nameArray = json.names();
                            JSONArray valArray = json.toJSONArray( nameArray );
                            if (!json.optBoolean("falhou")){
                                for (int i = 0; i < valArray.length(); i++) {
                                    JSONObject object = valArray.getJSONObject(i);
                                    String id = object.getString("idTios").trim();
                                    String nome = object.getString("nome").trim();
                                    String email = object.getString("email").trim();
                                    String cpf = object.getString("cpf").trim();
                                    String apelido = object.getString("apelido").trim();
                                    String placa = object.getString("placa").trim();
                                    String tell = object.getString("tell").trim();
                                    String strImage = object.getString("img").trim();

                                    textNomeU.setText(nome);
                                    textEmailU.setText(email);
                                    textCpfU.setText(cpf);
                                    tv_name.setText(apelido);
                                    texPlacaU.setText(placa);
                                    textTellU.setText(tell);
                                    Glide.with(context).load(strImage).apply(cropOptions).into(imgPerfilT);

                                }
                            }
                        }catch ( JSONException e ) {
                            Log.e("JSON", "Error parsing JSON", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", "Error", error);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("idTios", getId);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
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

    public  boolean verificaConexao() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            conectado = true;
        }
        else
            conectado = false;
        return conectado;
    }

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

            UploadPicture(getId, getCpf, getStringImage(bitmap));

        }
    }

    //Fazer o Upload da foto
    private void UploadPicture(final String id, final String cpf, final String photo) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Mensagem Upload", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("OK")){
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Erro", "Upload", e);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idTios", id);
                params.put("cpf", cpf);
                params.put("img", photo);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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

                final String id = getId;
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

    public void EditarCPF(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PerfilActivity.this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String nCPF = user.get(sessionManager.CPF);
        String nId = user.get(sessionManager.ID);
        int id = Integer.parseInt(nId);

        View mView = getLayoutInflater().inflate(R.layout.dialog_cpf, null);
        final EditText cpfE = (EditText) mView.findViewById(R.id.cpfD);
        Button mSim = (Button) mView.findViewById(R.id.btnSim);
        Button mNao = (Button) mView.findViewById(R.id.btnNao);

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
        String nId = user.get(sessionManager.ID);

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

                final String id = getId;
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

    public void alterarSenha(MenuItem item) {
        Intent intent = new Intent(PerfilActivity.this, AlterarSenhaActivity.class);
        startActivity(intent);
    }


    private BroadcastReceiver dataChangeReceiver1= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // update your listview
            getUserDetail(context);
        }
    };

    public static void dialogP(boolean value, final Context context){

        if(value){
            snackbar.dismiss();
            getUserDetail(context);
            countPais(context);
            countKids(context);

            Handler handler = new Handler();
            Runnable delayrunnable = new Runnable() {
                @Override
                public void run() {
                    snackbar.dismiss();
                }
            };
            handler.postDelayed(delayrunnable, 300);
        }else {
            getUserDetailSessão(context);
            txtCountPais.setText("0");
            txtCountKids.setText("0");
            snackbar = showSnackbar(relativeLayoutPer, Snackbar.LENGTH_INDEFINITE, context);
            snackbar.show();
            View view = snackbar.getView();
            TextView tv = (TextView) view.findViewById(R.id.textSnack);
            tv.setText("Sem conexão a internet!");
        }
    }


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

    private static Snackbar showSnackbar(RelativeLayout coordinatorLayout, int duration, Context context) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "", duration);
        // 15 is margin from all the sides for snackbar
        int marginFromSides = 15;

        float height = 100;

        //inflate view
        LayoutInflater inflater = (LayoutInflater)context.getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View snackView = inflater.inflate(R.layout.snackbar_layout, null);

        // White background
        snackbar.getView().setBackgroundColor(Color.WHITE);
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
