package com.angkorteam.mbaas.plain.response.oauth2;

import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 3/30/16.
 */
public class OAuth2TokenResponse extends Response<String> {

    @Expose
    @SerializedName("scope")
    private String scope;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
