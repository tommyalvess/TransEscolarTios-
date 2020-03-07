package br.com.transescolar.datasource;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.swipe.util.Attributes;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.API.IRota;
import br.com.transescolar.Activies.AddKidsActivity;
import br.com.transescolar.Activies.AddKidsRotaActivity;
import br.com.transescolar.Activies.CadastrarRotaActivity;
import br.com.transescolar.Activies.LoginActivity;
import br.com.transescolar.Activies.PassageirosActivity;
import br.com.transescolar.Activies.RotaActivity;
import br.com.transescolar.Adapter.RotaAdapter;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;
import br.com.transescolar.controler.TioControler;
import br.com.transescolar.model.Kids;
import br.com.transescolar.model.Rota;
import br.com.transescolar.model.Tios;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.transescolar.API.URL.URL_REGIST_ROTA;
import static br.com.transescolar.Activies.CadastrarRotaActivity.progressBar;
import static br.com.transescolar.Activies.RotaActivity.progressBarR;
import static br.com.transescolar.Activies.RotaActivity.recyclerView;

public class DataSourceRota {

    private List<Rota> contacts;
    private RotaAdapter adapter;
    private IRota apiInterface;

    //TODO: Create
    public boolean inserirDadosRota(final Rota objRota, final Tios objTios, final Context context){
        //TODO: Adicionar Tio
        Call<ResponseBody> call = ApiClient
                .getInstance()
                .getApiR()
                .createRotas(objRota.getNm_rota(), objRota.getHora(), objRota.getDias(), objTios.getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String s = null;

                try {
                    if (response.code() == 201) {

                        s = response.body().string();
                        Intent intent = new Intent(context, RotaActivity.class);
                        context.startActivity(intent);

                    }else {

                        s = response.errorBody().string();
                        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                        Log.e("Chamada", s);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (s != null){
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        Toast.makeText(context, s, Toast.LENGTH_LONG).show();

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Call", "Error", t);
            }
        });

        return true;
    }

    public boolean inserirKidnaRota(final Rota objRota, final Kids objKids, final Context context){
        //TODO: Add Cirançã na rota
        Call<ResponseBody> call = ApiClient
                .getInstance()
                .getApiR()
                .createRotaHasKid(objRota.getId(), objKids.getIdKids());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String s = null;

                try {
                    if (response.code() == 201) {

                        s = response.body().string();
                        Intent intent = new Intent(context, AddKidsRotaActivity.class);
                        context.startActivity(intent);

                    }else {

                        s = response.errorBody().string();
                        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                        Log.e("Chamada", s);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (s != null){
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        Toast.makeText(context, s, Toast.LENGTH_LONG).show();

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Call", "Error", t);
            }
        });
        return true;
    }

    //TODO: Read
    public void fetchRotas(String type, String key, String id, final Context context) {
        apiInterface = ApiClient.getApiClient().create(IRota.class);

        Call<List<Rota>> call = apiInterface.getRotas(type, key, id);
        call.enqueue(new Callback<List<Rota>>() {
            @Override
            public void onResponse(Call<List<Rota>> call, Response<List<Rota>> response) {

                if (!response.body().isEmpty()){
                    progressBarR.setVisibility(View.GONE);
                    contacts = response.body();
                    adapter = new RotaAdapter(contacts, context);
                    adapter.setMode(Attributes.Mode.Single);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }else {
                    progressBarR.setVisibility(View.GONE);
                    Toast.makeText(context, "Nenhuma rota localizada!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<List<Rota>> call, Throwable t) {
                progressBarR.setVisibility(View.GONE);
                Toast.makeText(context, "Oppss! Algo deu errado!", Toast.LENGTH_LONG).show();
                Log.e("Chamada", "Erro", t);
            }
        });
    }


}
