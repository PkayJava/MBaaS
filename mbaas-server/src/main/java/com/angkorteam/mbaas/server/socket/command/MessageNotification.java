package com.angkorteam.mbaas.server.socket.command;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 5/14/16.
 */
public class MessageNotification {

    @Expose
    @SerializedName("userId")
    private String userId;

    @Expose
    @SerializedName("message")
    private String message;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
