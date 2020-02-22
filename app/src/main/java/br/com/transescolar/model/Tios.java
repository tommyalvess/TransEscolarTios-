package br.com.transescolar.model;

public class Tios {
    private String nome, email, cpf, apelido, placa, tell, senha;
    private int id;

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
}
