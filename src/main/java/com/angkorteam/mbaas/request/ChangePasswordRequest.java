package com.angkorteam.mbaas.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class ChangePasswordRequest extends Request {

    @Expose
    @SerializedName("old")
    private String oldPassword;

    @Expose
    @SerializedName("new")
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
