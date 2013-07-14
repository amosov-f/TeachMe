package com.kk.teachme.model;

import java.util.Map;

public class User {

    private int id;
    private String login;

    public User(int id, String login) {
        this.id = id;
        this.login = login;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

}
