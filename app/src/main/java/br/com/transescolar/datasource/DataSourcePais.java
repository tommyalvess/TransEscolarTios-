package br.com.transescolar.datasource;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;

import java.util.List;

import br.com.transescolar.API.IPais;
import br.com.transescolar.Activies.PaisActivity;
import br.com.transescolar.Adapter.PaiAdapter;
import br.com.transescolar.model.Pais;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.transescolar.Activies.PaisActivity.progressBar;
import static br.com.transescolar.Activies.PaisActivity.recyclerView;

public class DataSourcePais {

    private List<Pais> pais;
    private PaiAdapter paiAdapter;
    private IPais iPai;
    //TODO:Create
    public boolean inserirDados(){
        return true;
    }

    //TODO: Read
    public void fetchAllPais(String users, String id, Context context) {
        iPai = IPais.retrofit.create(IPais.class);

        final Call<List<Pais>> call = iPai.getTodosPais(users, id);

        call.enqueue(new Callback<List<Pais>>() {
            @Override
            public void onResponse(Call<List<Pais>> call, Response<List<Pais>> response) {

                if (!response.body().isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    pais = response.body();
                    paiAdapter = new PaiAdapter(pais, context);
                    recyclerView.setAdapter(paiAdapter);
                    paiAdapter.setMode(Attributes.Mode.Single);
                    paiAdapter.notifyDataSetChanged();
                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, "Nenhum pai encontrado!", Toast.LENGTH_SHORT).show();
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

    public void fetcPaisBy(String users, String query, String id, Context context) {
        iPai = IPais.retrofit.create(IPais.class);

        final Call<List<Pais>> call = iPai.getPaisBy(users,query, id);

        call.enqueue(new Callback<List<Pais>>() {
            @Override
            public void onResponse(Call<List<Pais>> call, Response<List<Pais>> response) {

                if (!response.body().isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    pais = response.body();
                    paiAdapter = new PaiAdapter(pais, context);
                    recyclerView.setAdapter(paiAdapter);
                    paiAdapter.setMode(Attributes.Mode.Single);
                    paiAdapter.notifyDataSetChanged();
                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, "Nenhum pai cadastrado!", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<List<Pais>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("Call", "carregar dados", t);

            }
        });
    }

}
