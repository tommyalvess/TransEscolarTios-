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
import br.com.transescolar.controler.KidsControler;
import br.com.transescolar.model.Escolas;
import br.com.transescolar.model.Kids;
import br.com.transescolar.model.Pais;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import br.com.transescolar.model.Tios;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static br.com.transescolar.API.URL.PATH_TO_SERVER2;

public class AddKidsActivity extends AppCompatActivity implements  SearchableSpinner.OnItemSelectedListener {

    Spinner spinnerPeriodo;
    SearchableSpinner spinnerEscola;
    EditText editNomeK,endT;
    MaskEditText dtNasc, embarque, desembarque;
    boolean isUpdating = false;
    Kids objKids;
    Tios objTio;
    Button btnSaveCadastro;

    KidsControler kidsControler;


    List<Escolas> spinnerEscolaData;
    String name [] = {"Manhã", "Tarde"};
    String record = "";

    ArrayAdapter<String> arrayAdapter;

    private RequestQueue queue;

    SessionManager sessionManager;
    int getId, getIdPai;

    ScrollView scrollCadas;

    public static List<String> lst1=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kids);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sessionManager = new SessionManager(this);
        Pais pais = (Pais) getIntent().getExtras().get("paisId");

        HashMap<String, String> user = sessionManager.getUserDetail();

        objKids = new Kids();
        objTio = new Tios();

        kidsControler = new KidsControler();

        getIdPai = pais.getIdPais();
        getId = Integer.parseInt(user.get(sessionManager.ID));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadastrar Criança");

        spinnerEscola= findViewById(R.id.spinnerE);
        spinnerPeriodo = findViewById(R.id.spinnerP);

        editNomeK = findViewById(R.id.editNomeK);
        dtNasc = findViewById(R.id.dtNasc);
        endT = findViewById(R.id.end);
        embarque =findViewById(R.id.embarque);
        desembarque = findViewById(R.id.desembarque);
        btnSaveCadastro = findViewById(R.id.btnSaveCadastro);
        scrollCadas = findViewById(R.id.scrollCadas);

        editNomeK.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        endT.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        // Spinner periodo
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, name);
        spinnerPeriodo.setAdapter(arrayAdapter);

        spinnerPeriodo.setPrompt("Selecione um Periodo:");
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

        new GetDataEscola().execute();

        spinnerEscola.setTitle("Selecionar uma Escola");
        spinnerEscola.setOnItemSelectedListener(this);

        btnSaveCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popularDadosKids();
            }
        });

    }

    private void popularDadosKids() {
        //TODO: inserido dados nos atributos
        String nome = editNomeK.getText().toString().trim();
        objKids.setNome(editNomeK.getText().toString().trim());
        objKids.setDt_nas(dtNasc.getText().toString());
        objKids.setEnd_principal(endT.getText().toString().trim());
        objKids.setEmbarque(embarque.getText().toString());
        objKids.setDesembarque(desembarque.getText().toString());
        objKids.setPeriodo(record.trim());
        objKids.setNm_escola(String.valueOf(spinnerEscola.getSelectedItemPosition()+1));
        objKids.setNm_pais(String.valueOf(getIdPai));
        objKids.setIdTios(getId);

        //TODO: Validação simples do formulçario.
        if (objKids.getNm_escola().isEmpty()){
            editNomeK.setError("Insira o seu nome");
            editNomeK.requestFocus();
            return;
        } else if (objKids.getDt_nas().isEmpty()){
            dtNasc.setError("Insira aniversario!");
            dtNasc.requestFocus();
            return;
        } else if (objKids.getEnd_principal().isEmpty()){
            endT.setError("Insira endereço!");
            endT.requestFocus();
            return;
        } else if (objKids.getEmbarque().isEmpty()){
            endT.setError("Insira o horário!");
            endT.requestFocus();
            return;
        } else if (objKids.getDesembarque().isEmpty()){
            endT.setError("Insira o horário!");
            endT.requestFocus();
            return;
        } else {
            //TODO: conexão com o controler
            kidsControler.salvarKids(objKids, AddKidsActivity.this);
        }

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

}
