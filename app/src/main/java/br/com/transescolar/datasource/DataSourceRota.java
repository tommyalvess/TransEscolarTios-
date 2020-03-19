package br.com.transescolar.datasource;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.API.IRota;
import br.com.transescolar.Activies.AddKidsActivity;
import br.com.transescolar.Activies.AddKidsRotaActivity;
import br.com.transescolar.Activies.CadastrarRotaActivity;
import br.com.transescolar.Activies.EditRotaActivity;
import br.com.transescolar.Activies.LoginActivity;
import br.com.transescolar.Activies.PassageirosActivity;
import br.com.transescolar.Activies.RotaActivity;
import br.com.transescolar.Adapter.RotaAdapter;
import br.com.transescolar.Adapter.RotaInfAdapter;
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

import static br.com.transescolar.API.URL.URL_DELETA_KID_ROTA;
import static br.com.transescolar.API.URL.URL_DELETA_ROTA;
import static br.com.transescolar.API.URL.URL_DESEMBARQUE;
import static br.com.transescolar.API.URL.URL_EDIT_ROTA;
import static br.com.transescolar.API.URL.URL_EMBARQUE;
import static br.com.transescolar.API.URL.URL_REGIST_ROTA;
import static br.com.transescolar.Activies.EditRotaActivity.progressBar;
import static br.com.transescolar.Activies.EditRotaActivity.btnSalvarI;
import static br.com.transescolar.Activies.RotaActivity.progressBarR;
import static br.com.transescolar.controler.HomeControler.showToast;

public class DataSourceRota {

    private List<Rota> contacts = new ArrayList<>();
    private RotaAdapter adapter;
    private IRota apiInterface;
    boolean resp;

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

                    }else {

                        s = response.errorBody().string();
                        Toast.makeText(context, s, Toast.LENGTH_LONG).show();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (s != null){
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();

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
    public boolean fetchRotas(String type, String key, String id, final Context context) {
        apiInterface = ApiClient.getApiClient().create(IRota.class);

        Call<List<Rota>> call = apiInterface.getRotas(type, key, id);
        call.enqueue(new Callback<List<Rota>>() {
            @Override
            public void onResponse(Call<List<Rota>> call, Response<List<Rota>> response) {

                if (!response.body().isEmpty()){
                    progressBarR.setVisibility(View.GONE);
                    contacts = response.body();
                    adapter = new RotaAdapter(context, contacts);
                    adapter.setMode(Attributes.Mode.Single);
                    //recyclerView.setAdapter(adapter);
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
        return true;
    }

    //TODO: UpDate
    public boolean upDateRota(final Rota objRota, Context context){
        //TODO: Update Tio
        Call<ResponseBody> call = ApiClient
                .getInstance()
                .getApiR()
                .updaterota(objRota.getId(), objRota.getNm_rota(), objRota.getHora(), objRota.getDias());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String s = null;

                try {
                    if (response.code() == 200) {

                        s = response.body().string();

                    }else {

                        s = response.errorBody().string();
                        Toast.makeText(context, s, Toast.LENGTH_LONG).show();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (s != null){
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();

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

    public boolean upDateKidsStatusE(Kids objKids, Context context){
        //TODO: Update Kids Status
        Call<ResponseBody> call = ApiClient
                .getInstance()
                .getApiR()
                .updatestatusembarque(objKids.getIdKids(), objKids.getStatus());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String s = null;

                try {
                    if (response.code() == 200) {

                        s = response.body().string();
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("data_changed1"));

                    }else {

                        s = response.errorBody().string();
                        Toast.makeText(context, s, Toast.LENGTH_LONG).show();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (s != null){
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();

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

    public boolean upDateKidsStatusD(Kids objKids, Context context){
        //TODO: Update Kids Status
        Call<ResponseBody> call = ApiClient
                .getInstance()
                .getApiR()
                .updatestatusdesembarque(objKids.getIdKids(), objKids.getStatus());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String s = null;

                try {
                    if (response.code() == 200) {

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("data_changed1"));

                    }else {

                        s = response.errorBody().string();
                        Toast.makeText(context, s, Toast.LENGTH_LONG).show();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (s != null){
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();

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

    public boolean upDateKidsStatusF(Kids objKids, Context context){
        //TODO: Update Kids Status
        Call<ResponseBody> call = ApiClient
                .getInstance()
                .getApiR()
                .updatestatus(objKids.getIdKids(), objKids.getStatus());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String s = null;

                try {
                    if (response.code() == 200) {

                        s = response.body().string();
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("data_changed1"));

                    }else {

                        s = response.errorBody().string();
                        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                        Log.d("Erro", s);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (s != null){
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();

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

    //TODO: Deletar

    public boolean deletarKidsdaRota(Kids objKids, Context context){

        Call<ResponseBody> call = ApiClient
                .getInstance()
                .getApiR()
                .deletarRotas(String.valueOf(objKids.getIdKids()));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String s = null;

                try {
                    if (response.code() == 201) {
                        s = response.body().string();
                        resp = true;
                    }else {
                        s = response.errorBody().string();
                        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                        resp = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (s != null){
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        resp = true;

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

        return resp;
    }

    public boolean deletarRotas(Rota objRota, Context context){

        Call<ResponseBody> call = ApiClient
                .getInstance()
                .getApiR()
                .deletarRotas(objRota.getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String s = null;

                try {
                    if (response.code() == 201) {
                        s = response.body().string();
                        resp = true;
                    }else {
                        s = response.errorBody().string();
                        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                        resp = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (s != null){
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        resp = true;

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

        return resp;
    }

}
