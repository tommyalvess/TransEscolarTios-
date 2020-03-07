package br.com.transescolar.controler;

import android.content.Context;

import br.com.transescolar.datasource.DataSourceKids;
import br.com.transescolar.model.Kids;
import br.com.transescolar.model.Tios;

public class KidsControler extends DataSourceKids {

    Kids kids;

    public KidsControler() {
        this.kids = new Kids();
    }

    public void salvarKids(Kids objKids, Context context){
        this.kids = objKids;
        inserirKids(objKids, context);
    }

}
