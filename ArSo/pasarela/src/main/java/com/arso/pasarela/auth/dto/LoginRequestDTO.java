package com.arso.pasarela.auth.dto;

import javax.validation.constraints.NotBlank;

public class LoginRequestDTO {

    @NotBlank(message = "email es obligatorio")
    private String email;

    @NotBlank(message = "password es obligatorio")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
