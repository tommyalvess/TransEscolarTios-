package br.com.transescolar.Classes;

import br.com.transescolar.model.Tios;

public class LoginResponse {

    private boolean error;
    private String message;
    private Tios user;

    public LoginResponse(boolean error, String message, Tios user) {
        this.error = error;
        this.message = message;
        this.user = user;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Tios getUser() {
        return user;
    }
}
