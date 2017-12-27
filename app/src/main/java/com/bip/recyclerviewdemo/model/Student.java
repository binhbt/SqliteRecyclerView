package com.bip.recyclerviewdemo.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by t430 on 12/13/2017.
 */
//Model
public class Student {
    private int id;

    @SerializedName("login")
    private String name;

    @SerializedName("avatar_url")
    private String avatar;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
