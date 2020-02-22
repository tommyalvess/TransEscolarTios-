package br.com.transescolar.Conexao;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import br.com.transescolar.Activies.HomeActivity;
import br.com.transescolar.Activies.LoginActivity;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String CPF = "CPF";
    public static final String APELIDO = "APELIDO";
    public static final String PLACA = "PLACA";
    public static final String TELL = "TELL";
    public static final String IMG = "IMG";
    public static final String ID = "ID";

    public static final String DATE = "dt_nas";
    public static final String ENDERECO = "end_principal";
    public static final String PERIODO = "periodo";
    public static final String IDT = "idTios";
    public static final String IDE = "idEscola";
    public static final String IDP = "idPais";
    public static final String IDROTA = "idRota";
    public static final String STATUS = "status";


    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String id, String nome, String email, String cpf, String apelido, String placa, String tell, String img){

        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, nome);
        editor.putString(EMAIL, email);
        editor.putString(CPF, cpf);
        editor.putString(APELIDO, apelido);
        editor.putString(PLACA, placa);
        editor.putString(TELL, tell);
        editor.putString(IMG, img);
        editor.putString(ID, id);
        editor.apply();

    }

    public void createSession(String id, String nome, String email, String cpf, String apelido, String placa, String tell){
        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, nome);
        editor.putString(EMAIL, email);
        editor.putString(CPF, cpf);
        editor.putString(APELIDO, apelido);
        editor.putString(PLACA, placa);
        editor.putString(TELL, tell);
        editor.putString(ID, id);
        editor.apply();

    }

    public void createSessionSenha(String id, String nome, String email, String cpf, String apelido, String placa, String tell, String img){
        editor.putString(NAME, nome);
        editor.putString(EMAIL, email);
        editor.putString(CPF, cpf);
        editor.putString(APELIDO, apelido);
        editor.putString(PLACA, placa);
        editor.putString(TELL, tell);
        editor.putString(IMG, img);
        editor.putString(ID, id);
        editor.apply();

    }

    public void createSessionStatus(String nome){
        editor.putBoolean(LOGIN, true);
        editor.putString(STATUS, nome);
        editor.apply();

    }

    public void createSessionRota(String id){
        editor.putString(IDROTA, id);
        editor.apply();
    }


    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }


    public void checkLogin(){

        if (!this.isLoggin()){
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((HomeActivity) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail(){

        HashMap<String, String> user = new HashMap<>();
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        user.put(CPF, sharedPreferences.getString(CPF, null));
        user.put(APELIDO, sharedPreferences.getString(APELIDO, null));
        user.put(PLACA, sharedPreferences.getString(PLACA, null));
        user.put(TELL, sharedPreferences.getString(TELL, null));
        user.put(IMG, sharedPreferences.getString(IMG, null));
        user.put(ID, sharedPreferences.getString(ID, null));

        return user;
    }

    public HashMap<String, String> getIdRoto(){
        HashMap<String, String> rotas = new HashMap<>();
        rotas.put(IDROTA, sharedPreferences.getString(IDROTA, null));
        return rotas;
    }

    public HashMap<String, String> getStatus(){
        HashMap<String, String> status = new HashMap<>();
        status.put(STATUS, sharedPreferences.getString(STATUS, null));
        return status;
    }


    public boolean logout(){

//        editor.clear();
//        editor.commit();
//        Intent i = new Intent(context, LoginActivity.class);
//        context.startActivity(i);
//        ((HomeActivity) context).finish();

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }
}
