package br.com.transescolar.controler;

import android.content.Context;

import br.com.transescolar.datasource.DataSourceTio;
import br.com.transescolar.model.Tios;
import java.util.List;


public class TioControler extends DataSourceTio {

    private Tios tio;

    public TioControler() {
        this.tio = new Tios();
    }

    public void logarTio(Tios objTio, Context context){
        this.tio = objTio;
        sessionTio(objTio, context);
    }

}
