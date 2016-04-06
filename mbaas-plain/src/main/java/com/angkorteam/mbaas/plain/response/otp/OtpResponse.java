package com.angkorteam.mbaas.plain.response.otp;

import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 4/3/16.
 */
public class OtpResponse extends Response<OtpResponse.Body> {

    public OtpResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("result")
        private String hash;

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }
    }

}
