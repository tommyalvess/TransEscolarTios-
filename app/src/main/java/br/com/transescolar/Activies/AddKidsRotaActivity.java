package br.com.transescolar.Activies;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.appcompat.widget.SearchView;
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
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;

import java.util.HashMap;
import java.util.List;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.API.IKids;
import br.com.transescolar.Adapter.AddKidsAdapter;
import br.com.transescolar.model.Kids;
import br.com.transescolar.model.Rota;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddKidsRotaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Kids> contacts;
    private AddKidsAdapter adapter;
    private IKids apiInterface;
    ProgressBar progressBar;

    SessionManager sessionManager;
    String getId;

    Rota rota;
    ConstraintLayout constraintLayoutAddKids;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kids_rota);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Add Pasageiro na Rotas");     //Titulo para ser exibido na sua Action Bar em frente à seta

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        rota = (Rota) getIntent().getExtras().get("rota");

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        id = Integer.parseInt(getId);

        constraintLayoutAddKids = findViewById(R.id.constraintLayoutAddKids);
        progressBar = findViewById(R.id.progess);
        recyclerView = findViewById(R.id.addKidsList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        fetchRotas("users", id);

    }

    private void fetchRotas(String type, int id) {
        apiInterface = ApiClient.getApiClient().create(IKids.class);

        Call<List<Kids>> call = apiInterface.getKidsById(type, id);
        call.enqueue(new Callback<List<Kids>>() {
            @Override
            public void onResponse(Call<List<Kids>> call, Response<List<Kids>> response) {

                if (!response.body().isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    contacts = response.body();
                    adapter = new AddKidsAdapter(contacts, AddKidsRotaActivity.this);
                    adapter.setMode(Attributes.Mode.Single);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddKidsRotaActivity.this, "Nenhum passageiro encontrado!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Kids>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("Chamada", "Erro", t);
            }
        });
    }

    private void fetchAllRotas(String type, String key, int id) {
        apiInterface = ApiClient.getApiClient().create(IKids.class);

        Call<List<Kids>> call = apiInterface.getAllKids(type, key, id);
        call.enqueue(new Callback<List<Kids>>() {
            @Override
            public void onResponse(Call<List<Kids>> call, Response<List<Kids>> response) {

                if (!response.body().isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    contacts = response.body();
                    adapter = new AddKidsAdapter(contacts, AddKidsRotaActivity.this);
                    adapter.setMode(Attributes.Mode.Single);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
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
        inflater.inflate(R.menu.menu_escola, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchAllRotas("users", query, id);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetchAllRotas("users", newText, id);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, InfRotaActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("rota", rota);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
