package br.com.transescolar.Activies;

import android.app.SearchManager;
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

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
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

import java.util.HashMap;
import java.util.List;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.API.IRota;
import br.com.transescolar.Adapter.RotaAdapter;
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
    private RecyclerView.LayoutManager layoutManager;
    private List<Rota> contacts;
    private RotaAdapter adapter;
    private IRota apiInterface;
    ProgressBar progressBar;
    View layout;

    private NetworkChangeReceiver5 mNetworkReceiver;
    static Snackbar snackbar;
    static ConstraintLayout constraintLayoutRota;

    public static SessionManager sessionManager;
    String getId;

    TioControler tioControler;
    Tios tios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rota);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Rotas");     //Titulo para ser exibido na sua Action Bar em frente à seta

        tioControler = new TioControler();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mNetworkReceiver = new NetworkChangeReceiver5();
        registerNetworkBroadcastForNougat();

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.toast_personalizado,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        constraintLayoutRota = findViewById(R.id.constraintLayoutRota);
        progressBar = findViewById(R.id.progess);
        recyclerView = findViewById(R.id.rotaList);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new DividerItemDecoration(RotaActivity.this, DividerItemDecoration.VERTICAL));

        fetchRotas("users", "", getId);


    }// OnCreat

    private void fetchRotas(String type, String key, String id) {
        apiInterface = ApiClient.getApiClient().create(IRota.class);

        Call<List<Rota>> call = apiInterface.getRotas(type, key, id);
        call.enqueue(new Callback<List<Rota>>() {
            @Override
            public void onResponse(Call<List<Rota>> call, Response<List<Rota>> response) {

                if (!response.body().isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    contacts = response.body();
                    adapter = new RotaAdapter(contacts, RotaActivity.this);
                    adapter.setMode(Attributes.Mode.Single);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RotaActivity.this, "Nenhuma rota localizada!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<List<Rota>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("Chamada", "Erro", t);
            }
        });
    }

    public void Add(MenuItem item) {
        Intent it = new Intent(this, CadastrarRotaActivity.class);
        startActivity(it);
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
                fetchRotas("users", query, getId);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetchRotas("users", newText, getId);
                return false;
            }
        });
        return true;
    }

    public static void dialogR(boolean value, final Context context){

        if(value){
            snackbar.dismiss();
            Handler handler = new Handler();
            Runnable delayrunnable = new Runnable() {
                @Override
                public void run() {
                    snackbar.dismiss();
                }
            };
            handler.postDelayed(delayrunnable, 300);
        }else {
            Toast.makeText(context, "Sem conexão a internet!", Toast.LENGTH_SHORT).show();
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

}// Class
