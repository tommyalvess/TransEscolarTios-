package br.com.transescolar.controler;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.daimajia.swipe.util.Attributes;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.API.IRota;
import br.com.transescolar.Adapter.RotaAdapter;
import br.com.transescolar.R;
import br.com.transescolar.datasource.DataSourceRota;
import br.com.transescolar.model.Kids;
import br.com.transescolar.model.Rota;
import br.com.transescolar.model.Tios;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.transescolar.Activies.RotaActivity.constraintLayoutRota;
import static br.com.transescolar.Activies.RotaActivity.progressBarR;
import static br.com.transescolar.Activies.RotaActivity.snackbar;
import static br.com.transescolar.Activies.RotaActivity.recyclerView;
import static br.com.transescolar.controler.HomeControler.showSnackbarC;


public class RotaControler extends DataSourceRota {

    private Rota rota;
    private Tios tios;
    private Kids kids;
    private List<Rota> contacts;
    private RotaAdapter adapter;
    private IRota apiInterface;

    public RotaControler() {
        this.rota = new Rota();
    }

    public void salvarRota(Rota objRota, Tios objTios, Context context){
        this.rota = objRota;
        this.tios = objTios;
        inserirDadosRota(objRota, objTios, context);
    }

    public void salvarKidsnaRota(Rota objRota, Kids objKids, Context context){
        this.kids = objKids;
        this.rota = objRota;
        inserirKidnaRota(objRota, objKids, context);

    }

    public void readRota(String type, String key, String id, final Context context){
        fetchRotas(type, key, id, context);
    }
    //TODO: Dialogo de conexão com a net
    public static void dialogR(boolean value, final Context context){

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
            snackbar = showSnackbarC(constraintLayoutRota, Snackbar.LENGTH_INDEFINITE, context);
            snackbar.show();
            snackbar.dismiss();
            View view = snackbar.getView();
            TextView tv = view.findViewById(R.id.textSnack);
            tv.setText("Sem conexão a internet!");
        }
    }

}
