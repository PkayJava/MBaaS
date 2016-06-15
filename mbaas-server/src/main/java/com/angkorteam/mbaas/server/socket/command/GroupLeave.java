package com.angkorteam.mbaas.server.socket.command;

import com.angkorteam.mbaas.server.socket.Command;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 5/14/16.
 */
public class GroupLeave extends Command {

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
