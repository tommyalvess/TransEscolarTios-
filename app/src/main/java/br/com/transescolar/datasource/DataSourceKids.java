package br.com.transescolar.datasource;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.API.IKids;
import br.com.transescolar.Activies.AddKidsActivity;
import br.com.transescolar.Activies.PaisActivity;
import br.com.transescolar.Activies.PassageirosActivity;
import br.com.transescolar.Adapter.KidsAdpter;
import br.com.transescolar.model.Kids;
import br.com.transescolar.model.Tios;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.transescolar.Activies.PassageirosActivity.progressBarPass;
import static br.com.transescolar.Activies.PassageirosActivity.recyclerView;

public class DataSourceKids {
    private List<Kids> kids;
    private KidsAdpter kidsAdpter;
    private IKids iKids;

    //TODO: Create
    public boolean inserirKids(Kids objKids, Context context){

        Call<ResponseBody> call = ApiClient
                .getInstance()
                .getApiK()
                .createkids(objKids.getNome(), objKids.getDt_nas(), objKids.getEnd_principal(), objKids.getPeriodo(),
                        objKids.getEmbarque(), objKids.getDesembarque(), String.valueOf(objKids.getIdTios()), objKids.getNm_escola(), objKids.getNm_pais());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                String s = null;

                try {
                    if (response.code() == 201) {
                        s = response.body().string();
                        Intent it = new Intent(context, PaisActivity.class);
                        context.startActivity(it);

                    }else {
                        s = response.errorBody().string();
                        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (s != null){
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    }catch (JSONException e){
                        e.printStackTrace();
                        Log.e("Call", "Erro", e);
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

    //TODO:Read
    public void fetchKid(String type, int id, final Context context) {
        iKids = ApiClient.getApiClient().create(IKids.class);

        Call<List<Kids>> call = iKids.getKidsById(type, id);
        call.enqueue(new Callback<List<Kids>>() {
            @Override
            public void onResponse(Call<List<Kids>> call, Response<List<Kids>> response) {

                if (!response.body().isEmpty()){
                    progressBarPass.setVisibility(View.GONE);
                    kids = response.body();
                    kidsAdpter = new KidsAdpter(kids, context);
                    recyclerView.setAdapter(kidsAdpter);
                    kidsAdpter.notifyDataSetChanged();
                }else {
                    progressBarPass.setVisibility(View.GONE);
                    Toast.makeText(context, "Nenhum passageiro encontrado!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<List<Kids>> call, Throwable t) {
                progressBarPass.setVisibility(View.GONE);
                Log.e("Chamada", "Erro", t);
            }
        });
    }

    public void fetchAllKid(String type, String key, int id, final Context context) {
        iKids = ApiClient.getApiClient().create(IKids.class);

        Call<List<Kids>> call = iKids.getAllKids(type, key, id);
        call.enqueue(new Callback<List<Kids>>() {
            @Override
            public void onResponse(Call<List<Kids>> call, Response<List<Kids>> response) {

                if (!response.body().isEmpty()){
                    progressBarPass.setVisibility(View.GONE);
                    kids = response.body();
                    kidsAdpter = new KidsAdpter(kids, context);
                    recyclerView.setAdapter(kidsAdpter);
                    kidsAdpter.notifyDataSetChanged();
                }else {
                    progressBarPass.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Kids>> call, Throwable t) {
                progressBarPass.setVisibility(View.GONE);
                Toast.makeText(context, "Opss! Algo deu errado!", Toast.LENGTH_SHORT).show();
                Log.e("Chamada", "Erro", t);
            }
        });
    }
}
