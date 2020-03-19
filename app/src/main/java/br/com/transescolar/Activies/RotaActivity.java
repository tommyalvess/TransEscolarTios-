package br.com.transescolar.Activies;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.API.IRota;
import br.com.transescolar.Adapter.RotaAdapter;
import br.com.transescolar.controler.RotaControler;
import br.com.transescolar.controler.TioControler;
import br.com.transescolar.model.Rota;
import br.com.transescolar.model.Tios;
import br.com.transescolar.Conexao.NetworkChangeReceiver5;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RotaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Rota> contacts = new ArrayList<>();
    private RotaAdapter adapter;
    private IRota apiInterface;
    public static ProgressBar progressBarR;

    private NetworkChangeReceiver5 mNetworkReceiver;
    public static ConstraintLayout constraintLayoutRota;

    public static SessionManager sessionManager;
    String getId;

    TioControler tioControler;
    RotaControler rotaControler;
    Tios tios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rota);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Rotas");     //Titulo para ser exibido na sua Action Bar em frente à seta

        tioControler = new TioControler();
        rotaControler = new RotaControler();
        tios = new Tios();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //TODO: Recebendo informação sobre conexão com a net.
        mNetworkReceiver = new NetworkChangeReceiver5();
        registerNetworkBroadcastForNougat();

        //TODO: Pegando o id da sessão
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        constraintLayoutRota = findViewById(R.id.constraintLayoutRota);
        progressBarR = findViewById(R.id.progess);
        recyclerView = findViewById(R.id.rotaList);

        fetchRotas("users", "", getId, RotaActivity.this);

        IntentFilter inF1 = new IntentFilter("data_changed");
        LocalBroadcastManager.getInstance(RotaActivity.this).registerReceiver(dataChangeReceiver1,inF1);

    }// OnCreat

    private BroadcastReceiver dataChangeReceiver1= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fetchRotas("users", "", getId, RotaActivity.this);
        }
    };

    public void Add(MenuItem item) {
        Intent it = new Intent(RotaActivity.this, CadastrarRotaActivity.class);
        startActivity(it);
    }

    public void fetchRotas(String type, String key, String id, final Context context) {
        apiInterface = ApiClient.getApiClient().create(IRota.class);
        Call<List<Rota>> call = apiInterface.getRotas(type, key, id);
        call.enqueue(new Callback<List<Rota>>() {
            @Override
            public void onResponse(Call<List<Rota>> call, Response<List<Rota>> response) {

                if (contacts != null) {
                    contacts.clear();
                }

                progressBarR.setVisibility(View.GONE);
                contacts = response.body();
                adapter = new RotaAdapter(context, contacts);
                adapter.setMode(Attributes.Mode.Single);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

//                if (!response.body().isEmpty()){
//                    progressBarR.setVisibility(View.GONE);
//                }else {
//                    progressBarR.setVisibility(View.GONE);
//                    Toast.makeText(context, "Nenhuma rota localizada!", Toast.LENGTH_LONG).show();
//                }

            }

            @Override
            public void onFailure(Call<List<Rota>> call, Throwable t) {
                progressBarR.setVisibility(View.GONE);
                Toast.makeText(context, "Oppss! Algo deu errado!", Toast.LENGTH_LONG).show();
                Log.e("Chamada", "Erro", t);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_itinerario, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_rota).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchRotas("users", query, getId, RotaActivity.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetchRotas("users", newText, getId, RotaActivity.this);
                return false;
            }
        });
        return true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Id correspondente ao botão Up/Home da actionbar
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(RotaActivity .this, upIntent)) {
                    //Se a atividade não faz parte do aplicativo, criamos uma nova tarefa
                    // para navegação com a pilha de volta sintetizada.
                    TaskStackBuilder.create(this)
                            // Adiciona todas atividades parentes na pilha de volta
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    //Se essa atividade faz parte da tarefa do app
                    //navegamos para seu parente logico.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.notifyDataSetChanged();
        contacts.remove(null);
    }
}// Class
