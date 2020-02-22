package br.com.transescolar.Activies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.swipe.util.Attributes;

import java.util.List;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.API.IRota;
import br.com.transescolar.Adapter.RotaInfAdapter;
import br.com.transescolar.model.Kids;
import br.com.transescolar.model.Rota;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfRotaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Kids> contacts;
    private RotaInfAdapter adapter;
    private IRota apiInterface;
    ProgressBar progressBar;
    String getId;
    Rota rota;
    String embarcou,desembarcou;

    SessionManager sessionManager;

    Handler handler;

    ConstraintLayout constraintLayoutInfR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inf_rota);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true); //Ativar o botão

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        rota = (Rota) getIntent().getExtras().get("rota");
        getId = String.valueOf(rota.getId());

        sessionManager = new SessionManager(this);
        sessionManager.createSessionRota(getId);

        getSupportActionBar().setTitle(rota.getNm_rota()); //Titulo para ser exibido na sua Action Bar em frente à seta
        constraintLayoutInfR = findViewById(R.id.constraintLayoutInfR);
        progressBar = findViewById(R.id.progess);
        recyclerView = findViewById(R.id.rotaList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        fetchRotas("users", getId);

        IntentFilter inF1 = new IntentFilter("data_changed1");
        LocalBroadcastManager.getInstance(InfRotaActivity.this).registerReceiver(dataChangeReceiver1,inF1);

        handler = new Handler();
        doTheAutoRefresh();

    }

    private void fetchRotas(String type, String idRota) {
        apiInterface = ApiClient.getApiClient().create(IRota.class);

        Call<List<Kids>> call = apiInterface.getInfRotas(type, idRota);
        call.enqueue(new Callback<List<Kids>>() {
            @Override
            public void onResponse(Call<List<Kids>> call, Response<List<Kids>> response) {

                if (!response.body().isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    contacts = response.body();
                    adapter = new RotaInfAdapter(contacts, InfRotaActivity.this);
                    adapter.setMode(Attributes.Mode.Single);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }else {
                    final Snackbar snackbar = showSnackbar(constraintLayoutInfR, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    View view = snackbar.getView();
                    TextView tv = (TextView) view.findViewById(R.id.textSnack);
                    tv.setText("Nenhum passageiro adicinado!");
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_inf_rota, menu);
        return true;
    };

    public void Add(MenuItem item) {
        Intent it = new Intent(this, AddKidsRotaActivity.class);
        it.putExtra("rota", rota);
        startActivity(it);
    }

    private BroadcastReceiver dataChangeReceiver1= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // update your listview
            fetchRotas("users", getId);
        }
    };

    private void doTheAutoRefresh() {
        handler.postDelayed(new Runnable() {
            public void run() {
                LocalBroadcastManager.getInstance(InfRotaActivity.this).sendBroadcast(new Intent("data_changed1"));
                doTheAutoRefresh();
            }
        }, 60000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    private Snackbar showSnackbar(ConstraintLayout coordinatorLayout, int duration) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "", duration);
        // 15 is margin from all the sides for snackbar
        int marginFromSides = 15;

        float height = 100;

        //inflate view
        LayoutInflater inflater = (LayoutInflater)InfRotaActivity.this.getApplicationContext().getSystemService
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
}
