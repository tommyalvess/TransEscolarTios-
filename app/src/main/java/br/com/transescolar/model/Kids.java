package br.com.transescolar.model;

import android.widget.TextView;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Kids implements Serializable {

    @SerializedName("idKids") private int idKids;
    @SerializedName("idTios") private int idTios;
    @SerializedName("nome") private String nome;
    @SerializedName("periodo") private String periodo;
    @SerializedName("img") private String img;
    @SerializedName("nm_escola") private String nm_escola;
    @SerializedName("dt_nas") private String dt_nas;
    @SerializedName("end_principal") private String end_principal;
    @SerializedName("nm_pais") private String nm_pais;
    @SerializedName("status") private String status;
    @SerializedName("embarque") private String embarque;
    @SerializedName("desembarque") private String desembarque;

    public Kids() {
    }

    public Kids(String nome, String periodo, String escola, String endereco, String pais, String dtnasc, String img, String status) {
        this.nome = nome;
        this.periodo = periodo;
        this.nm_escola = escola;
        this.dt_nas = dtnasc;
        this.end_principal = endereco;
        this.nm_pais = pais;
        this.img = img;
        this.status = status;
    }

    public Kids(int id, String nome, String periodo, String nm_escola, String dt_nas, String end_principal, String nm_pais, String status) {
        this.nome = nome;
        this.periodo = periodo;
        this.nm_escola = nm_escola;
        this.dt_nas = dt_nas;
        this.end_principal = end_principal;
        this.nm_pais = nm_pais;
        this.status = status;
    }

    public Kids(int idKids, String nome, String periodo, String nm_escola) {
        this.idKids = idKids;
        this.nome = nome;
        this.periodo = periodo;
        this.nm_escola = nm_escola;
    }

    public Kids(int idKids, int idTios, String nome, String periodo, String img, String nm_escola, String dt_nas, String end_principal, String nm_pais, String status) {
        this.idKids = idKids;
        this.idTios = idTios;
        this.nome = nome;
        this.periodo = periodo;
        this.img = img;
        this.nm_escola = nm_escola;
        this.dt_nas = dt_nas;
        this.end_principal = end_principal;
        this.nm_pais = nm_pais;
        this.status = status;

    }

    public Kids(int idTios, String nome, String periodo, String img, String nm_escola, String dt_nas, String end_principal, String nm_pais, String status) {
        this.idTios = idTios;
        this.nome = nome;
        this.periodo = periodo;
        this.img = img;
        this.nm_escola = nm_escola;
        this.dt_nas = dt_nas;
        this.end_principal = end_principal;
        this.nm_pais = nm_pais;
        this.status = status;

    }

    public Kids(TextView nome, TextView periodo, TextView nm_escola) {
        this.nome = String.valueOf(nome);
        this.periodo = String.valueOf(periodo);
        this.nm_escola = String.valueOf(nm_escola);
    }

    public Kids(String nome, String escola, String periodo, String endereco, String dtnasc, String img) {
        this.nome = nome;
        this.nm_escola = escola;
        this.periodo = periodo;
        this.end_principal = endereco;
        this.dt_nas = dtnasc;
        this.img = img;

    }

    public Kids(String nome, String escola, String periodo) {
        this.nome = nome;
        this.nm_escola = escola;
        this.periodo = periodo;
    }

    public Kids(String nome, String date) {
        this.nome = nome;
        this.dt_nas = date;
    }

    public Kids(TextView nome) {
        this.nome = String.valueOf(nome);
    }


    public int getIdKids() {
        return idKids;
    }

    public void setIdKids(int idKids) {
        this.idKids = idKids;
    }

    public int getIdTios() {
        return idTios;
    }

    public void setIdTios(int idTios) {
        this.idTios = idTios;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmbarque() {
        return embarque;
    }

    public void setEmbarque(String embarque) {
        this.embarque = embarque;
    }

    public String getDesembarque() {
        return desembarque;
    }

    public void setDesembarque(String desembarque) {
        this.desembarque = desembarque;
    }

    public String toString(){
        return "Nome: " + this.nome + "\n" + "Escola: " + this.nm_escola + "\n" + "Periodo: " + this.periodo;
    }

}

