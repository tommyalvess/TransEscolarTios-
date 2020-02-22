package br.com.transescolar.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Rota implements Serializable {
    @SerializedName("idRota") int id;
    @SerializedName("nm_rota") String nm_rota;
    @SerializedName("hora") String hora;
    @SerializedName("dias") String dias;
    @SerializedName("idTios") int idTio;

    public Rota(int id, String nm_rota, String hora, String dias, int idTio) {
        this.id = id;
        this.nm_rota = nm_rota;
        this.hora = hora;
        this.dias = dias;
        this.idTio = idTio;
    }

    public Rota() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNm_rota() {
        return nm_rota;
    }

    public void setNm_rota(String nm_rota) {
        this.nm_rota = nm_rota;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public int getIdTio() {
        return idTio;
    }

    public void setIdTio(int idTio) {
        this.idTio = idTio;
    }
}
