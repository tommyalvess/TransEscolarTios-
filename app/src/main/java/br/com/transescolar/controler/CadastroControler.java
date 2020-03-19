package br.com.transescolar.controler;

import android.text.TextUtils;

import java.util.regex.Pattern;

import br.com.transescolar.datasource.DataSourceCadastro;
import br.com.transescolar.model.Tios;

public class CadastroControler extends DataSourceCadastro {

    private Tios tios;
    boolean resp;
    public CadastroControler(){
        this.tios = new Tios();
    }

    public boolean salvarTios(Tios objTios){
        this.tios = objTios;
        if(inserirDados(objTios) == true){
            resp = true;
        }else {
            resp = false;
        }
        return resp;
    }

    public static boolean isValidPassword(String s) {
        //TODO: Validando a senha
        Pattern PASSWORD_PATTERN = Pattern.compile("[a-zA-Z0-9\\!\\@\\#\\$]{8,24}");
        return !TextUtils.isEmpty(s) && PASSWORD_PATTERN.matcher(s).matches();
    }

}
