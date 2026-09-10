package com.arso.pasarela.adapter.retrofit;

public class GithubLoginRequestDTO {
    private String githubId;
    private String email;

    public GithubLoginRequestDTO() {
    }

    public GithubLoginRequestDTO(String githubId, String email) {
        this.githubId = githubId;
        this.email = email;
    }

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
