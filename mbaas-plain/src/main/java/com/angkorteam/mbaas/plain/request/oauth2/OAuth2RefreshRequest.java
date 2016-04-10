package com.angkorteam.mbaas.plain.request.oauth2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 4/1/16.
 */
public class OAuth2RefreshRequest {

    @Expose
    @SerializedName("refresh_token")
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
