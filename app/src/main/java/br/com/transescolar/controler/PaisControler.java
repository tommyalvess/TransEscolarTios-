package br.com.transescolar.controler;

import android.content.Context;

import br.com.transescolar.datasource.DataSourcePais;
import br.com.transescolar.model.Pais;

public class PaisControler extends DataSourcePais {

    Pais pais;

    public PaisControler() {
        this.pais = new Pais();
    }

    public void readPais(String users, String id, Context context){
        fetchAllPais(users, id,context);
    }

    public void readPaisBy(String users, String query, String id, Context context){
        fetcPaisBy(users, query, id, context);
    }
}
