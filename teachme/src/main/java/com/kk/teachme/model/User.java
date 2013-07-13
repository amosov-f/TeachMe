package com.kk.teachme.model;

public class User {

    public User(int id, String login) {
        this.id = id;
        this.login = login;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    private int id;
    private String login;

}
