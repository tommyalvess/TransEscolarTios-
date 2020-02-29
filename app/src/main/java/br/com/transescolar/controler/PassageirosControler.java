package br.com.transescolar.controler;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.API.IKids;
import br.com.transescolar.Activies.PassageirosActivity;
import br.com.transescolar.Adapter.KidsAdpter;
import br.com.transescolar.R;
import br.com.transescolar.model.Kids;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.transescolar.Activies.PassageirosActivity.progressBarPass;
import static br.com.transescolar.Activies.PassageirosActivity.relativeLayoutPass;
import static br.com.transescolar.Activies.PassageirosActivity.snackbar;
import static br.com.transescolar.controler.HomeControler.showSnackbar;

public class PassageirosControler {

    private RecyclerView recyclerView;
    private List<Kids> kids;
    private KidsAdpter kidsAdpter;
    private IKids iKids;
    private Kids passageiros;

    public PassageirosControler() {
        this.passageiros = new Kids();
    }

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

    public static void dialogPas(boolean value, final Context context){

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
            snackbar = showSnackbar(relativeLayoutPass, Snackbar.LENGTH_INDEFINITE, context);
            snackbar.show();
            snackbar.dismiss();
            View view = snackbar.getView();
            TextView tv = view.findViewById(R.id.textSnack);
            tv.setText("Sem conexão a internet!");
        }
    }
}
