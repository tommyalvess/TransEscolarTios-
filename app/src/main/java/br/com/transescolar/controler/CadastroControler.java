package br.com.transescolar.controler;

import android.text.TextUtils;

import java.util.regex.Pattern;

import br.com.transescolar.datasource.DataSourceCadastro;
import br.com.transescolar.model.Tios;

public class CadastroControler extends DataSourceCadastro {

    private Tios tios;

    public CadastroControler(){
        this.tios = new Tios();
    }

    public void salvarTios(Tios objTios){
        this.tios = objTios;
        inserirDados(objTios);
    }

    public static boolean isValidPassword(String s) {
        //TODO: Validando a senha
        Pattern PASSWORD_PATTERN = Pattern.compile("[a-zA-Z0-9\\!\\@\\#\\$]{8,24}");
        return !TextUtils.isEmpty(s) && PASSWORD_PATTERN.matcher(s).matches();
    }

}
