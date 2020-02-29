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

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class RotaActivity extends AppCompatActivity {

    public static RecyclerView recyclerView;
    private List<Rota> contacts;
    private RotaAdapter adapter;
    private IRota apiInterface;
    public static ProgressBar progressBarR;

    private NetworkChangeReceiver5 mNetworkReceiver;
    public static Snackbar snackbar;
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

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        rotaControler.fetchRotas("users", "", getId, this);

    }// OnCreat


    public void Add(MenuItem item) {
        Intent it = new Intent(RotaActivity.this, CadastrarRotaActivity.class);
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
                rotaControler.fetchRotas("users", query, getId, RotaActivity.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                rotaControler.fetchRotas("users", newText, getId, RotaActivity.this);
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

}// Class
