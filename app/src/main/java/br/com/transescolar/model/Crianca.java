package br.com.transescolar.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Crianca implements Serializable {
    @SerializedName("IdK") private int idKids;
    @SerializedName("Nome") private String nome;
    @SerializedName("Periodo") private String periodo;
    @SerializedName("Img") private String img;
    @SerializedName("Escola") private String nm_escola;
    @SerializedName("DtNas") private String dt_nas;
    @SerializedName("End") private String end_principal;
    @SerializedName("Pais") private String nm_pais;

    public Crianca(int idKids, String nome, String periodo, String img, String nm_escola, String dt_nas, String end_principal, String nm_pais) {
        this.idKids = idKids;
        this.nome = nome;
        this.periodo = periodo;
        this.img = img;
        this.nm_escola = nm_escola;
        this.dt_nas = dt_nas;
        this.end_principal = end_principal;
        this.nm_pais = nm_pais;
    }

    public int getIdKids() {
        return idKids;
    }

    public void setIdKids(int idKids) {
        this.idKids = idKids;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getNm_escola() {
        return nm_escola;
    }

    public void setNm_escola(String nm_escola) {
        this.nm_escola = nm_escola;
    }

    public String getDt_nas() {
        return dt_nas;
    }

    public void setDt_nas(String dt_nas) {
        this.dt_nas = dt_nas;
    }

    public String getEnd_principal() {
        return end_principal;
    }

    public void setEnd_principal(String end_principal) {
        this.end_principal = end_principal;
    }

    public String getNm_pais() {
        return nm_pais;
    }

    public void setNm_pais(String nm_pais) {
        this.nm_pais = nm_pais;
    }
}
