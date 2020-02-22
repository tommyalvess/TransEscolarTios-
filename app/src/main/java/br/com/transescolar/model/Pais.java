package br.com.transescolar.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Pais implements Serializable {

    @SerializedName("idPais") private int idPais;
    @SerializedName("nm_pai") private String nome;
    @SerializedName("email") private String email;
    @SerializedName("tell") private String tell;
    @SerializedName("cpf") private String cpf;
    @SerializedName("img") private String img;
    @SerializedName("status") private String status;

    public Pais(int idPais, String nome, String email, String tell, String cpf, String img, String status) {
        this.idPais = idPais;
        this.nome = nome;
        this.email = email;
        this.tell = tell;
        this.cpf = cpf;
        this.img = img;
        this.status = status;
    }

    public int getIdPais() {
        return idPais;
    }

    public void setIdPais(int idPais) {
        this.idPais = idPais;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTell() {
        return tell;
    }

    public void setTell(String tell) {
        this.tell = tell;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
