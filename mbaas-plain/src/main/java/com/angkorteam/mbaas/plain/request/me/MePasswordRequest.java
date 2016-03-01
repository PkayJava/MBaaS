package com.angkorteam.mbaas.plain.request.me;

import com.angkorteam.mbaas.plain.request.Request;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khauv Socheat on 2/12/2016.
 */
public class MePasswordRequest extends Request {

    @Expose
    @SerializedName("old")
    private String oldPassword;
    @Expose
    @SerializedName("new")
    private String newPassword;

    public MePasswordRequest() {
    }

    public String getOldPassword() {
        return this.oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
