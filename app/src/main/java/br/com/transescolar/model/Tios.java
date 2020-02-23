package br.com.transescolar.model;

public class Tios {
    private String nome, email, cpf, apelido, placa, tell, senha;
    private int id;

    public Tios() {
    }

    public Tios(int id, String nome, String email, String cpf, String apelido, String placa, String tell, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.apelido = apelido;
        this.placa = placa;
        this.tell = tell;
        this.senha = senha;

    }

    public Tios(int id, String nome, String email, String cpf, String apelido, String placa, String tell) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.apelido = apelido;
        this.placa = placa;
        this.tell = tell;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public String getApelido() {
        return apelido;
    }

    public String getPlaca() {
        return placa;
    }

    public String getTell() {
        return tell;
    }

    public String getSenha() {
        return senha;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setTell(String tell) {
        this.tell = tell;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setId(int id) {
        this.id = id;
    }
}
