package br.com.transescolar.controler;

import android.content.Context;

import br.com.transescolar.datasource.DataSourceLogin;
import br.com.transescolar.model.Tios;

public class TioControler extends DataSourceLogin {

    private Tios tio;

    public TioControler() {
        this.tio = new Tios();
    }

    public void logarTio(Tios objTio, Context context){
        this.tio = objTio;
        sessionTio(objTio, context);
    }
}
