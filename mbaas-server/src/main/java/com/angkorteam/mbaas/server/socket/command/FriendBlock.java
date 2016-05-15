package com.angkorteam.mbaas.server.socket.command;

import com.angkorteam.mbaas.server.socket.Command;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 5/14/16.
 */
public class FriendBlock extends Command {

    @Expose
    @SerializedName("userId")
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
