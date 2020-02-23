package br.com.transescolar.controler;

import java.util.HashMap;

import br.com.transescolar.Conexao.SessionManager;

public class TioControler {
    SessionManager sessionManager;

    public String pegarIdTio(){

        HashMap<String, String> user = sessionManager.getUserDetail();
        String getId = user.get(sessionManager.ID);

        return getId;
    }
}
