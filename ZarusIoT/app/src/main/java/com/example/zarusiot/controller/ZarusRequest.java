package com.example.zarusiot.controller;

public class ZarusRequest {
    private final String URL = "zarus.dev";
    private final String AUTH_PATH = "/auth";
    private final String VERSION_PATH = "/ver";

    public ZarusRequest() {
    }

    public String verifyLogin(String username, String password){
        return java.util.UUID.randomUUID().toString();
    }

    public boolean registerAccount(){
        return true;
    }
}

