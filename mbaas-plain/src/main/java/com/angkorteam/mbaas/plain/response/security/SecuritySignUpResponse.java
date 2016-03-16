package com.angkorteam.mbaas.plain.response.security;

import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 2/16/16.
 */
public class SecuritySignUpResponse extends Response<SecuritySignUpResponse.Body> {

    public SecuritySignUpResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("userId")
        private String userId;

        @Expose
        @SerializedName("login")
        private String login;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }
    }

}
