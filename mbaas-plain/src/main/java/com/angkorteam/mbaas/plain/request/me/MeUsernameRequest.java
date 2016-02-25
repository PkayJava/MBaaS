package com.angkorteam.mbaas.plain.request.me;

import com.angkorteam.mbaas.plain.request.Request;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 2/25/16.
 */
public class MeUsernameRequest extends Request {

    @Expose
    @SerializedName("username")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
