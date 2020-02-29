package br.com.transescolar.controler;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.API.IEscolas;
import br.com.transescolar.Activies.EscolasActivity;
import br.com.transescolar.Adapter.EscolaAdapter;
import br.com.transescolar.R;
import br.com.transescolar.datasource.DataSourceEscola;
import br.com.transescolar.model.Escolas;
import br.com.transescolar.model.Rota;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.transescolar.Activies.EscolasActivity.progressBarEs;
import static br.com.transescolar.Activies.EscolasActivity.relativeLayoutEs;
import static br.com.transescolar.Activies.EscolasActivity.snackbar;
import static br.com.transescolar.Activies.EscolasActivity.recyclerView;
import static br.com.transescolar.controler.HomeControler.showSnackbar;

public class EscolaControler extends DataSourceEscola {

    private List<Escolas> contacts;
    private EscolaAdapter adapter;
    private IEscolas apiInterface;
    private Escolas escolas;

    public EscolaControler() {
        this.escolas = new Escolas();
    }

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
                Toast.makeText(context, "Nada Localizado!", Toast.LENGTH_SHORT).show();
                Log.e("Chamada", "Erro", t);
            }
        });
    }

    public static void dialogE(boolean value, final Context context){

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
            snackbar = showSnackbar(relativeLayoutEs, Snackbar.LENGTH_INDEFINITE, context);
            snackbar.show();
            View view = snackbar.getView();
            TextView tv = view.findViewById(R.id.textSnack);
            tv.setText("Sem conexão a internet!");
        }
    }
}
