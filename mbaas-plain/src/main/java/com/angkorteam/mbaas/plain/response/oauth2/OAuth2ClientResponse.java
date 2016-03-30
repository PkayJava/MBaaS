package com.angkorteam.mbaas.plain.response.oauth2;

import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 3/30/16.
 */
public class OAuth2ClientResponse extends Response<String> {

    @Expose
    @SerializedName("access_token")
    private String accessToken;

    @Expose
    @SerializedName("token_type")
    private String tokenType;

    @Expose
    @SerializedName("expires_in")
    private Integer expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }
}
