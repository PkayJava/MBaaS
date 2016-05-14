package com.angkorteam.mbaas.server.socket.command;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by socheat on 5/14/16.
 */
public class GroupLeave {

    @Expose
    @SerializedName("conversationId")
    private String conversationId;

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
