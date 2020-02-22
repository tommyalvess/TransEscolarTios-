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

import java.util.HashMap;
import java.util.List;


import br.com.transescolar.API.ApiClient;
import br.com.transescolar.API.IKids;
import br.com.transescolar.Adapter.KidsAdpter;
import br.com.transescolar.Conexao.NetworkChangeReceiver3;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.model.Kids;
import br.com.transescolar.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassageirosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Kids> kids;
    private KidsAdpter kidsAdpter;
    private IKids iKids;
    ProgressBar progressBar;
    SessionManager sessionManager;
    String getId;
    int id;
    static RelativeLayout relativeLayoutPass;
    private NetworkChangeReceiver3 mNetworkReceiver;
    static Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passageiros);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Passageiros");     //Titulo para ser exibido na sua Action Bar em frente à seta

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        relativeLayoutPass = findViewById(R.id.relativeLayoutPass);

        progressBar = findViewById(R.id.progess);
        recyclerView = findViewById(R.id.passList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        mNetworkReceiver = new NetworkChangeReceiver3();
        registerNetworkBroadcastForNougat();

        id = Integer.parseInt(getId);

        //fetchKids();
        fetchKid("users", id);

    }

    private void fetchKid(String type, int id) {
        iKids = ApiClient.getApiClient().create(IKids.class);

        Call<List<Kids>> call = iKids.getKidsById(type, id);
        call.enqueue(new Callback<List<Kids>>() {
            @Override
            public void onResponse(Call<List<Kids>> call, Response<List<Kids>> response) {

                if (!response.body().isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    kids = response.body();
                    kidsAdpter = new KidsAdpter(kids, PassageirosActivity.this);
                    recyclerView.setAdapter(kidsAdpter);
                    kidsAdpter.notifyDataSetChanged();
                }else {
                    progressBar.setVisibility(View.GONE);
//                    final Snackbar snackbar = showSnackbar(relativeLayoutPass, Snackbar.LENGTH_LONG, PassageirosActivity.this);
//                    snackbar.show();
//                    View view = snackbar.getView();
//                    TextView tv = (TextView) view.findViewById(R.id.textSnack);
//                    tv.setText("Nenhum passageiro encontrado!");
                }


            }

            @Override
            public void onFailure(Call<List<Kids>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("Chamada", "Erro", t);
            }
        });
    }

    private void fetchAllKid(String type, String key, int id) {
        iKids = ApiClient.getApiClient().create(IKids.class);

        Call<List<Kids>> call = iKids.getAllKids(type, key, id);
        call.enqueue(new Callback<List<Kids>>() {
            @Override
            public void onResponse(Call<List<Kids>> call, Response<List<Kids>> response) {

                if (!response.body().isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    kids = response.body();
                    kidsAdpter = new KidsAdpter(kids, PassageirosActivity.this);
                    recyclerView.setAdapter(kidsAdpter);
                    kidsAdpter.notifyDataSetChanged();
                }else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Kids>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("Chamada", "Erro", t);
            }
        });
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
                fetchAllKid("users", query, id);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetchAllKid("users", newText, id);
                return false;
            }
        });
        return true;
    }

    public static void dialogPas(boolean value, final Context context){

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
            snackbar = showSnackbar(relativeLayoutPass, Snackbar.LENGTH_INDEFINITE, context);
            snackbar.show();
            snackbar.dismiss();
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
        snackbar.getView().setBackgroundResource(R.color.ColorBGThema);
        snackbar.setActionTextColor(Color.BLACK);
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

    public void Add(MenuItem item) {
    }
}
