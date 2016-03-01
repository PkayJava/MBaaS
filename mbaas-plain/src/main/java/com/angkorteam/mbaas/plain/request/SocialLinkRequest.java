package com.angkorteam.mbaas.plain.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class SocialLinkRequest extends Request {

    @Expose
    @SerializedName("oauth_token")
    private String token;

    @Expose
    @SerializedName("oauth_secret")
    private String secret;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
