package com.angkorteam.mbaas.plain.request.otp;

import com.angkorteam.mbaas.plain.request.Request;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 4/3/16.
 */
public class OtpRequest extends Request {

    @Expose
    @SerializedName("secret")
    private String secret;

    @Expose
    @SerializedName("otp")
    private String otp;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
