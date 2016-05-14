package com.angkorteam.mbaas.server.socket.command;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by socheat on 5/14/16.
 */
public class GroupInvite {

    @Expose
    @SerializedName("conversationId")
    private String conversationId;

    @Expose
    @SerializedName("userId")
    private List<String> userId;

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public List<String> getUserId() {
        return userId;
    }

    public void setUserId(List<String> userId) {
        this.userId = userId;
    }
}
