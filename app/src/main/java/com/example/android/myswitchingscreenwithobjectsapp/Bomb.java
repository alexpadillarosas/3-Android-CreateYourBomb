package com.example.android.myswitchingscreenwithobjectsapp;


import java.io.Serializable;

public class Bomb implements Serializable {

    //This constant is used to identify the bomb instance to pass between activities.
    public static final String BOMB_KEY = "booom";

    private String name;
    private Integer resId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }
}
