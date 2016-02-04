package com.angkorteam.mbaas.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by socheat on 2/4/16.
 */
public class LoginRequest implements Serializable {

    @Expose
    @SerializedName("username")
    private String username;

    @Expose
    @SerializedName("password")
    private String password;

    @Expose
    @SerializedName("appcode")
    private String appCode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }
}
