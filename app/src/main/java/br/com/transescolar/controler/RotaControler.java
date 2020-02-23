package br.com.transescolar.controler;
import br.com.transescolar.datasource.DataSourceRota;
import br.com.transescolar.model.Rota;


public class RotaControler extends DataSourceRota {

    private Rota rota;

    public RotaControler() {
        this.rota = new Rota();
    }

    public void salvarRota(Rota objRota){
        this.rota = objRota;
        inserirDadosRota(objRota);

    }

}
