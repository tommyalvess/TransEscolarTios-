package br.com.transescolar.datasource;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.Activies.CadastroTioActivity;
import br.com.transescolar.Activies.LoginActivity;
import br.com.transescolar.R;
import br.com.transescolar.model.Tios;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataSourceCadastro {

    private Context context;

    public boolean inserirDados(Tios objTios){
        //TODO: Adicionar Tio

        Call<ResponseBody> call = ApiClient
                .getInstance()
                .getApi()
                .createuser(objTios.getNome(), objTios.getEmail(), objTios.getCpf(), objTios.getApelido(),
                        objTios.getPlaca(), objTios.getTell(), objTios.getSenha());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String s = null;

                try {
                    if (response.code() == 201) {

                        s = response.body().string();
                        Intent intent = new Intent(context, LoginActivity.class);
                        DataSourceCadastro.this.context.startActivity(intent);

                    }else {
                        s = response.errorBody().string();
                        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                        Log.e("Chamada", s);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (s != null){
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

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

}
