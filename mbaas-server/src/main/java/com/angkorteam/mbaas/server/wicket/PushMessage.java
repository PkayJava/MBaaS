package com.angkorteam.mbaas.server.wicket;

import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.protocol.ws.api.message.TextMessage;

import java.io.Serializable;

/**
 * Created by socheat on 7/15/16.
 */
public class PushMessage extends TextMessage implements IWebSocketPushMessage, Serializable {

    public PushMessage(CharSequence text) {
        super(text);
    }

}
