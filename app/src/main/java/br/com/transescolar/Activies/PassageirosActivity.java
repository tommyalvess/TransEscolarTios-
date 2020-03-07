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
import android.view.MenuItem;
import android.view.View;

import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import br.com.transescolar.API.ApiClient;
import br.com.transescolar.API.IKids;
import br.com.transescolar.Adapter.KidsAdpter;
import br.com.transescolar.Conexao.NetworkChangeReceiver3;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.controler.PassageirosControler;
import br.com.transescolar.model.Kids;
import br.com.transescolar.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.transescolar.controler.HomeControler.showSnackbar;

public class PassageirosActivity extends AppCompatActivity {

    public static RecyclerView recyclerView;
    public static ProgressBar progressBarPass;
    SessionManager sessionManager;
    String getId;
    int id;
    public static RelativeLayout relativeLayoutPass;
    private NetworkChangeReceiver3 mNetworkReceiver;
    public static Snackbar snackbar;

    private List<Kids> kids = new ArrayList<>();
    private KidsAdpter kidsAdpter;
    private IKids iKids;

    PassageirosControler passageirosControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passageiros);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Passageiros");     //Titulo para ser exibido na sua Action Bar em frente à seta

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        passageirosControler = new PassageirosControler();

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        relativeLayoutPass = findViewById(R.id.relativeLayoutPass);

        progressBarPass = findViewById(R.id.progess);
        recyclerView = findViewById(R.id.passList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        mNetworkReceiver = new NetworkChangeReceiver3();
        registerNetworkBroadcastForNougat();

        id = Integer.parseInt(getId);

        passageirosControler.readKids("users", id, PassageirosActivity.this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_passageiros, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                passageirosControler.readKidsBy("users", query, id, PassageirosActivity.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                passageirosControler.readKidsBy("users", newText, id, PassageirosActivity.this);
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

}
