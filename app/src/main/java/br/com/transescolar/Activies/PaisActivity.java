package br.com.transescolar.Activies;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
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

import com.daimajia.swipe.util.Attributes;

import java.util.HashMap;
import java.util.List;

import br.com.transescolar.API.IPais;
import br.com.transescolar.Adapter.PaiAdapter;
import br.com.transescolar.Conexao.NetworkChangeReceiver6;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.model.Pais;
import br.com.transescolar.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PaisActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Pais> pais;
    private PaiAdapter paiAdapter;
    private IPais iPai;
    ProgressBar progressBar;
    SessionManager sessionManager;
    String getId;
    private String id;
    static Snackbar snackbar;
    private NetworkChangeReceiver6 mNetworkReceiver;
    static RelativeLayout relativeLayoutPais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pais);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Pais");     //Titulo para ser exibido na sua Action Bar em frente à seta

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        progressBar = findViewById(R.id.progess);
        recyclerView = findViewById(R.id.paisList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        relativeLayoutPais = findViewById(R.id.relativeLayoutPais);

        mNetworkReceiver = new NetworkChangeReceiver6();
        registerNetworkBroadcastForNougat();

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        fetchAllPais("users", getId);

    }//onCreate

    private void fetchAllPais(String users, String id) {
        iPai = IPais.retrofit.create(IPais.class);

        final Call<List<Pais>> call = iPai.getTodosPais(users, id);

        call.enqueue(new Callback<List<Pais>>() {
            @Override
            public void onResponse(Call<List<Pais>> call, Response<List<Pais>> response) {

                if (!response.body().isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    pais = response.body();
                    paiAdapter = new PaiAdapter(pais, PaisActivity.this);
                    recyclerView.setAdapter(paiAdapter);
                    paiAdapter.setMode(Attributes.Mode.Single);
                    paiAdapter.notifyDataSetChanged();
                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PaisActivity.this, "Nenhum pai encontrado!", Toast.LENGTH_SHORT).show();
//                    snackbar = showSnackbar(relativeLayoutPais, Snackbar.LENGTH_LONG, PaisActivity.this);
//                    snackbar.show();
//                    View view = snackbar.getView();
//                    TextView tv = (TextView) view.findViewById(R.id.textSnack);
//                    tv.setText("Nenhum pai encontrado!");
                }

            }

            @Override
            public void onFailure(Call<List<Pais>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("Call", "carregar dados", t);

            }
        });
    }

    private void fetcPaisBy(String users, String query, String id) {
        iPai = IPais.retrofit.create(IPais.class);

        final Call<List<Pais>> call = iPai.getPaisBy(users,query, id);

        call.enqueue(new Callback<List<Pais>>() {
            @Override
            public void onResponse(Call<List<Pais>> call, Response<List<Pais>> response) {

                if (!response.body().isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    pais = response.body();
                    paiAdapter = new PaiAdapter(pais, PaisActivity.this);
                    recyclerView.setAdapter(paiAdapter);
                    paiAdapter.setMode(Attributes.Mode.Single);
                    paiAdapter.notifyDataSetChanged();
                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PaisActivity.this, "Nenhum pai cadastrado!", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<List<Pais>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("Call", "carregar dados", t);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pais, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetcPaisBy("users", query, getId);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetcPaisBy("users", newText, getId);
                return false;
            }
        });
        return true;
    }


    private void fetchPais() {

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);
        int id = Integer.parseInt(getId);
        iPai = IPais.retrofit.create(IPais.class);

        final Call<List<Pais>> call = iPai.getPais(id);

        call.enqueue(new Callback<List<Pais>>() {
            @Override
            public void onResponse(Call<List<Pais>> call, Response<List<Pais>> response) {

                if (!response.body().isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    pais = response.body();
                    paiAdapter = new PaiAdapter(pais, PaisActivity.this);
                    recyclerView.setAdapter(paiAdapter);
                    paiAdapter.setMode(Attributes.Mode.Single);
                    paiAdapter.notifyDataSetChanged();
                }else {
                    progressBar.setVisibility(View.GONE);
                    snackbar = showSnackbar(relativeLayoutPais, Snackbar.LENGTH_LONG, PaisActivity.this);
                    snackbar.show();
                    View view = snackbar.getView();
                    TextView tv = (TextView) view.findViewById(R.id.textSnack);
                    tv.setText("Nenhum pai encontrado!");
                }

            }

            @Override
            public void onFailure(Call<List<Pais>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("Call", "carregar dados", t);

            }
        });
    }

    public static void dialogPai(boolean value, final Context context){

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
            snackbar = showSnackbar(relativeLayoutPais, Snackbar.LENGTH_SHORT, context);
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
        snackbar.getView().setBackgroundResource(R.color.ColorBGThema);
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

    public void addPais(MenuItem item) {
        Intent intent = new Intent(PaisActivity.this, AddPaisActivity.class);
        startActivity(intent);
    }
}
