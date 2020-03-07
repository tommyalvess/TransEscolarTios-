package br.com.transescolar.controler;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import br.com.transescolar.API.ApiClient;
import br.com.transescolar.API.IKids;
import br.com.transescolar.Adapter.KidsAdpter;
import br.com.transescolar.R;
import br.com.transescolar.datasource.DataSourceKids;
import br.com.transescolar.model.Kids;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.transescolar.Activies.PassageirosActivity.progressBarPass;
import static br.com.transescolar.Activies.PassageirosActivity.recyclerView;
import static br.com.transescolar.Activies.PassageirosActivity.relativeLayoutPass;
import static br.com.transescolar.Activies.PassageirosActivity.snackbar;
import static br.com.transescolar.controler.HomeControler.showSnackbar;

public class PassageirosControler extends DataSourceKids {

    private List<Kids> kids;
    private KidsAdpter kidsAdpter;
    private IKids iKids;
    private Kids passageiros;

    public PassageirosControler() {
        this.passageiros = new Kids();
    }

    public void readKids(String type, int id, final Context context){
        fetchKid(type,id, context);
    }

    public void readKidsBy(String type, String key, int id, final Context context){
        fetchAllKid(type, key, id, context);
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
