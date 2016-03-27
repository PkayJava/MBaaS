package com.angkorteam.mbaas.plain.response.security;

import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by socheat on 2/16/16.
 */
public class SecurityLoginResponse extends Response<SecurityLoginResponse.Body> {

    public SecurityLoginResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("bearer")
        private String bearer;

        @Expose
        @SerializedName("dateCreated")
        private Date dateCreated;

        @Expose
        @SerializedName("login")
        private String login;

        public String getBearer() {
            return bearer;
        }

        public void setBearer(String bearer) {
            this.bearer = bearer;
        }

        public Date getDateCreated() {
            return dateCreated;
        }

        public void setDateCreated(Date dateCreated) {
            this.dateCreated = dateCreated;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }
    }

}
