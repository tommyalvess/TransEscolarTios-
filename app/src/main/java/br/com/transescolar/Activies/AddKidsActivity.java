package br.com.transescolar.Activies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.santalu.widget.MaskEditText;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.model.Escolas;
import br.com.transescolar.model.Kids;
import br.com.transescolar.model.Pais;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static br.com.transescolar.API.URL.PATH_TO_SERVER2;

public class AddKidsActivity extends AppCompatActivity implements  SearchableSpinner.OnItemSelectedListener {

    Spinner spinnerPeriodo;
    SearchableSpinner spinnerEscola;
    EditText editNomeT,endT;
    MaskEditText dtNasc, embarque, desembarque;
    boolean isUpdating = false;
    Kids kids;
    Button btnSaveCadastro;

    List<Escolas> spinnerEscolaData;
    String name [] = {"Manhã", "Tarde"};
    String record = "";

    ArrayAdapter<String> arrayAdapter;

    private RequestQueue queue;

    SessionManager sessionManager;
    String nome;
    String dtNas;
    String end, embarqueK, desembarqueK;
    int tio, getId, getIdPai;
    int escola;
    String periodo;

    ScrollView scrollCadas;

    public static List<String> lst1=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kids);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Spinner dados
        sessionManager = new SessionManager(this);
        Pais pais = (Pais) getIntent().getExtras().get("paisId");

        HashMap<String, String> user = sessionManager.getUserDetail();

        getIdPai = pais.getIdPais();
        getId = Integer.parseInt(user.get(sessionManager.ID));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadastrar Criança");

        spinnerEscola= findViewById(R.id.spinnerE);
        spinnerPeriodo = findViewById(R.id.spinnerP);

        editNomeT = findViewById(R.id.editNomeT);
        dtNasc = findViewById(R.id.dtNasc);
        endT = findViewById(R.id.end);
        embarque =findViewById(R.id.embarque);
        desembarque = findViewById(R.id.desembarque);
        btnSaveCadastro = findViewById(R.id.btnSaveCadastro);
        scrollCadas = findViewById(R.id.scrollCadas);

        editNomeT.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        endT.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        // Spinner periodo
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, name);
        spinnerPeriodo.setAdapter(arrayAdapter);

        spinnerPeriodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position)
                {
                    case 0:
                        record = "Manhã";
                        break;

                    case 1:
                        record = "Tarde";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSaveCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nomeA  = editNomeT.getText().toString().trim();
                final String dtNasA  = dtNasc.getText().toString();
                final String endA = endT.getText().toString().trim();
                final String embarqueKA = embarque.getText().toString();
                final String desembarqueKA = desembarque.getText().toString();
                final String periodoA = record.trim();

                final int escola  = spinnerEscola.getSelectedItemPosition()+1;

                if (nomeA.isEmpty()){
                    editNomeT.setError("Insira o seu nome");
                    editNomeT.requestFocus();
                    return;
                }

                if (dtNasA.isEmpty()){
                    dtNasc.setError("Insira aniversario!");
                    dtNasc.requestFocus();
                    return;
                }

                if (endA.isEmpty()){
                    endT.setError("Insira endereço!");
                    endT.requestFocus();
                    return;
                }

                if (embarqueKA.isEmpty()){
                    endT.setError("Insira o horário!");
                    endT.requestFocus();
                    return;
                }

                if (desembarqueKA.isEmpty()){
                    endT.setError("Insira o horário!");
                    endT.requestFocus();
                    return;
                }

                Call<ResponseBody> call = ApiClient
                        .getInstance()
                        .getApi()
                        .createkids(nomeA, dtNasA, endA, periodoA, embarqueKA, desembarqueKA, getId, escola, getIdPai);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                        String s = null;

                        try {
                            if (response.code() == 201) {
                                s = response.body().string();
                                Intent it = new Intent(AddKidsActivity.this, PassageirosActivity.class);
                                AddKidsActivity.this.startActivity(it);
                                finish();
                            }else {
                                s = response.errorBody().string();
                                Toast.makeText(AddKidsActivity.this, s, Toast.LENGTH_SHORT).show();

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (s != null){
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                Toast.makeText(AddKidsActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                            }catch (JSONException e){
                                e.printStackTrace();
                                Log.e("Call", "Erro", e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("Call", "Error", t);
                    }
                });

            }
        });

        spinnerPeriodo.setPrompt("Selecione um Periodo:");

        new GetDataEscola().execute();

        spinnerEscola.setTitle("Selecionar uma Escola");

        spinnerEscola.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class GetDataEscola extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
        @Override
        protected String doInBackground(String... params) {
            lst1=new ArrayList<String>();
            JsonArrayRequest movieReq = new JsonArrayRequest(PATH_TO_SERVER2,
                    new Response.Listener<JSONArray>()
                    {
                        @Override
                        public void onResponse(JSONArray response) {
                            for(int i=0;i<response.length();i++){
                                try {
                                    //Getting json object
                                    JSONObject json = response.getJSONObject(i);
                                    //Adding the name of the student to array list
                                    lst1.add(json.getString("nm_escola"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            ArrayAdapter adapter=new ArrayAdapter(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, lst1);
                            spinnerEscola.setAdapter(adapter);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
            //Creating a request queue
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            //Adding request to the queue
            requestQueue.add(movieReq);
            return null;
        }

    }

    private void registroPadrao() {

    }

}
