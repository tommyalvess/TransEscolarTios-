package br.com.transescolar.controler;

import br.com.transescolar.datasource.DataSourceTios;
import br.com.transescolar.model.Tios;

public class CadastroControler extends DataSourceTios {

    private Tios tios;

    public CadastroControler(){
        this.tios = new Tios();
    }

    public void salvarTios(Tios objTios){
        this.tios = objTios;
        inserirDados(objTios);
    }

}
