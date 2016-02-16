package com.angkorteam.mbaas.plain.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by socheat on 2/16/16.
 */
public class SecuritySignUpResponse extends Response<SecuritySignUpResponse.Body> {

    public SecuritySignUpResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("session")
        private String session;

        @Expose
        @SerializedName("dateCreated")
        private Date dateCreated;

        @Expose
        @SerializedName("login")
        private String login;

        public String getSession() {
            return session;
        }

        public void setSession(String session) {
            this.session = session;
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
