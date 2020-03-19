package br.com.transescolar.datasource;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.API.IEscolas;
import br.com.transescolar.Adapter.EscolaAdapter;
import br.com.transescolar.model.Escolas;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.transescolar.Activies.EscolasActivity.progressBarEs;
import static br.com.transescolar.Activies.EscolasActivity.recyclerView;

public class DataSourceEscola {
    private List<Escolas> contacts;
    private EscolaAdapter adapter;
    private IEscolas apiInterface;
    private Escolas escolas;

    public void fetchEscolas(String type, String key, final Context context) {

        apiInterface = ApiClient.getApiClient().create(IEscolas.class);

        Call<List<Escolas>> call = apiInterface.getEscolas(type, key);
        call.enqueue(new Callback<List<Escolas>>() {
            @Override
            public void onResponse(Call<List<Escolas>> call, Response<List<Escolas>> response) {
                progressBarEs.setVisibility(View.GONE);
                contacts = response.body();
                adapter = new EscolaAdapter(contacts, context);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Escolas>> call, Throwable t) {
                progressBarEs.setVisibility(View.GONE);
                Toast.makeText(context, "Opsss! Algo deu errado.", Toast.LENGTH_SHORT).show();
                Log.e("Chamada", "Erro", t);
            }
        });
    }


}
