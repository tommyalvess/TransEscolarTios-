package br.com.transescolar.Activies;

import android.app.SearchManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.List;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.API.IEscolas;
import br.com.transescolar.Adapter.EscolaAdapter;
import br.com.transescolar.controler.EscolaControler;
import br.com.transescolar.model.Escolas;
import br.com.transescolar.Conexao.NetworkChangeReceiver4;
import br.com.transescolar.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EscolasActivity extends AppCompatActivity {

    public static RecyclerView recyclerView;
    private List<Escolas> contacts;
    private EscolaAdapter adapter;
    private IEscolas apiInterface;
    public static ProgressBar progressBarEs;
    private NetworkChangeReceiver4 mNetworkReceiver;
    public static Snackbar snackbar;
    public static RelativeLayout relativeLayoutEs;

    EscolaControler escolaControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_escolas);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
            getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
            getSupportActionBar().setTitle("Escolas");     //Titulo para ser exibido na sua Action Bar em frente à seta

            escolaControler = new EscolaControler();

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            mNetworkReceiver = new NetworkChangeReceiver4();
            registerNetworkBroadcastForNougat();

            relativeLayoutEs = findViewById(R.id.relativeLayoutEs);
            progressBarEs = findViewById(R.id.progess);
            recyclerView = findViewById(R.id.escolaList);
            GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);

            escolaControler.readEscola("users", "", this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_escola, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                escolaControler.readEscola("users", query, EscolasActivity.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                escolaControler.readEscola("users", newText, EscolasActivity.this);
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


}// fim da Activity
